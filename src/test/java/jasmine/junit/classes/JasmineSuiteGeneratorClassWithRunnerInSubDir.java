package jasmine.junit.classes;

import jasmine.junit.JasmineSuite;

@JasmineSuite(
		specs = { "spec1.js", "spec2.js" },
		sources = { "source1.js", "source2.js" },
		generateSpecRunner = true,
    specRunnerSubDir = "subDir1/subDir2")
public class JasmineSuiteGeneratorClassWithRunnerInSubDir {

}
