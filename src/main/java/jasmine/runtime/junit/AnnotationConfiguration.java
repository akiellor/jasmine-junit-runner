package jasmine.runtime.junit;

import jasmine.runtime.Configuration;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;

import static java.util.Arrays.asList;

public class AnnotationConfiguration implements Configuration {
    private final JasmineSuite annotation;
    private final String defaultSpec;

    public AnnotationConfiguration(JasmineSuite annotation) {
        this(annotation, null);
    }

    public AnnotationConfiguration(JasmineSuite annotation, String defaultSpec) {
        this.annotation = annotation;
        this.defaultSpec = defaultSpec;
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
        StringBuilder outputPath = new StringBuilder(annotation.jsRootDir()).append("/runners");
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

    @Override public String jsRootFile(String relativePath) {
        return fromPwd(new File(annotation.jsRootDir(), relativePath));
    }
}