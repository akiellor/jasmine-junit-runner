package jasmine.rhino.vfs;

import com.google.common.collect.Iterables;
import jasmine.utils.Exceptions;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Path {
    private final Set<String> paths;

    public static Path fromClasspath() {
        return new Path(newHashSet(System.getProperty("java.class.path").split(File.pathSeparator)));
    }

    public Path(Set<String> paths) {
        this.paths = paths;
    }

    public Set<URL> toUrls() {
        Set<URL> urls = newHashSet();
        for(String part : paths){
            File file = new File(part);
            if(file.exists()){
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw Exceptions.unchecked(e);
                }
            }
        }
        return Collections.unmodifiableSet(urls);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && new EqualsBuilder().append(paths, ((Path) o).paths).isEquals();
    }

    @Override
    public int hashCode() {
        return paths != null ? paths.hashCode() : 0;
    }

    public Path append(Iterable<String> additionalPaths) {
        return new Path(newHashSet(Iterables.concat(paths, additionalPaths)));
    }
}
