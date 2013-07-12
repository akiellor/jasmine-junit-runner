package jasmine.runtime.vfs;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.reflections.vfs.Vfs;

import java.util.List;

import static java.util.Arrays.asList;

public class VirtualFileSystem {
    private final List<Source> sources;

    VirtualFileSystem(Source... sources){
        this.sources = asList(sources);
    }

    public VirtualFileSystem(Iterable<String> paths, Predicate<Vfs.File> candidateFilter) {
        this(new ReflectionsSource(paths, candidateFilter), new FileSource());
    }

    public Iterable<Vfs.File> findAll(final String regex) {
        Iterable<Vfs.File> files = Iterables.concat(Iterables.transform(sources, new Function<Source, Iterable<Vfs.File>>() {
            @Override public Iterable<Vfs.File> apply(Source input) {
                return input.findMatching(regex);
            }
        }));

        if (Iterables.isEmpty(files)) {
            throw new IllegalArgumentException("Could not find any resources for: " + regex);
        }

        return files;
    }

    public Vfs.File find(final String path) {
        Iterable<Vfs.File> files = Iterables.concat(Iterables.transform(sources, new Function<Source, Iterable<Vfs.File>>() {
            @Override public Iterable<Vfs.File> apply(Source input) {
                return input.findExact(path);
            }
        }));

        if (Iterables.isEmpty(files)) {
            throw new IllegalArgumentException("Could not find resource: " + path);
        }

        return files.iterator().next();
    }
}
