package lamart.lkvms.core.utilities.common;

public class PlaintextExtractor {
    private PlaintextExtractor(){}

    public static String extractPlainText(String html) {
        return html.replaceAll("<[^>]*>", "");
    }
}
