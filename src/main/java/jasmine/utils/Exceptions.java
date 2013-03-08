package jasmine.utils;

public class Exceptions {
    public static RuntimeException unchecked(Throwable throwable){
        if(throwable instanceof RuntimeException){
            return (RuntimeException)throwable;
        }else{
            return new RuntimeException(throwable);
        }
    }
}
