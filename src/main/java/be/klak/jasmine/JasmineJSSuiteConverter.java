package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JasmineJSSuiteConverter {

	private final RhinoContext context;

	public JasmineJSSuiteConverter(RhinoContext context) {
		this.context = context;
	}

	public JasmineDescriptions convertToJunitDescriptions(Class<?> testClass, NativeArray baseSuites) {
		Description rootDescription = Description.createSuiteDescription(testClass);
		List<It> specs = convertSuiteArrayToDescriptions(baseSuites, rootDescription, new ArrayList<String>());
		return new JasmineDescriptions(rootDescription, specs);
	}

	private List<It> convertSuiteArrayToDescriptions(NativeArray suiteArray, Description rootDescription,
			List<String> processed) {
		List<It> specs = new ArrayList<It>();
		for (Object idObj : suiteArray.getIds()) {
			NativeObject suite = (NativeObject) suiteArray.get((Integer) idObj, suiteArray);

			String description = String.valueOf(suite.get("description", suite));
			if (!processed.contains(description)) {
				Description suiteDescription = addSuiteToDescription(rootDescription, processed, description);
				specs.addAll(convertToJunitDescription(suite, suiteDescription));

				NativeArray subSuites = (NativeArray) context.executeFunction(suite, "suites");
				specs.addAll(convertSuiteArrayToDescriptions(subSuites, suiteDescription, processed));
			}
		}

		return specs;
	}

	public static Description addSuiteToDescription(Description description, List<String> processed, String suiteName) {
		processed.add(suiteName);
		Description suiteDescription = Description.createSuiteDescription(suiteName, UUID.randomUUID());
		description.addChild(suiteDescription);
		return suiteDescription;
	}

	private List<It> convertToJunitDescription(NativeObject suite, Description description) {
		List<It> specsMap = new ArrayList<It>();
		NativeArray specsArray = (NativeArray) context.executeFunction(suite, "specs");
		for (Object idObj : specsArray.getIds()) {
			NativeObject spec = (NativeObject) specsArray.get((Integer) idObj, specsArray);

			It it = new It(spec);
			specsMap.add(it);
			description.addChild(it.getDescription());
		}

		return specsMap;
	}

}
