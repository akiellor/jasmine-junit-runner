package jasmine.rhino.vfs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.reflections.vfs.Vfs;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VirtualFileSystemTest {
    @Mock ReflectionsSource source;
    @Mock Vfs.File one;
    @Mock Vfs.File two;

    @Test
    public void shouldReturnFirstFileFound() {
        when(source.findExact("someFile.js")).thenReturn(newArrayList(one, two));

        VirtualFileSystem fileSystem = new VirtualFileSystem(source);

        assertThat(fileSystem.find("someFile.js")).isEqualTo(one);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentWhenNoFileFoundWithFindExact() {
        when(source.findExact("someFile.js")).thenReturn(new ArrayList<Vfs.File>());

        VirtualFileSystem fileSystem = new VirtualFileSystem(source);

        fileSystem.find("someFile.js");
    }

    @Test
    public void shouldReturnAllFilesMatchingPattern() {
        when(source.findMatching("someFile.js")).thenReturn(newArrayList(one, two));

        VirtualFileSystem fileSystem = new VirtualFileSystem(source);

        assertThat(fileSystem.findAll("someFile.js")).containsOnly(one, two);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentWhenNoFileFoundWithFindMatching() {
        when(source.findMatching("someFile.js")).thenReturn(new ArrayList<Vfs.File>());

        VirtualFileSystem fileSystem = new VirtualFileSystem(source);

        fileSystem.findAll("someFile.js");
    }
}
