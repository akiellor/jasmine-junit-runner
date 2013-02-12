package be.klak.junit.jasmine;

import be.klak.junit.resources.FileResource;
import be.klak.junit.resources.Resource;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class AnnotationConfiguration {
    private final JasmineSuite annotation;
    private final String defaultSpec;

    public AnnotationConfiguration(JasmineSuite annotation){
        this(annotation, null);
    }

    public AnnotationConfiguration(JasmineSuite annotation, String defaultSpec) {
        this.annotation = annotation;
        this.defaultSpec = defaultSpec;
    }

    public Collection<? extends Resource> sources() {
        return FileResource.files(new File(annotation.sourcesRootDir()), annotation.sources());
    }

    public Collection<? extends Resource> specs() {
        List<FileResource> specs = FileResource.files(new File(annotation.jsRootDir(), "specs"), annotation.specs());
        if(!specs.isEmpty()) { return specs; }
        if(defaultSpec != null) { return FileResource.files(new File(annotation.jsRootDir(), "specs"), defaultSpec); }

        throw new IllegalStateException("No specs found.");
    }
}
