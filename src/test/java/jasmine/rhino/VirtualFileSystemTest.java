package jasmine.rhino;

import org.junit.Test;
import org.reflections.vfs.Vfs;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VirtualFileSystemTest {
    @Test
    public void shouldFindAllFilesMatchingPattern() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        VirtualFileSystem fileSystem = new VirtualFileSystem(asList(one, two));

        Iterable<Vfs.File> files = fileSystem.findAll("source/.*?.js");

        assertThat(files).containsOnly(one, two);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenNoFilesMatchProvidedRegex() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        VirtualFileSystem fileSystem = new VirtualFileSystem(asList(one, two));

        fileSystem.findAll("blah.js");
    }

    @Test
    public void shouldFindOneOfTwoFilesWithFindAll() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        VirtualFileSystem fileSystem = new VirtualFileSystem(asList(one, two));

        Iterable<Vfs.File> files = fileSystem.findAll("source/on.\\.js");

        assertThat(files).containsOnly(one);
    }

    @Test
    public void shouldFindASingleFileWithExactMatch() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        VirtualFileSystem fileSystem = new VirtualFileSystem(asList(one, two));

        Vfs.File file = fileSystem.find("source/one.js");

        assertThat(file).isEqualTo(one);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenNoFileFound() {
        Vfs.File one = mockFile("source/one.js");
        Vfs.File two = mockFile("source/two.js");

        VirtualFileSystem fileSystem = new VirtualFileSystem(asList(one, two));

        fileSystem.find("blah.js");
    }

    private Vfs.File mockFile(String path) {
        Vfs.File file = mock(Vfs.File.class);
        when(file.getRelativePath()).thenReturn(path);
        return file;
    }
}
