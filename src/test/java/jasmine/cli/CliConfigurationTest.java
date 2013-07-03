package jasmine.cli;

import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;

import static org.fest.assertions.Assertions.assertThat;

public class CliConfigurationTest {
    @Test
    public void shouldDefaultToRunAllSpecs() {
        CliConfiguration configuration = new CliConfiguration();

        assertThat(configuration.specs()).containsOnly(".*Spec.js$");
    }

    @Test
    public void shouldDefaultJavascriptToCurrentDirectory() throws MalformedURLException {
        CliConfiguration configuration = new CliConfiguration();

        assertThat(configuration.getJavascriptPath()).containsOnly(new File("").getAbsoluteFile().toURI().toURL().toExternalForm());
    }

    @Test
    public void shouldTreatTrailingArgumentsAsFilesToRun() {
        CliConfiguration configuration = new CliConfiguration("Spec.js$", "foo/bar.js");

        assertThat(configuration.specs()).containsOnly("Spec.js$", "foo/bar.js");
    }

    @Test
    public void shouldSpecifyTheJavascriptPath() {
        CliConfiguration configuration = new CliConfiguration("-p", "foo:bar");

        assertThat(configuration.getJavascriptPath()).containsOnly("foo", "bar");
    }
}
