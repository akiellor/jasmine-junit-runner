package jasmine.runtime.rhino;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import jasmine.runtime.utils.Exceptions;
import jasmine.runtime.vfs.VirtualFileSystem;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Arrays.asList;

public class Loader {
    private final Scriptable scope;
    private final Context context;
    private final VirtualFileSystem fileSystem;
    private final CopyOnWriteArraySet<Vfs.File> loaded = new CopyOnWriteArraySet<Vfs.File>();

    public Loader(Scriptable scope, Context context, VirtualFileSystem fileSystem) {
        this.scope = scope;
        this.context = context;
        this.fileSystem = fileSystem;
    }

    @SuppressWarnings("UnusedDeclaration") public void loadFromVirtualFileSystem(final String path) {
        loadFromVirtualFileSystem(asList(path));
    }

    public void loadFromVirtualFileSystem(final List<String> paths) {
        Iterable<Vfs.File> files = Iterables.concat(Iterables.transform(paths, new Function<String, Iterable<Vfs.File>>() {
            @Override public Iterable<Vfs.File> apply(@Nullable String input) {
                return fileSystem.findAll(input);
            }
        }));

        load(files);
    }

    @SuppressWarnings("UnusedDeclaration") public void loadAllFromVirtualFileSystem(final String path) {
        loadAllFromVirtualFileSystem(asList(path));
    }

    public void loadAllFromVirtualFileSystem(final List<String> paths) {
        Iterable<Vfs.File> files = Iterables.concat(Iterables.transform(paths, new Function<String, Iterable<Vfs.File>>() {
            @Override public Iterable<Vfs.File> apply(@Nullable String input) {
                return fileSystem.findAll(input);
            }
        }));

        load(files);
    }

    private synchronized void load(Iterable<Vfs.File> files) {
        for(Vfs.File file : files){
            if(loaded.contains(file)) { continue; }
            loaded.add(file);
            try {
                this.context.evaluateReader(this.scope, new InputStreamReader(file.openInputStream()), file.getRelativePath(), 1, null);
            } catch (IOException e) {
                throw Exceptions.unchecked(e);
            }
        }
    }
}
