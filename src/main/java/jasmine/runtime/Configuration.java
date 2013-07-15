package jasmine.runtime;

import jasmine.runtime.vfs.VirtualFileSystem;

import java.util.Collection;
import java.util.List;

public interface Configuration {
    Collection<String> sources();

    Collection<String> specs();

    VirtualFileSystem getFileSystem();
}
