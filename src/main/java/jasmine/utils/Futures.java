package jasmine.utils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Futures {
    public static <T> Iterable<T> await(Iterable<Future<T>> futures) {
        return ImmutableList.copyOf(Iterables.transform(futures, new Function<Future<T>, T>() {
            @Override public T apply(Future<T> input) {
                try {
                    return input.get();
                } catch (InterruptedException e) {
                    throw Exceptions.unchecked(e);
                } catch (ExecutionException e) {
                    throw Exceptions.unchecked(e);
                }
            }
        }));
    }
}
