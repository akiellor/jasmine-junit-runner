package jasmine.runtime.generator.resources;

import java.net.URL;

public interface Resource {
    URL getURL();
    String getBaseName();
    FileResource asFileResource();
}
