package be.klak.junit.jasmine;

import be.klak.junit.resources.Resource;
import be.klak.junit.resources.ResourceParser;
import be.klak.utils.Exceptions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class Loader {
    private final Scriptable scope;
    private final Context context;
    private final ResourceParser parser;
    private final VirtualFileSystem fileSystem;

    public Loader(Scriptable scope, Context context) {
        this.scope = scope;
        this.context = context;
        this.parser = new ResourceParser();
        this.fileSystem = new VirtualFileSystem();
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

    private static class VirtualFileSystem{
        private final Iterable<Vfs.File> files;

        public VirtualFileSystem(){
            try {
                List<URL> sources = Lists.newArrayList(Iterables.concat(ClasspathHelper.forJavaClassPath(), asList(new File(".").toURI().toURL())));
                this.files = Vfs.findFiles(sources, Predicates.<Vfs.File>alwaysTrue());
            } catch (MalformedURLException e) {
                throw Exceptions.unchecked(e);
            }
        }

        public Iterable<Vfs.File> find(final String... paths){
            Iterable<Vfs.File> files = Iterables.filter(this.files, new Predicate<Vfs.File>() {
                @Override public boolean apply(@Nullable Vfs.File input) {
                    return input != null && asList(paths).contains(input.getRelativePath());
                }
            });

            if(Iterables.isEmpty(files)) { throw new IllegalArgumentException("Could not find resources: " + asList(paths)); }

            return files;
        }
    }

    public void loadFromVirtualFileSystem(final String... paths) {
        for(Vfs.File file : fileSystem.find(paths)){
            try {
                this.context.evaluateReader(this.scope, new InputStreamReader(file.openInputStream()), file.getRelativePath(), 1, null);
            } catch (IOException e) {
                throw Exceptions.unchecked(e);
            }
        }
    }
}
