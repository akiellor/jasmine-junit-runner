package jasmine.junit;

import jasmine.runtime.Configuration;
import jasmine.runtime.utils.SystemProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationConfigurationTest {
    @Mock
    JasmineSuite annotation;
    @Mock
    SystemProperties properties;
    String defaultSpec = "defaultSpec.js";

    @Test
    public void shouldLoadSourcesAsFileResources() throws IOException {
        when(annotation.sources()).thenReturn(new String[]{"one.js", "two.js"});

        Configuration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.sources()).containsOnly("one.js", "two.js");
    }

    @Test
    public void shouldLoadSpecsAsFileResources() throws IOException {
        when(annotation.specs()).thenReturn(new String[]{"one.js", "two.js"});

        Configuration configuration = new AnnotationConfiguration(annotation);

        assertThat(configuration.specs()).containsOnly("one.js", "two.js");
    }

    @Test
    public void shouldUseDefaultSpecWhenNoSpecsAreResolved() throws IOException {
        when(annotation.specs()).thenReturn(new String[]{});

        Configuration configuration = new AnnotationConfiguration(annotation, "blah.js");

        assertThat(configuration.specs()).containsOnly("blah.js");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoSpecsAreResolvedAndNoDefaultProvided() {
        when(annotation.specs()).thenReturn(new String[]{});

        Configuration configuration = new AnnotationConfiguration(annotation);

        configuration.specs();
    }
}
