package computer.heather.goagnostic.internal.util;

public class StringUtils {
    

    public static String lowerWithSlashes(String input) {
        return input.replace(".", "/").replace(" ", "/").toLowerCase();
    }

    public static String lowerWithPeriods(String input) {
        return input.replace("/", ".").replace(" ", ".").toLowerCase();
    }

    public static String standardWithSlashes(String input) {
        return input.replace(".", "/").replace(" ", "/");
    }


}
