package jasmine.runtime;

import com.google.common.base.Optional;

public interface Describe {
    String getId();
    String getDescription();
    Optional<Describe> getParent();
}
