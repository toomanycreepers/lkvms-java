package lamart.lkvms.core.utilities.enumerables;

import java.util.HashMap;
import java.util.Map;

public enum CargoSizeType {
    HC40(1, "40HC"),
    DC20(2, "20DC"),
    FR20(3, "20FR"),
    HC20(4, "20HC"),
    OT20(5, "20OT"),
    DC40(6, "40DC"),
    FR40(7, "40FR"),
    OT40(8, "40OT"),
    HC45(9, "45HC"),
    LCL(10, "Сборный"),
    HC40_DC20(11, "40HC+20DC"),
    DC20_HC40(12, "20DC+40HC"),
    REF40(13, "40REF"),
    TRUCK_80M3(14, "truck 80m3"),
    TRUCK_110M3(15, "truck 110m3"),
    NOT_SPECIFIED(0, "Не указан");

    private final int code;
    private final String description;

    private static final Map<Integer, CargoSizeType> CODE_LOOKUP = new HashMap<>();

    static {
        for (CargoSizeType s : values()) {
            CODE_LOOKUP.put(s.code, s);
        }
    }

    CargoSizeType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CargoSizeType valueOfCode(int code) {
        return CODE_LOOKUP.get(code);
    }

    public String getDisplayName(){
        return description;
    }
}
