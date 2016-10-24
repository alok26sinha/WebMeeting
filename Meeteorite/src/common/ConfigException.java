package common;

public class ConfigException extends UncheckedException {
    public ConfigException(String s) {
        super(s);
    }

    public ConfigException(String s, Exception e) {
        super(s, e);
    }
}
