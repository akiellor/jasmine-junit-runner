package jasmine.cli;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import jasmine.runtime.Configuration;
import jasmine.runtime.utils.Exceptions;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

class CliConfiguration implements Configuration {
    @Option(name = "-p")
    private String path;

    @Argument
    private List<String> arguments = newArrayList();

    public CliConfiguration(String... args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<String> sources() {
        return Arrays.asList();
    }

    @Override
    public Collection<String> specs() {
        if (arguments.isEmpty()) {
            return newArrayList(".*Spec.js$");
        }
        return ImmutableList.copyOf(arguments);
    }

    @Override
    public List<String> getJavascriptPath() {
        if (path == null) {
            try {
                return newArrayList(new File("").getAbsoluteFile().toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                throw Exceptions.unchecked(e);
            }
        }
        return newArrayList(path.split(":"));
    }
}
