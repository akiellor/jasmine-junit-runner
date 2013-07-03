package jasmine.runtime.rhino;

import com.google.common.collect.Lists;
import jasmine.runtime.rhino.Loader;
import jasmine.runtime.vfs.VirtualFileSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.reflections.vfs.Vfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class LoaderTest {
    @Mock Scriptable scope;
    @Mock Context context;
    @Mock VirtualFileSystem virtualFileSystem;

    @Test
    public void shouldLoadFromVirtualFileSystem() throws IOException {
        Vfs.File fileOne = new MockFile("someFile.js");

        when(virtualFileSystem.findAll("someFile.js")).thenReturn(Lists.newArrayList(fileOne));

        Loader loader = new Loader(scope, context, virtualFileSystem);

        loader.loadFromVirtualFileSystem("someFile.js");

        verify(context).evaluateReader(eq(scope), any(Reader.class), eq("someFile.js"), eq(1), eq(null));
    }

    @Test
    public void shouldNotLoadSameFileTwiceWithLoad() throws IOException {
        Vfs.File fileOne = new MockFile("someFile.js");
        when(virtualFileSystem.findAll("someFile.js")).thenReturn(Lists.newArrayList(fileOne));
        Loader loader = new Loader(scope, context, virtualFileSystem);

        loader.loadFromVirtualFileSystem("someFile.js");
        verify(context).evaluateReader(eq(scope), any(Reader.class), eq("someFile.js"), eq(1), eq(null));

        loader.loadFromVirtualFileSystem("someFile.js");
        verifyNoMoreInteractions(context);
    }

    @Test
    public void shouldLoadAllFromVirtualFileSystem() throws IOException {
        Vfs.File fileOne = new MockFile("someFile.js");

        when(virtualFileSystem.findAll("someFile.js")).thenReturn(newArrayList(fileOne));

        Loader loader = new Loader(scope, context, virtualFileSystem);

        loader.loadAllFromVirtualFileSystem("someFile.js");

        verify(context).evaluateReader(eq(scope), any(Reader.class), eq("someFile.js"), eq(1), eq(null));
    }

    @Test
    public void shouldNotLoadSameFileTwiceWithLoadAll() throws IOException {
        Vfs.File fileOne = new MockFile("someFile.js");
        when(virtualFileSystem.findAll("someFile.js")).thenReturn(newArrayList(fileOne));
        Loader loader = new Loader(scope, context, virtualFileSystem);

        loader.loadAllFromVirtualFileSystem("someFile.js");
        verify(context).evaluateReader(eq(scope), any(Reader.class), eq("someFile.js"), eq(1), eq(null));

        loader.loadAllFromVirtualFileSystem("someFile.js");
        verifyNoMoreInteractions(context);
    }

    private class MockFile implements Vfs.File {
        private final String name;
        private final String relativePath;
        private final ByteArrayInputStream inputStream;

        public MockFile(String relativePath){
            this(relativePath, relativePath, new ByteArrayInputStream("".getBytes()));
        }

        public MockFile(String name, String relativePath, ByteArrayInputStream inputStream) {
            this.name = name;
            this.relativePath = relativePath;
            this.inputStream = inputStream;
        }

        @Override public String getName() {
            return name;
        }

        @Override public String getRelativePath() {
            return relativePath;
        }

        @Override public InputStream openInputStream() throws IOException {
            inputStream.reset();
            return inputStream;
        }
    }
}
