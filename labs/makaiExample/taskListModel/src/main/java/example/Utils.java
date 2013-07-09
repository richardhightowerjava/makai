package example;

public class Utils {

    public static void notEmpty(CharSequence string) {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException("Argument must not be null or empty");
        }

    }
    public static void notNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument must not be null");
        }

    }

}
