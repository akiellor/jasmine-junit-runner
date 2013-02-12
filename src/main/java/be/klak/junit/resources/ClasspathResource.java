package be.klak.junit.resources;

import java.io.File;
import java.net.URL;

public class ClasspathResource implements Resource {
    private final String path;
    private final ClassLoader classLoader;

    public ClasspathResource(String path, ClassLoader classLoader){
        this.path = path;
        this.classLoader = classLoader;
    }

    public ClasspathResource(String path){
        this(path, Thread.currentThread().getContextClassLoader());
    }

    public URL getURL(){
        return classLoader.getResource(path);
    }

    public String getBaseName() {
        String external = getURL().toExternalForm();
        return external.substring(external.lastIndexOf("/") + 1);
    }

    @Override public FileResource asFileResource() {
        return FileResource.from(this, new File(System.getProperty("java.io.tmpdir")));
    }
}
