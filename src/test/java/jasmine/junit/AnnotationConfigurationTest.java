package jasmine.junit;

import jasmine.runtime.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationConfigurationTest {
    @Mock JasmineSuite annotation;

    @Test
    public void shouldLoadSourcesAsFileResources() throws IOException {
        when(annotation.sourcesRootDir()).thenReturn("src/main/javascript");
        when(annotation.sources()).thenReturn(new String[]{"one.js", "two.js"});

        Configuration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.sources()).containsOnly(
                "src/main/javascript/one.js",
                "src/main/javascript/two.js");
    }

    @Test
    public void shouldLoadSpecsAsFileResources() throws IOException {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specs()).thenReturn(new String[]{"one.js", "two.js"});

        Configuration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.specs()).containsOnly(
                "src/test/javascript/specs/one.js",
                "src/test/javascript/specs/two.js");
    }

    @Test
    public void shouldUseDefaultSpecWhenNoSpecsAreResolved() throws IOException {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specs()).thenReturn(new String[]{});

        Configuration configuration = new AnnotationConfiguration(annotation, "blah.js");

        assertThat(configuration.specs()).containsOnly("src/test/javascript/specs/blah.js");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoSpecsAreResolvedAndNoDefaultProvided() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specs()).thenReturn(new String[]{});

        Configuration configuration = new AnnotationConfiguration(annotation);

        configuration.specs();
    }

    @Test
    public void shouldGetHtmlRunnerDirectory() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");

        Configuration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.htmlRunnerOutputDir()).isEqualTo(new File("src/test/javascript/runners"));
    }

    @Test
    public void shouldGetHtmlRunnerDirectoryWhenSubDirSpecified() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specRunnerSubDir()).thenReturn("foo");

        Configuration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.htmlRunnerOutputDir()).isEqualTo(new File("src/test/javascript/runners/foo"));
    }

    @Test
    public void shouldDelegateToAnnotationForWhetherToGenerateHtmlRunner() {
        Configuration configuration = new AnnotationConfiguration(annotation);

        when(annotation.generateSpecRunner()).thenReturn(true);
        assertThat(configuration.generateSpecRunner()).isEqualTo(true);

        when(annotation.generateSpecRunner()).thenReturn(false);
        assertThat(configuration.generateSpecRunner()).isEqualTo(false);
    }

    @Test
    public void shouldDelegateToAnnotationForWhetherToRunDebug() {
        Configuration configuration = new AnnotationConfiguration(annotation);

        when(annotation.debug()).thenReturn(true);
        assertThat(configuration.debug()).isEqualTo(true);

        when(annotation.debug()).thenReturn(false);
        assertThat(configuration.debug()).isEqualTo(false);
    }

    @Test
    public void shouldDelegateToAnnotationForWhetherToRunEnvJS() {
        Configuration configuration = new AnnotationConfiguration(annotation);

        when(annotation.envJs()).thenReturn(true);
        assertThat(configuration.envJs()).isEqualTo(true);

        when(annotation.envJs()).thenReturn(false);
        assertThat(configuration.envJs()).isEqualTo(false);
    }

    @Test
    public void shouldCreateFileResourceRelativeToJsRootDir() throws IOException {
        Configuration configuration = new AnnotationConfiguration(annotation);

        when(annotation.jsRootDir()).thenReturn("src/main/javascript");
        assertThat(configuration.jsRootFile("blah.js")).isEqualTo("src/main/javascript/blah.js");
    }
}
