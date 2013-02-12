package be.klak.junit.resources;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileResource implements Resource {
    private final File file;

    public FileResource(String path){
        this(new File(path));
    }

    public FileResource(File file){
        this.file = file;
    }

    public URL getURL(){
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBaseName() {
        String external = getURL().toExternalForm();
        return external.substring(external.lastIndexOf("/") + 1);
    }

    @Override public FileResource asFileResource() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileResource that = (FileResource) o;

        if (file != null ? !file.equals(that.file) : that.file != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return file != null ? file.hashCode() : 0;
    }

    public static FileResource from(Resource initial, File output) {
        File outputFile = new File(output, initial.getBaseName());
        try {
            FileUtils.writeStringToFile(outputFile, IOUtils.toString(initial.getURL().openStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileResource(outputFile);
    }

    public static List<FileResource> files(File root, String... files) {
        return files(root, Arrays.asList(files));
    }

    private static List<FileResource> files(File root, List<String> files) {
        List<FileResource> resources = new ArrayList<FileResource>();
        for(String file : files){
            resources.add(new FileResource(new File(root, file)));
        }
        return resources;
    }
}
