package be.klak.junit.jasmine;

import be.klak.junit.resources.FileResource;
import be.klak.junit.resources.Resource;

import java.io.File;
import java.util.Collection;

public class AnnotationConfiguration {
    private final JasmineSuite annotation;

    public AnnotationConfiguration(JasmineSuite annotation){
        this.annotation = annotation;
    }

    public Collection<? extends Resource> sources() {
        return FileResource.files(new File(annotation.sourcesRootDir()), annotation.sources());
    }
}
