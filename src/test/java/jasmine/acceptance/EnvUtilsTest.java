package jasmine.acceptance;

import jasmine.junit.JasmineSuite;
import jasmine.junit.JasmineTestRunner;
import jasmine.runtime.rhino.RhinoContext;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(JasmineTestRunner.class)
@JasmineSuite(sources = "jquery-1.6.1.js", envJs = true)
public class EnvUtilsTest {

	@Before
	public void loadJasmineJQueryMatchers(RhinoContext context) {
		context.loadFromVirtualFileSystem("js/lib/jasmine-jquery-rhino.js");
	}

}
