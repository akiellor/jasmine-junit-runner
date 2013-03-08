package jasmine.rhino;

import jasmine.utils.Exceptions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

    public VirtualFileSystem(Predicate<Vfs.File> candidateFilter){
        try {
            List<URL> sources = Lists.newArrayList(Iterables.concat(
                    ClasspathHelper.forJavaClassPath(), asList(new File(".").toURI().toURL())));
            this.files = Lists.newArrayList(Vfs.findFiles(sources, candidateFilter));
        } catch (MalformedURLException e) {
            throw Exceptions.unchecked(e);
        }
    }

    public Vfs.File find(final String path){
        Iterable<Vfs.File> files = Iterables.filter(this.files, new Predicate<Vfs.File>() {
            @Override public boolean apply(@Nullable Vfs.File input) {
                return input != null && path.equals(input.getRelativePath());
            }
        });

        if(Iterables.isEmpty(files)) { throw new IllegalArgumentException("Could not find resource: " + path); }

        return files.iterator().next();
    }
}
