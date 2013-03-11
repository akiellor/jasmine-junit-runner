package jasmine.junit;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class TestObjectTest {
    public static class TestClassWithInterestingNameTest{
    }

    @Test
    public void shouldGetTheDefaultSpecName() {
        TestObject object = new TestObject(TestClassWithInterestingNameTest.class);

        assertThat(object.getDefaultSpecPath()).isEqualTo("jasmine/junit/testClassWithInterestingNameSpec.js");
    }
}
