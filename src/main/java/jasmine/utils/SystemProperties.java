package jasmine.utils;

public class SystemProperties {
    public String get(String key) {
        return System.getProperty(key);
    }
}
