package jasmine.rhino.vfs;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.reflections.vfs.Vfs;

public class VirtualFileSystem {
    private final ReflectionsSource source;

    public VirtualFileSystem(ReflectionsSource source){
        this.source = source;
    }

    public VirtualFileSystem(Iterable<String> paths, Predicate<Vfs.File> candidateFilter) {
        this(new ReflectionsSource(paths, candidateFilter));
    }

    public Iterable<Vfs.File> findAll(final String regex) {
        Iterable<Vfs.File> files = source.findMatching(regex);

        if (Iterables.isEmpty(files)) {
            throw new IllegalArgumentException("Could not find any resources for: " + regex);
        }

        return files;
    }

    public Vfs.File find(final String path) {
        Iterable<Vfs.File> files = source.findExact(path);

        if (Iterables.isEmpty(files)) {
            throw new IllegalArgumentException("Could not find resource: " + path);
        }

        return files.iterator().next();
    }
}
