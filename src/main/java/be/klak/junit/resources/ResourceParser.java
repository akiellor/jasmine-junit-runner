package be.klak.junit.resources;

public class ResourceParser {
    public Resource parse(String path) {
        if(path.startsWith("classpath:")){
            return new ClasspathResource(path.replace("classpath:", ""));
        }else if(path.startsWith("file:") || path.startsWith("/")){
            return new FileResource(path);
        }
        throw new IllegalArgumentException();
    }
}
