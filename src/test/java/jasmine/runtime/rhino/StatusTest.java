package jasmine.runtime.rhino;

import jasmine.runtime.Notifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StatusTest {
    @Mock RhinoIt rhinoIt;
    @Mock Notifier notifier;

    @Test
    public void shouldNotifyWithFailWhenFailed() {
        Status.FAILED.notify(notifier, rhinoIt);

        verify(notifier).fail(rhinoIt);
    }

    @Test
    public void shouldNotifyWithPassWhenPassed() {
        Status.PASSED.notify(notifier, rhinoIt);

        verify(notifier).pass(rhinoIt);
    }

    @Test
    public void shouldNotifyWithIgnoredWhenSkipped() {
        Status.SKIPPED.notify(notifier, rhinoIt);

        verify(notifier).skipped(rhinoIt);
    }
}
