package jasmine.utils;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class SystemPropertiesTest {
    @Test
    public void shouldGetPropertyFromSystemProperties() {
        SystemProperties properties = new SystemProperties();

        String classPath = properties.get("java.class.path");

        assertThat(classPath).isNotEmpty();
    }
}
