package be.klak.junit.resources;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ResourceParserTest {
    @Test
    public void shouldParsePathsStartingWithClasspathAsClasspathResources() {
        Resource resource  = new ResourceParser().parse("classpath:foo");

        assertThat(resource).isEqualTo(new ClasspathResource("foo"));
    }

    @Test
    public void shouldParsePathsStartingWithFileAsFileResources() {
        Resource resource  = new ResourceParser().parse("file:///foo");

        assertThat(resource).isEqualTo(new FileResource("file:///foo"));
    }

    @Test
    public void shouldParsePathsStartingWithSlashAsFileResources() {
        Resource resource  = new ResourceParser().parse("/foo");

        assertThat(resource).isEqualTo(new FileResource("/foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowForUnknownScheme() {
        new ResourceParser().parse("blah://");
    }
}
