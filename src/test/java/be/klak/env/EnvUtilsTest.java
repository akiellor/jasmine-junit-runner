package be.klak.env;

import be.klak.jasmine.junit.JasmineSuite;
import be.klak.jasmine.junit.JasmineTestRunner;
import be.klak.rhino.RhinoContext;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(JasmineTestRunner.class)
@JasmineSuite(sources = "jquery-1.6.1.js", sourcesRootDir = "src/test/javascript")
public class EnvUtilsTest {

	@Before
	public void loadJasmineJQueryMatchers(RhinoContext context) {
		context.loadFromVirtualFileSystem("js/lib/jasmine-jquery-rhino.js");
	}

}
