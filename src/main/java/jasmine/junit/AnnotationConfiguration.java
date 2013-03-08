package jasmine.junit;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import jasmine.runtime.Configuration;
import jasmine.utils.SystemProperties;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
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
        return Collections2.transform(asList(annotation.sources()), new Function<String, String>() {
            @Override public String apply(@Nullable String input) {
                    return fromPwd(new File(annotation.sourcesRootDir(), input));
            }
        });
    }

    @Override public Collection<String> specs() {
        Collection<String> specs = Collections2.transform(asList(annotation.specs()), new Function<String, String>() {
            @Override public String apply(@Nullable String input) {
                return fromPwd(new File(new File(annotation.jsRootDir(), "specs"), input));
            }
        });

        if (!specs.isEmpty()) {
            return specs;
        }
        if (defaultSpec != null) {
            return asList(fromPwd(new File(new File(annotation.jsRootDir(), "specs"), defaultSpec)));
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
