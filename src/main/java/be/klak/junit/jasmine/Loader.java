package be.klak.junit.jasmine;

import be.klak.junit.resources.Resource;
import be.klak.junit.resources.ResourceParser;
import be.klak.utils.Exceptions;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Loader {
    private final Scriptable scope;
    private final Context context;
    private final ResourceParser parser;

    public Loader(Scriptable scope, Context context) {
        this.scope = scope;
        this.context = context;
        this.parser = new ResourceParser();
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
        for(String path : paths) { load(parser.parse(path)); }
    }
}
