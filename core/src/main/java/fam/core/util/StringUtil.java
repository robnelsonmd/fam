package fam.core.util;

public class StringUtil {
    public enum CapitalizationStrategy { ALL, FIRST_LETTER };

    public static String capitalizeString(String string, CapitalizationStrategy capitalizationStrategy) {
        switch(capitalizationStrategy) {
            case ALL:
                return string.toUpperCase();
            case FIRST_LETTER:
                return String.format("%s%s",string.substring(0,1).toUpperCase(),string.substring(1));
            default:
                throw new IllegalArgumentException(String.format("Invalid capitalization strategy: %s",capitalizationStrategy));
        }
    }

    public static boolean isEmptyString(String string) {
        return (string == null) || string.trim().isEmpty();
    }
}
