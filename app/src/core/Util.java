package core;

public class Util {
    public static String sanitizeFilename(String name) {
        if (name == null) {
            return "_";
        }
        return name.replaceAll("[:\\\\/*?|<>]", "");
    }
}
