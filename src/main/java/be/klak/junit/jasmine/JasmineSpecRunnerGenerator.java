package be.klak.junit.jasmine;

import be.klak.junit.resources.ClasspathResource;
import be.klak.junit.resources.FileResource;
import be.klak.junit.resources.Resource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class JasmineSpecRunnerGenerator {

    private final JasmineSuite suite;
	private final Collection<? extends Resource> jasmineSpecs;
	private final String outputPath;
	private final String outputFileName;

    public JasmineSpecRunnerGenerator(Collection<? extends Resource> jasmineSpecs, JasmineSuite suite, String outputPath, String outputFileName) {
		this.jasmineSpecs = jasmineSpecs;
		this.suite = suite;
		this.outputPath = outputPath;
		this.outputFileName = outputFileName;
	}

	public void generate() {
        List<FileResource> javascriptFiles = new ArrayList<FileResource>();

        javascriptFiles.addAll(Arrays.asList(
                new ClasspathResource("js/lib/jasmine-1.0.2/jasmine.js").asFileResource(),
                new ClasspathResource("js/lib/jasmine-1.0.2/jasmine-html.js").asFileResource()));

        for(String source : suite.sources()){
            javascriptFiles.add(new FileResource(new File(suite.sourcesRootDir(), source)));
        }
        for(Resource spec : jasmineSpecs){
            javascriptFiles.add(spec.asFileResource());
        }

        List<FileResource> cssFiles = new ArrayList<FileResource>();
        cssFiles.add(new ClasspathResource("js/lib/jasmine-1.0.2/jasmine.css").asFileResource());

        HtmlPageRunner htmlPageRunner = new HtmlPageRunner(javascriptFiles, cssFiles);
        try {
			FileUtils.writeStringToFile(new File(outputPath, outputFileName), htmlPageRunner.render());
		} catch (IOException e) {
			throw new RuntimeException("unable to write spec runner contents to destination", e);
		}
	}

}
