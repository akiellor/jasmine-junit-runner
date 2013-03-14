package jasmine.runtime;

import jasmine.runtime.rhino.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StatusTest {
    @Mock It it;
    @Mock Notifier notifier;
    @Mock Exception exception;

    @Test
    public void shouldNotifyWithFailWhenFailed() {
        new Status.Failed(it, exception).notify(notifier);

        verify(notifier).fail(it, exception);
    }

    @Test
    public void shouldNotifyWithPassWhenPassed() {
        new Status.Passed(it).notify(notifier);

        verify(notifier).pass(it);
    }

    @Test
    public void shouldNotifyWithIgnoredWhenSkipped() {
        new Status.Skipped(it).notify(notifier);

        verify(notifier).skipped(it);
    }
}
