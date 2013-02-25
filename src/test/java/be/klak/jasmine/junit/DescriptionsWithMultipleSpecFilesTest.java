package be.klak.jasmine.junit;

import be.klak.jasmine.junit.classes.JasmineSuiteGeneratorClassWithRunner;
import org.junit.Test;
import org.junit.runner.Description;

import static org.fest.assertions.Assertions.assertThat;

public class DescriptionsWithMultipleSpecFilesTest {

	@Test
	public void getDescriptionsShouldIncludeBothSpec1AndSpec2SuiteInfo() {
		Description root = new JasmineTestRunner(JasmineSuiteGeneratorClassWithRunner.class).getDescription();

		assertThat(root.getChildren()).hasSize(2);
		Description spec1 = root.getChildren().get(0);
		assertThat(spec1.getDisplayName()).isEqualTo("spec 1");
		assertThat(spec1.getChildren()).hasSize(1);

		Description spec2 = root.getChildren().get(1);
		assertThat(spec2.getDisplayName()).isEqualTo("spec 2");
		assertThat(spec2.getChildren()).hasSize(1);
	}

}
