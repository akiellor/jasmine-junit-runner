package jasmine.runtime;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Address<T> {
    private final List<T> address;

    public Address() {
        address = newArrayList();
    }

    private Address(List<T> address) {
        this.address = address;
    }

    public Address<T> push(T element) {
        return new Address<T>(newArrayList(Iterables.concat(address, newArrayList(element))));
    }

    public Optional<T> peek() {
        if (address.isEmpty()) {
            return Optional.absent();
        }
        return Optional.of(address.get(address.size() - 1));
    }
}
