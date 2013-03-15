package jasmine.runtime;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class AddressTest {
    @Test
    public void shouldPushNextElement() {
        Address<String> address = new Address<String>();
        assertThat(address.peek()).isEqualTo(Optional.absent());
        assertThat(address.push("A").peek()).isEqualTo(Optional.of("A"));
        assertThat(address.push("A").push("B").peek()).isEqualTo(Optional.of("B"));
    }
}
