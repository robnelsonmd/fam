package fam.core.util;

public class StringUtil {
    public static boolean isEmptyString(String string) {
        return (string == null) || string.trim().isEmpty();
    }
}
