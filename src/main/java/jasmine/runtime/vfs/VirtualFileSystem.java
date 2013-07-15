package jasmine.runtime.vfs;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.util.List;

import static java.util.Arrays.asList;

public class VirtualFileSystem {
    public static class Filters{
        public static final Predicate<Vfs.File> JAVASCRIPT = new Predicate<Vfs.File>() {
            @Override
            public boolean apply(@Nullable Vfs.File input) {
                return input != null && input.getRelativePath().endsWith("js");
            }
        };
    }

    private final List<Source> sources;

    public VirtualFileSystem(Source... sources) {
        this.sources = asList(sources);
    }

    public VirtualFileSystem(Iterable<String> paths, Predicate<Vfs.File> candidateFilter) {
        this(new ReflectionsSource(paths, candidateFilter), new FileSource());
    }

    public Iterable<Vfs.File> findAll(final String regex) {
        Iterable<Vfs.File> files = Iterables.concat(Iterables.transform(sources, new Function<Source, Iterable<Vfs.File>>() {
            @Override
            public Iterable<Vfs.File> apply(Source input) {
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
            @Override
            public Iterable<Vfs.File> apply(Source input) {
                return input.findExact(path);
            }
        }));

        if (Iterables.isEmpty(files)) {
            throw new IllegalArgumentException("Could not find resource: " + path);
        }

        return files.iterator().next();
    }
}
