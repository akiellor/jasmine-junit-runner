package jasmine.junit;

import jasmine.runtime.Configuration;
import jasmine.runtime.utils.SystemProperties;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

class AnnotationConfiguration implements Configuration {
    private final JasmineSuite annotation;
    private final SystemProperties properties;
    private final String defaultSpec;

    public AnnotationConfiguration(JasmineSuite annotation) {
        this(annotation, null);
    }

    public AnnotationConfiguration(JasmineSuite annotation, String defaultSpec) {
        this(annotation, defaultSpec, new SystemProperties());
    }

    public AnnotationConfiguration(JasmineSuite annotation, String defaultSpec, SystemProperties properties) {
        this.annotation = annotation;
        this.defaultSpec = defaultSpec;
        this.properties = properties;
    }

    @Override public Collection<String> sources() {
        return asList(annotation.sources());
    }

    @Override public Collection<String> specs() {
        Collection<String> specs = asList(annotation.specs());

        if (!specs.isEmpty()) {
            return specs;
        }

        if (defaultSpec != null) {
            return asList(defaultSpec);
        }

        throw new IllegalStateException("No specs found.");
    }

    @Override public List<String> getJavascriptPath() {
        String javascriptPath = properties.get("javascript.path");
        if(javascriptPath == null) { return newArrayList(); }

        return newArrayList(javascriptPath.split(File.pathSeparator));
    }
}
