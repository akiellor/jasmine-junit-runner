package jasmine.junit;

import jasmine.runtime.Configuration;
import jasmine.utils.SystemProperties;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public class AnnotationConfiguration implements Configuration {
    public static final String HTML_OUTPUT_DIR = "jasmine.html.outputDir";
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

    @Override public File htmlRunnerOutputDir() {
        String outputDir = properties.get(HTML_OUTPUT_DIR);
        if(outputDir == null){
            throw new IllegalStateException("Must specify SystemProperty '" + HTML_OUTPUT_DIR + "' in order to generate output");
        }
        StringBuilder outputPath = new StringBuilder(outputDir).append("/runners");
        if (StringUtils.isNotBlank(annotation.specRunnerSubDir())) {
            outputPath.append('/').append(annotation.specRunnerSubDir());
        }
        return new File(outputPath.toString());
    }

    @Override public boolean generateSpecRunner() {
        return annotation.generateSpecRunner();
    }

    @Override public boolean debug() {
        return annotation.debug();
    }

    @Override public boolean envJs() {
        return annotation.envJs();
    }

    private String fromPwd(File file){
        return new File(".").toURI().relativize(file.toURI()).toString();
    }

    @Override public List<String> getJavascriptPath() {
        String javascriptPath = properties.get("javascript.path");
        if(javascriptPath == null) { return newArrayList(); }

        return newArrayList(javascriptPath.split(File.pathSeparator));
    }
}