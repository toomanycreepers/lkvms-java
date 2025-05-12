package lamart.lkvms.core.utilities.common;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineEnumerator {

    private LineEnumerator(){}

    public static String enumerateLines(String fileString) {
        String[] lines = fileString.split("\n");
        int maxValue = lines.length;
        int maxDigitCount = maxValue > 0 ? (int) Math.log10(maxValue) + 1 : 1;
        String formatString = "%" + maxDigitCount + "d";
        
        return IntStream.range(0, lines.length)
            .mapToObj(i -> String.format(formatString + ": %s", i + 1, lines[i]))
            .collect(Collectors.joining("\n"));
    }
}
