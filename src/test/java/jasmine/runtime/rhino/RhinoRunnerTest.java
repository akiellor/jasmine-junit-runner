package jasmine.runtime.rhino;

import com.google.common.util.concurrent.Futures;
import jasmine.runtime.Notifier;
import jasmine.runtime.rhino.RhinoContext;
import jasmine.runtime.rhino.RhinoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RhinoRunnerTest {
    @Mock Notifier notifier;
    @Mock ExecutorService executorService;
    @Mock RhinoIt it;

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

    @Test
    public void shouldShutdownExecutor() throws InterruptedException {
        when(executorService.submit(Mockito.<Callable<RhinoIt>>any())).thenReturn(Futures.immediateFuture(it));

        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD IT'}]; }, suites: function(){ return [{description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return [{id: 1, suite: {id: 2}, description: 'GRANDCHILD IT'}]; }}]}}; object;"
        );

        RhinoRunner runner = new RhinoRunner(object, context, executorService);

        runner.execute(notifier);

        verify(executorService).awaitTermination(1L, TimeUnit.SECONDS);
        verify(executorService).shutdown();
    }
}
