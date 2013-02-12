package be.klak.junit.jasmine;

import be.klak.junit.resources.FileResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationConfigurationTest {
    @Mock JasmineSuite annotation;

    @Test
    public void shouldLoadSourcesAsFileResources() {
        when(annotation.sourcesRootDir()).thenReturn("src/main/javascript");
        when(annotation.sources()).thenReturn(new String[]{"one.js", "two.js"});

        AnnotationConfiguration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.sources()).isEqualTo(FileResource.files(new File("src/main/javascript"), "one.js", "two.js"));
    }

    @Test
    public void shouldLoadSpecsAsFileResources() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specs()).thenReturn(new String[]{"one.js", "two.js"});

        AnnotationConfiguration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.specs()).isEqualTo(FileResource.files(new File("src/test/javascript/specs"), "one.js", "two.js"));
    }

    @Test
    public void shouldUseDefaultSpecWhenNoSpecsAreResolved() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specs()).thenReturn(new String[]{});

        AnnotationConfiguration configuration = new AnnotationConfiguration(annotation, "blah.js");

        assertThat(configuration.specs()).isEqualTo(FileResource.files(new File("src/test/javascript/specs"), "blah.js"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoSpecsAreResolvedAndNoDefaultProvided() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specs()).thenReturn(new String[]{});

        AnnotationConfiguration configuration = new AnnotationConfiguration(annotation);

        configuration.specs();
    }

    @Test
    public void shouldGetHtmlRunnerDirectory() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");

        AnnotationConfiguration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.htmlRunnerOutputDir()).isEqualTo(new File("src/test/javascript/runners"));
    }

    @Test
    public void shouldGetHtmlRunnerDirectoryWhenSubDirSpecified() {
        when(annotation.jsRootDir()).thenReturn("src/test/javascript");
        when(annotation.specRunnerSubDir()).thenReturn("foo");

        AnnotationConfiguration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.htmlRunnerOutputDir()).isEqualTo(new File("src/test/javascript/runners/foo"));
    }
}
