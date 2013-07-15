package jasmine.runtime.vfs;

import org.reflections.vfs.Vfs;

public interface Source {
    Iterable<Vfs.File> findMatching(String regex);

    Iterable<Vfs.File> findExact(String path);
}
