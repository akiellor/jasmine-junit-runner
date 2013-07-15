package jasmine.runtime;

import com.google.common.collect.Lists;
import jasmine.runtime.rhino.RhinoBackend;
import jasmine.runtime.vfs.InMemorySource;
import jasmine.runtime.vfs.ReflectionsSource;
import jasmine.runtime.vfs.VirtualFileSystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class AcceptanceTest {
    public static final Configuration configuration = new Configuration() {
        @Override
        public Collection<String> sources() {
            return Lists.newArrayList();
        }

        @Override
        public Collection<String> specs() {
            return Lists.newArrayList(".*Spec.js$");
        }

        @Override
        public VirtualFileSystem getFileSystem() {
            Vfs.File file = Mockito.mock(Vfs.File.class);
            when(file.getName()).thenReturn("sourceSpec.js");
            when(file.getRelativePath()).thenReturn("sourceSpec.js");
            try {
                when(file.openInputStream()).thenReturn(new ByteArrayInputStream("describe('foo', function(){ it('should be foo', function(){ expect('foo').toBe('foo'); }); });".getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            List<String> paths = Lists.newArrayList();
            for (URL url : ClasspathHelper.forJavaClassPath()) {
                paths.add(url.toExternalForm());
            }
            return new VirtualFileSystem(new ReflectionsSource(paths, VirtualFileSystem.Filters.JAVASCRIPT), new InMemorySource(Lists.newArrayList(file)));
        }
    };

    private final Backend backend;

    @Parameterized.Parameters
    public static Iterable<Backend[]> backends() {
        return new ArrayList<Backend[]>() {{
            add(new Backend[]{new RhinoBackend(configuration)});
        }};
    }

    public AcceptanceTest(Backend backend) {
        this.backend = backend;
    }

    @Test
    public void shouldReportLoadedTests() {
        Notifier notifier = Mockito.mock(Notifier.class);

        backend.execute(notifier);

        verify(notifier).started(Mockito.any(It.class));
        verify(notifier).pass(Mockito.any(It.class));
        verify(notifier).finished();
        verifyNoMoreInteractions(notifier);
    }
}
