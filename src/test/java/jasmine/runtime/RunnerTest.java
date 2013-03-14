package jasmine.runtime;

import jasmine.rhino.RhinoContext;
import jasmine.runtime.rhino.RhinoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RunnerTest {
    @Mock Notifier notifier;

    @Test
    public void shouldReportNoSpecsToRun() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [];}}; object;"
        );

        RhinoRunner runner = new RhinoRunner(object, context);

        runner.execute(notifier);

        verify(notifier).nothingToRun();
    }
}
