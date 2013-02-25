package be.klak.jasmine;

import org.junit.runner.Description;

import java.util.Collection;

public class JasmineDescriptions {

	private final Description rootDescription;
	private final Collection<It> specs;

	public JasmineDescriptions(Description rootDescription, Collection<It> specs) {
		this.rootDescription = rootDescription;
		this.specs = specs;
	}

	public Description getRootDescription() {
		return rootDescription;
	}

	public Collection<It> getSpecs() {
		return specs;
	}
}
