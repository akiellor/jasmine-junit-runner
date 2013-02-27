package be.klak.jasmine;

import java.io.File;
import java.util.Collection;

public interface Configuration {
    Collection<String> sources();

    Collection<String> specs();

    File htmlRunnerOutputDir();

    boolean generateSpecRunner();

    boolean debug();

    boolean envJs();

    String jsRootFile(String relativePath);
}
