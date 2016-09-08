package core;

public class Util {
    public static String sanitizeFilename(String name) {
       if (name == null) {
            return "_";
        }
         /*
        return name.replaceAll("[:\\\\*//*?|<>]", "");*/

        String[] forbiddenSymbols = new String[]{"<", ">", ":", ".", "\"", "/", "\\", "|", "?", "*"}; // для windows
        String result = name;
        for (String forbiddenSymbol : forbiddenSymbols) {
            result = result.replace(forbiddenSymbol, "");
        }
        // амперсанд в названиях передаётся как '& amp', приводим его к читаемому виду
        return result;
    }
}
