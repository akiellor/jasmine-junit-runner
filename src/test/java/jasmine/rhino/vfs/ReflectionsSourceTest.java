package jasmine.rhino.vfs;

import org.junit.Test;
import org.reflections.vfs.Vfs;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReflectionsSourceTest {
    @Test
    public void shouldFindAllFilesMatchingPattern() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        ReflectionsSource fileSystem = new ReflectionsSource(asList(one, two));

        Iterable<Vfs.File> files = fileSystem.findMatching("source/.*?.js");

        assertThat(files).containsOnly(one, two);
    }

    @Test
    public void shouldFailWhenNoFilesMatchProvidedRegex() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        ReflectionsSource fileSystem = new ReflectionsSource(asList(one, two));

        assertThat(fileSystem.findExact("blah.js")).isEmpty();
    }

    @Test
    public void shouldFindOneOfTwoFilesWithFindAll() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        ReflectionsSource fileSystem = new ReflectionsSource(asList(one, two));

        Iterable<Vfs.File> files = fileSystem.findMatching("source/on.\\.js");

        assertThat(files).containsOnly(one);
    }

    @Test
    public void shouldFindASingleFileWithExactMatch() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        ReflectionsSource fileSystem = new ReflectionsSource(asList(one, two));

        Iterable<Vfs.File> file = fileSystem.findExact("source/one.js");

        assertThat(file).containsOnly(one);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNoFileFound() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        ReflectionsSource fileSystem = new ReflectionsSource(asList(one, two));

        assertThat(fileSystem.findExact("blah.js")).isEmpty();
    }

    private Vfs.File mockFile(String path) {
        Vfs.File file = mock(Vfs.File.class);
        when(file.getRelativePath()).thenReturn(path);
        return file;
    }
}
