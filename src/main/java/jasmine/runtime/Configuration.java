package jasmine.runtime;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface Configuration {
    Collection<String> sources();

    Collection<String> specs();

    File htmlRunnerOutputDir();

    boolean debug();

    boolean envJs();

    List<String> getJavascriptPath();
}
