package be.klak.junit.jasmine.generator.resources;

import java.net.URL;

public interface Resource {
    URL getURL();
    String getBaseName();
    FileResource asFileResource();
}
