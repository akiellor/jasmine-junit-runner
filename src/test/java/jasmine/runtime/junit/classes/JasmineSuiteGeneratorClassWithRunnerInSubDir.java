package jasmine.runtime.junit.classes;

import jasmine.runtime.junit.JasmineSuite;

@JasmineSuite(
		specs = { "spec1.js", "spec2.js" },
		sources = { "source1.js", "source2.js" },
		sourcesRootDir = "src/test/javascript/sources/",
		generateSpecRunner = true,
    specRunnerSubDir = "subDir1/subDir2")
public class JasmineSuiteGeneratorClassWithRunnerInSubDir {

}
