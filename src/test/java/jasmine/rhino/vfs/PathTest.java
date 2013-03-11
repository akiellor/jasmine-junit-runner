package jasmine.rhino.vfs;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;

public class PathTest {
    @Test
    public void shouldGetTheDirectoriesFromTheClasspathAsUrls() throws MalformedURLException {
        File root = Files.createTempDir();
        File classes = new File(root, "target/classes");
        File testClasses = new File(root, "target/testClasses");
        classes.mkdirs();
        testClasses.mkdirs();

        HashSet<String> paths = newHashSet(classes.getAbsolutePath(), testClasses.getAbsolutePath());

        Path path = new Path(paths);

        Set<URL> urls = path.toUrls();

        assertThat(urls).containsOnly(
                classes.toURI().toURL(),
                testClasses.toURI().toURL()
        );
    }

    @Test
    public void shouldNotIncludeFilesOrDirectoriesThatDontExist() throws MalformedURLException {
        File root = Files.createTempDir();
        File classes = new File(root, "target/classes");
        File testClasses = new File(root, "target/testClasses");
        classes.mkdirs();

        HashSet<String> paths = newHashSet(classes.getAbsolutePath(), testClasses.getAbsolutePath());

        Path path = new Path(paths);

        Set<URL> urls = path.toUrls();

        assertThat(urls).containsOnly(
                classes.toURI().toURL()
        );
    }

    @Test
    public void shouldBeAbleToAppendPaths() {
        Path path = new Path(newHashSet("target/classes"));

        Path actual = path.append(newHashSet("target/testClasses"));
        Path expected = new Path(newHashSet("target/classes", "target/testClasses"));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldBuildClasspathFromSystemProperty() {
        Path actual = Path.fromClasspath();
        Path expected = new Path(newHashSet(System.getProperty("java.class.path").split(File.pathSeparator)));

        assertThat(actual).isEqualTo(expected);
    }
}
