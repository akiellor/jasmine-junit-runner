package jasmine.runtime.vfs;

import org.junit.Test;
import org.reflections.vfs.Vfs;

import java.io.File;
import java.net.URL;

import static org.fest.assertions.Assertions.assertThat;

public class FileSourceTest {

    private final FileSource fileSource = new FileSource();

    @Test
    public void shouldFindNothingWhenFindMatching() {
        assertThat(fileSource.findMatching("lkjasdlkjhasd")).isEmpty();
    }

    @Test
    public void shouldFindExactMatch() {
        URL resource = this.getClass().getClassLoader().getResource("js/lib/loader.js");

        Iterable<Vfs.File> result = fileSource.findExact(resource.getFile());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo(new File(resource.toExternalForm()).getName());
    }

    @Test
    public void shouldFindExactWhenFindMatching() {
        URL resource = this.getClass().getClassLoader().getResource("js/lib/loader.js");

        Iterable<Vfs.File> result = fileSource.findMatching(resource.getFile());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getName()).isEqualTo(new File(resource.toExternalForm()).getName());
    }
}
