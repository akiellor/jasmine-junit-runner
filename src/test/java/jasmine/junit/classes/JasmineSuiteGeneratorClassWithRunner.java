package jasmine.junit.classes;

import jasmine.junit.JasmineSuite;

@JasmineSuite(
		specs = { "spec1.js", "spec2.js" },
		sources = { "source1.js", "source2.js" },
		sourcesRootDir = "src/test/javascript/sources/",
		generateSpecRunner = true)
public class JasmineSuiteGeneratorClassWithRunner {

}
