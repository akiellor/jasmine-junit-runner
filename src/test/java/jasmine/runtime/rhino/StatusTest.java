package jasmine.runtime.rhino;

import jasmine.runtime.Failure;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import jasmine.runtime.rhino.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StatusTest {
    @Mock
    It it;
    @Mock
    Notifier notifier;
    @Mock
    Failure failure;

    @Test
    public void shouldNotifyWithFailWhenFailed() {
        new Status.Failed(it, failure).notify(notifier);

        verify(notifier).fail(it, failure);
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
