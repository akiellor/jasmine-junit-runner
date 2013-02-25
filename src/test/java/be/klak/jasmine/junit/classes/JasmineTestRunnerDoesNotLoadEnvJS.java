package be.klak.jasmine.junit.classes;

import be.klak.jasmine.junit.JasmineSuite;

@JasmineSuite(specs = { "doesNotLoadEnvJSSpec.js" }, envJs = false)
public class JasmineTestRunnerDoesNotLoadEnvJS { }
