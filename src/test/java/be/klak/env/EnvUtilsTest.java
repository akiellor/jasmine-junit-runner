package be.klak.env;

import be.klak.junit.jasmine.JasmineSuite;
import be.klak.junit.jasmine.JasmineTestRunner;
import be.klak.rhino.RhinoContext;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(JasmineTestRunner.class)
@JasmineSuite(sources = "jquery-1.6.1.js", sourcesRootDir = "src/test/javascript")
public class EnvUtilsTest {

	@Before
	public void loadJasmineJQueryMatchers(RhinoContext context) {
		context.load("classpath:js/lib/jasmine-1.0.2/jasmine-jquery-rhino.js");
	}

}
