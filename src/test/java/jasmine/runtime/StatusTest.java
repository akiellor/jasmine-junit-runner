package jasmine.runtime;

import jasmine.runtime.rhino.RhinoIt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StatusTest {
    @Mock RhinoIt it;
    @Mock Notifier notifier;

    @Test
    public void shouldNotifyWithFailWhenFailed() {
        Status.FAILED.notify(notifier, it);

        verify(notifier).fail(it, it.getFirstFailedStacktrace());
    }

    @Test
    public void shouldNotifyWithPassWhenPassed() {
        Status.PASSED.notify(notifier, it);

        verify(notifier).pass(it);
    }

    @Test
    public void shouldNotifyWithIgnoredWhenSkipped() {
        Status.SKIPPED.notify(notifier, it);

        verify(notifier).skipped(it);
    }
}
