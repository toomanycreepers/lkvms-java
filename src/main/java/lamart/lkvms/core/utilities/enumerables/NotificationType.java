package lamart.lkvms.core.utilities.enumerables;

public enum NotificationType {
    TELEGRAM("TG", "Telegram"),
    EMAIL("EM", "Email"),
    DESKTOP("DT", "Desktop");

    private final String code;
    private final String displayName;

    NotificationType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
