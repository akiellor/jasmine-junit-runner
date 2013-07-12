package jasmine;

import org.fest.assertions.Assertions;
import org.fest.assertions.StringAssert;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceAsserts {
    public static StringAssert assertThat(Throwable exception) {
        return Assertions.assertThat(getStack(exception));
    }

    private static String getStack(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
