package jasmine.runtime.generator;

import jasmine.runtime.Configuration;
import jasmine.runtime.generator.resources.ClasspathResource;
import jasmine.runtime.generator.resources.FileResource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JasmineSpecRunnerGenerator {

    private final Configuration configuration;
	private final String outputFileName;

    public JasmineSpecRunnerGenerator(Configuration configuration, String outputFileName) {
        this.configuration = configuration;
		this.outputFileName = outputFileName;
	}

	public void generate() {
        List<FileResource> javascriptFiles = new ArrayList<FileResource>();

        javascriptFiles.addAll(Arrays.asList(
                new ClasspathResource("js/lib/jasmine-1.3.1/jasmine.js").asFileResource(),
                new ClasspathResource("js/lib/jasmine-1.3.1/jasmine-html.js").asFileResource()));

        for(String source : configuration.sources()){
            javascriptFiles.add(new FileResource(source));
        }
        for(String spec : configuration.specs()){
            javascriptFiles.add(new FileResource(spec));
        }

        List<FileResource> cssFiles = new ArrayList<FileResource>();
        cssFiles.add(new ClasspathResource("js/lib/jasmine-1.3.1/jasmine.css").asFileResource());

        HtmlPageRunner htmlPageRunner = new HtmlPageRunner(javascriptFiles, cssFiles);
        try {
			FileUtils.writeStringToFile(new File(configuration.htmlRunnerOutputDir(), outputFileName), htmlPageRunner.render());
		} catch (IOException e) {
			throw new RuntimeException("unable to write spec runner contents to destination", e);
		}
	}

}
