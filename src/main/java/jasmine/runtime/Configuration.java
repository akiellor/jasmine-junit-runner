package jasmine.runtime;

import java.util.Collection;
import java.util.List;

public interface Configuration {
    Collection<String> sources();

    Collection<String> specs();

    boolean debug();

    List<String> getJavascriptPath();
}
