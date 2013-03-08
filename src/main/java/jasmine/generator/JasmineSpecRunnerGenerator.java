package jasmine.generator;

import com.google.common.base.Predicate;
import jasmine.generator.resources.ClasspathResource;
import jasmine.generator.resources.FileResource;
import jasmine.rhino.vfs.VirtualFileSystem;
import jasmine.runtime.Configuration;
import jasmine.utils.Exceptions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JasmineSpecRunnerGenerator {

    private final Configuration configuration;
	private final String outputFileName;
    private final VirtualFileSystem virtualFileSystem;

    public JasmineSpecRunnerGenerator(Configuration configuration, String outputFileName) {
        this.configuration = configuration;
		this.outputFileName = outputFileName;
        this.virtualFileSystem = new VirtualFileSystem(configuration.getJavascriptPath(), new Predicate<Vfs.File>() {
            @Override public boolean apply(@Nullable Vfs.File input) {
                return input != null && input.getRelativePath().endsWith("js");
            }
        });
	}

	public void generate() {
        List<FileResource> javascriptFiles = new ArrayList<FileResource>();

        javascriptFiles.addAll(Arrays.asList(
                new ClasspathResource("js/lib/jasmine-1.3.1/jasmine.js").asFileResource(),
                new ClasspathResource("js/lib/jasmine-1.3.1/jasmine-html.js").asFileResource()));

        for(String source : configuration.sources()){
            javascriptFiles.add(new FileResource(findAndEnsureOnDisk(source)));
        }
        for(String spec : configuration.specs()){
            javascriptFiles.add(new FileResource(findAndEnsureOnDisk(spec)));
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

    private File findAndEnsureOnDisk(String source) {
        Vfs.File file = virtualFileSystem.find(source);
        File out = new File(configuration.htmlRunnerOutputDir(), file.getRelativePath());
        out.getParentFile().mkdirs();
        try {
            IOUtils.copy(file.openInputStream(), new FileOutputStream(out));
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
        return out;
    }

}
