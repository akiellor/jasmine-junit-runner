package jasmine.runtime.webdriver;

import jasmine.utils.Exceptions;

import java.util.concurrent.atomic.AtomicReference;

public class Lifecycle {
    public enum State{
        Uninitialized,
        Initialized,
        Ready,
        Complete
    }

    private final AtomicReference<State> state;

    public void await(final State state) {
        while(!this.state.get().equals(state)){
            synchronized (this){
                try {
                    this.wait(5000l);
                } catch (InterruptedException e) {
                    throw Exceptions.unchecked(e);
                }
            }
        }
    }

    public Lifecycle(){
        state = new AtomicReference<State>(State.Uninitialized);
    }

    public void set(final State state){
        this.state.set(state);
        synchronized (this){
            this.notify();
        }
    }
}
