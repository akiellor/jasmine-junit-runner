package jasmine.utils;

import com.google.common.collect.Lists;
import jasmine.runtime.utils.Futures;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class FuturesTest {
    @Mock Future<Object> first;
    @Mock Future<Object> second;

    @Test
    public void shouldAwaitAndReturnAllFutures() throws ExecutionException, InterruptedException {
        when(first.get()).thenReturn("1");
        when(second.get()).thenReturn("2");
        
        Iterable<Object> objects = Futures.await(Lists.<Future<Object>>newArrayList(first, second));

        verify(first, atMost(1)).get();
        verify(second, atMost(1)).get();
        assertThat(objects).containsOnly("1", "2");
    }

    @Test
    public void shouldFailWithInterruptedException() throws ExecutionException, InterruptedException {
        when(first.get()).thenReturn("1");
        when(second.get()).thenThrow(new InterruptedException());

        try{
            Futures.await(Lists.<Future<Object>>newArrayList(first, second));
            fail();
        }catch(Exception e){
            assertThat(e.getCause()).isExactlyInstanceOf(InterruptedException.class);
        }
    }

    @Test
    public void shouldFailWithExecutionException() throws ExecutionException, InterruptedException {
        when(first.get()).thenReturn("1");
        when(second.get()).thenThrow(new ExecutionException(new RuntimeException()));

        try{
            Futures.await(Lists.<Future<Object>>newArrayList(first, second));
            fail();
        }catch(Exception e){
            assertThat(e.getCause()).isExactlyInstanceOf(ExecutionException.class);
        }
    }
}
