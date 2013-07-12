package jasmine.runtime.vfs;

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
    @Mock
    Source sourceOne;
    @Mock
    Source sourceTwo;

    @Mock
    Vfs.File one;
    @Mock
    Vfs.File two;
    @Mock
    Vfs.File three;

    @Test
    public void shouldReturnFirstFileFound() {
        when(sourceOne.findExact("someFile.js")).thenReturn(newArrayList(one, two));

        VirtualFileSystem fileSystem = new VirtualFileSystem(sourceOne);

        assertThat(fileSystem.find("someFile.js")).isEqualTo(one);
    }

    @Test
    public void shouldReturnFirstFileFoundFromManySources() {
        when(sourceOne.findExact("someFile.js")).thenReturn(new ArrayList<Vfs.File>());
        when(sourceTwo.findExact("someFile.js")).thenReturn(newArrayList(one, two));

        VirtualFileSystem fileSystem = new VirtualFileSystem(sourceOne, sourceTwo);

        assertThat(fileSystem.find("someFile.js")).isEqualTo(one);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentWhenNoFileFoundWithFindExact() {
        when(sourceOne.findExact("someFile.js")).thenReturn(new ArrayList<Vfs.File>());

        VirtualFileSystem fileSystem = new VirtualFileSystem(sourceOne);

        fileSystem.find("someFile.js");
    }

    @Test
    public void shouldReturnAllFilesMatchingPattern() {
        when(sourceOne.findMatching("someFile.js")).thenReturn(newArrayList(one, two));

        VirtualFileSystem fileSystem = new VirtualFileSystem(sourceOne);

        assertThat(fileSystem.findAll("someFile.js")).containsOnly(one, two);
    }

    @Test
    public void shouldReturnAllFilesMatchingPatternFromAllSources() {
        when(sourceOne.findMatching("someFile.js")).thenReturn(newArrayList(one, two));
        when(sourceTwo.findMatching("someFile.js")).thenReturn(newArrayList(three));

        VirtualFileSystem fileSystem = new VirtualFileSystem(sourceOne, sourceTwo);

        assertThat(fileSystem.findAll("someFile.js")).containsOnly(one, two, three);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentWhenNoFileFoundWithFindMatching() {
        when(sourceOne.findMatching("someFile.js")).thenReturn(new ArrayList<Vfs.File>());

        VirtualFileSystem fileSystem = new VirtualFileSystem(sourceOne);

        fileSystem.findAll("someFile.js");
    }
}
