package jasmine.runtime;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface Configuration {
    Collection<String> sources();

    Collection<String> specs();

    File htmlRunnerOutputDir();

    boolean generateSpecRunner();

    boolean debug();

    boolean envJs();

    String jsRootFile(String relativePath);

    List<String> getJavascriptPath();
}
