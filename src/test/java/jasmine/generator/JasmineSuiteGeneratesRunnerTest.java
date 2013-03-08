package jasmine.generator;

import jasmine.junit.AnnotationConfiguration;
import jasmine.junit.JasmineTestRunner;
import jasmine.junit.classes.JasmineSuiteGeneratorClassWithRunner;
import jasmine.junit.classes.JasmineSuiteGeneratorClassWithRunnerInSubDir;
import jasmine.junit.classes.JasmineSuiteGeneratorClassWithoutRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class JasmineSuiteGeneratesRunnerTest {

    private static final File RUNNERS_OUTPUT_DIR = new File(System.getProperty(AnnotationConfiguration.HTML_OUTPUT_DIR) + "/runners");

    @Mock
    private RunNotifier notifierMock;

    @Before
    public void clearRunnersOutputDirectory() throws IOException {
        RUNNERS_OUTPUT_DIR.mkdirs();
        FileUtils.cleanDirectory(RUNNERS_OUTPUT_DIR);
    }

    @Test
    public void byDefaultDoNotGenerateJasmineTestRunner() {
        Class<JasmineSuiteGeneratorClassWithoutRunner> testClass = JasmineSuiteGeneratorClassWithoutRunner.class;
        new JasmineTestRunner(testClass).run(notifierMock);

        File runnerResult = getTestRunnerResultFile(testClass);
        assertThat(runnerResult.isFile()).isFalse();
    }

    @Test
    public void outputsClasspathLibrariesWithRunner() throws IOException {
        Class<JasmineSuiteGeneratorClassWithRunner> testClass = JasmineSuiteGeneratorClassWithRunner.class;
        new JasmineTestRunner(testClass).run(notifierMock);

        File runnerResult = getTestRunnerResultFile(testClass);
        assertThat(runnerResult.isFile()).isTrue();

        String runnerContent = FileUtils.readFileToString(runnerResult);

        assertJSFileIncluded(runnerContent,
                "file://" + new File(new File(System.getProperty("java.io.tmpdir")), "jasmine.js").getAbsolutePath(),
                "file://" + new File(new File(System.getProperty("java.io.tmpdir")), "jasmine-html.js").getAbsolutePath());
        assertCssFileIncluded(runnerContent,
                "file://" + new File(new File(System.getProperty("java.io.tmpdir")), "jasmine.css").getAbsolutePath());
    }

    @Test
    public void generateJasmineTestRunnerAfterRunningTests() throws IOException {
        Class<JasmineSuiteGeneratorClassWithRunner> testClass = JasmineSuiteGeneratorClassWithRunner.class;
        new JasmineTestRunner(testClass).run(notifierMock);

        File runnerResult = getTestRunnerResultFile(testClass);
        assertThat(runnerResult.isFile()).isTrue();

        String runnerContent = FileUtils.readFileToString(runnerResult);

        assertThat(runnerContent).contains("jasmine.getEnv().addReporter(new jasmine.TrivialReporter());");
        assertJSFileIncluded(runnerContent,
                "file://" + new File("src/test/javascript/sources/source1.js").getAbsolutePath(),
                "file://" + new File("src/test/javascript/sources/source2.js").getAbsolutePath(),
                "file://" + new File("src/test/javascript/specs/spec1.js").getAbsolutePath(),
                "file://" + new File("src/test/javascript/specs/spec2.js").getAbsolutePath());
    }

    @Test
    public void generateJasmineTestRunnerAfterRunningTestsWithSubDir() throws IOException {
      Class<JasmineSuiteGeneratorClassWithRunnerInSubDir> testClass =
        JasmineSuiteGeneratorClassWithRunnerInSubDir.class;
      new JasmineTestRunner(testClass).run(notifierMock);

      File runnerResult = getTestRunnerResultFile(testClass, "subDir1/subDir2");
      assertThat(runnerResult.isFile()).isTrue();

      String runnerContent = FileUtils.readFileToString(runnerResult);

      assertThat(runnerContent).contains("jasmine.getEnv().addReporter(new jasmine.TrivialReporter());");
      assertJSFileIncluded(runnerContent,
              "file://" + new File("src/test/javascript/sources/source1.js").getAbsolutePath(),
              "file://" + new File("src/test/javascript/sources/source2.js").getAbsolutePath(),
              "file://" + new File("src/test/javascript/specs/spec1.js").getAbsolutePath(),
              "file://" + new File("src/test/javascript/specs/spec2.js").getAbsolutePath());

    }

    private File getTestRunnerResultFile(Class<?> testClass) {
        return getTestRunnerResultFile(testClass, StringUtils.EMPTY);
    }

    private File getTestRunnerResultFile(Class<?> testClass, String subDir) {
      File file = RUNNERS_OUTPUT_DIR;
      if (StringUtils.isNotBlank(subDir)) {
        file = new File(file, subDir);
      }
      return new File(file, testClass.getSimpleName() + "Runner.html");
    }

    private void assertJSFileIncluded(String rawContent, String... files) {
        for (String file : files) {
            assertThat(rawContent).contains("<script type='text/javascript' src='" + file + "'></script>");
        }
    }

    private void assertCssFileIncluded(String rawContent, String... files) {
        for (String file : files) {
            assertThat(rawContent).contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + file + "\">");
        }
    }
}
