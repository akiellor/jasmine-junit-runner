package jasmine.runtime.webdriver;

import com.google.common.collect.Iterables;
import jasmine.runtime.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

public class JasmineSpecRunnerGenerator {

    private final Configuration configuration;

    public JasmineSpecRunnerGenerator(Configuration configuration) {
        this.configuration = configuration;
	}

    public void generate(OutputStream outputStream) {
        Iterable<String> javascriptFiles = Iterables.concat(
                asList(
                        "jasmine/runtime/webdriver/clientLoader.js",
                        "js/lib/jasmine-1.3.1/jasmine.js",
                        "js/lib/jasmine-1.3.1/jasmine-html.js",
                        "jasmine/runtime/webdriver/restReporter.js"),
                newArrayList(configuration.sources()),
                newArrayList(configuration.specs()),
                newArrayList("jasmine/runtime/webdriver/bootstrap.js")
        );

        Iterable<String> cssFiles = newArrayList("js/lib/jasmine-1.3.1/jasmine.css");

        HtmlPageRunner htmlPageRunner = new HtmlPageRunner(javascriptFiles, cssFiles);
        try {
            IOUtils.copy(new ByteArrayInputStream(htmlPageRunner.render().getBytes()), outputStream);
        } catch (IOException e) {
            throw new RuntimeException("unable to write spec runner contents to destination", e);
        }
    }
}
