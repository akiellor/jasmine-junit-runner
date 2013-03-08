package jasmine.rhino;

import jasmine.utils.Exceptions;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class Loader {
    private final Scriptable scope;
    private final Context context;
    private final VirtualFileSystem fileSystem;

    public Loader(Scriptable scope, Context context, VirtualFileSystem fileSystem) {
        this.scope = scope;
        this.context = context;
        this.fileSystem = fileSystem;
    }

    @SuppressWarnings("UnusedDeclaration") public void loadFromVirtualFileSystem(final String path) {
        loadFromVirtualFileSystem(asList(path));
    }

    public void loadFromVirtualFileSystem(final List<String> paths) {
        Collection<Vfs.File> files = Collections2.transform(paths, new Function<String, Vfs.File>() {
            @Override public Vfs.File apply(@Nullable String input) {
                return fileSystem.find(input);
            }
        });

        for(Vfs.File file : files){
            try {
                this.context.evaluateReader(this.scope, new InputStreamReader(file.openInputStream()), file.getRelativePath(), 1, null);
            } catch (IOException e) {
                throw Exceptions.unchecked(e);
            }
        }
    }
}
