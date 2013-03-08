package jasmine.rhino;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import jasmine.utils.Exceptions;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static java.util.Arrays.asList;

public class VirtualFileSystem {
    private final Iterable<Vfs.File> files;

    public VirtualFileSystem(Iterable<String> paths, Predicate<Vfs.File> candidateFilter) {
        Iterable<URL> pathUrls = Iterables.transform(Iterables.concat(paths, asList(".")), new Function<String, URL>() {
            @Override public URL apply(@Nullable String input) {
                try {
                    return new File(input).toURI().toURL();
                } catch (MalformedURLException e) {
                    throw Exceptions.unchecked(e);
                }
            }
        });

        List<URL> sources = Lists.newArrayList(Iterables.concat(
                ClasspathHelper.forJavaClassPath(), pathUrls));
        this.files = Lists.newArrayList(Vfs.findFiles(sources, candidateFilter));
    }

    public Vfs.File find(final String path) {
        Iterable<Vfs.File> files = Iterables.filter(this.files, new Predicate<Vfs.File>() {
            @Override public boolean apply(@Nullable Vfs.File input) {
                return input != null && path.equals(input.getRelativePath());
            }
        });

        if (Iterables.isEmpty(files)) {
            throw new IllegalArgumentException("Could not find resource: " + path);
        }

        return files.iterator().next();
    }
}
