package jasmine.runtime.vfs;

import org.reflections.vfs.Vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.collect.Lists.newArrayList;

class FileSource implements Source {
    @Override public Iterable<Vfs.File> findMatching(String regex) {
        return findExact(regex);
    }

    @Override public Iterable<Vfs.File> findExact(String path) {
        final File file = new File(path);
        if(file.isFile()){
            Vfs.File vfsFile = new FileBasedVfsFile(file);
            return newArrayList(vfsFile);
        }else{
            return newArrayList();
        }
    }

    private static class FileBasedVfsFile implements Vfs.File {
        private final File file;

        public FileBasedVfsFile(File file) {
            this.file = file;
        }

        @Override public String getName() {
            return file.getName();
        }

        @Override public String getRelativePath() {
            return file.getPath();
        }

        @Override public InputStream openInputStream() throws IOException {
            return new FileInputStream(file);
        }
    }
}
