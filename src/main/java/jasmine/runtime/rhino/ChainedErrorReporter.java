package jasmine.runtime.rhino;

import jasmine.utils.Exceptions;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.tools.ToolErrorReporter;

import java.util.ArrayList;
import java.util.List;

class ChainedErrorReporter implements ErrorReporter {

    private List<ErrorReporter> chainedReporters = new ArrayList<ErrorReporter>();

    ChainedErrorReporter(ErrorReporter chainedDefaultReporter) {
        chainedReporters.add(chainedDefaultReporter);
        chainedReporters.add(new ToolErrorReporter(true, System.err));
    }

    @Override
    public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
        EvaluatorException ex = null;
        for (ErrorReporter reporter : chainedReporters) {
            try {
                reporter.error(message, sourceName, line, lineSource, lineOffset);
            } catch (EvaluatorException thrownByChainEx) {
                ex = thrownByChainEx;
            }
        }

        if (ex != null) {
            throw Exceptions.unchecked(ex);
        }
    }

    @Override
    public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
        EvaluatorException ex = null;
        for (ErrorReporter reporter : chainedReporters) {
            EvaluatorException returnedByChainEx = reporter.runtimeError(message, sourceName, line, lineSource, lineOffset);
            if (returnedByChainEx != null) {
                ex = returnedByChainEx;
            }
        }

        if (ex != null) {
            throw ex;
        }
        return null;
    }

    @Override
    public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
        for (ErrorReporter reporter : chainedReporters) {
            reporter.warning(message, sourceName, line, lineSource, lineOffset);
        }
    }

}
