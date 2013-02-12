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
}
