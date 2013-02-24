package be.klak.junit.jasmine;

import be.klak.junit.resources.Resource;
import be.klak.junit.resources.ResourceParser;
import be.klak.utils.Exceptions;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Loader {
    private final Scriptable scope;
    private final Context context;
    private final ResourceParser parser;
    private final VirtualFileSystem fileSystem;

    public Loader(Scriptable scope, Context context, VirtualFileSystem fileSystem) {
        this.scope = scope;
        this.context = context;
        this.parser = new ResourceParser();
        this.fileSystem = fileSystem;
    }

    public void load(Resource resource) {
        URL resourceURL = resource.getURL();

        if (resourceURL == null) {
            throw new IllegalArgumentException("resource " + resource + " not found");
        }

        try {
            this.context.evaluateReader(this.scope, new InputStreamReader(resource.getURL().openStream()), resource.getBaseName(), 1, null);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    public void load(Resource... resources){
        load(Arrays.asList(resources));
    }

    public void load(List<? extends Resource> resources) {
        for(Resource resource : resources){ load(resource); }
    }

    public void load(String... paths) {
        for(String path : paths) { load(path); }
    }

    public void load(String path) {
        load(parser.parse(path));
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
