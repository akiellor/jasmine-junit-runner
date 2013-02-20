package be.klak.junit.jasmine;

import org.junit.Test;
import org.junit.runner.Description;

import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class JasmineJSSuiteConverterTest {
    @Test
    public void shouldCreateDifferentDescriptionsDespiteHavingSameName() {
        Description root = Description.createSuiteDescription("root");

        Description first = JasmineJSSuiteConverter.addSuiteToDescription(root, new ArrayList<String>(), "suite");
        Description second = JasmineJSSuiteConverter.addSuiteToDescription(root, new ArrayList<String>(), "suite");

        assertThat(first).isNotEqualTo(second);
    }
}
