package jasmine.runtime.vfs;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.util.List;

public class InMemorySource implements Source {
    private final List<Vfs.File> files;

    public InMemorySource(List<Vfs.File> files) {
        this.files = files;
    }

    @Override
    public Iterable<Vfs.File> findMatching(final String regex) {
        return Iterables.filter(this.files, new Predicate<Vfs.File>() {
            @Override
            public boolean apply(@Nullable Vfs.File input) {
                return input != null && input.getRelativePath().matches(regex);
            }
        });
    }

    @Override
    public Iterable<Vfs.File> findExact(final String path) {
        return Iterables.filter(this.files, new Predicate<Vfs.File>() {
            @Override
            public boolean apply(@Nullable Vfs.File input) {
                return input != null && path.equals(input.getRelativePath());
            }
        });
    }
}
