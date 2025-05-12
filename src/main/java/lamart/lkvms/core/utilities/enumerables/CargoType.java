package lamart.lkvms.core.utilities.enumerables;

import java.util.HashMap;
import java.util.Map;

public enum CargoType {
    IMPORT_CTK(1, "Импорт (сборный ктк)"),
    IMPORT_CARGO(2, "Импорт (сборный груз)"),
    IMPORT_PORT_RAIL(3, "Импорт (порт + жд)"),
    IMPORT_PORT_AUTO(4, "Импорт (порт + авто)"),
    IMPORT_SEA_RAIL(5, "Импорт (море + жд)"),
    IMPORT_SEA_AUTO(6, "Импорт (море + авто)"),
    DOMESTIC(7, "Внутрироссийская перевозка"),
    IMPORT_RAIL_AUTO(8, "Импорт (жд + авто)"),
    IMPORT_AIR_AUTO(9, "Импорт (авиа + авто)"),
    IMPORT_AUTO(10, "Импорт (авто)"),
    CUSTOMS_OUR_CONTRACT(11, "Таможенное оформление (под наш контракт)"),
    CUSTOMS_BROKER(12, "Таможенное оформление (под печать брокера)"),
    CUSTOMS_CLIENT(13, "Таможенное оформление (под печать клиента)"),
    IMPORT_SEA_AIR(14, "Импорт (море + авиа)"),
    IMPORT_EXW_FREIGHT(15, "Импорт (EXW + фрахт)"),
    IMPORT_SEA(16, "Импорт (Море)"),
    EXPORT_SEA(17, "Экспорт (Море)"),
    WAREHOUSE_VMS(18, "Склад ВМС");

    private final int code;
    private final String description;

    private static final Map<Integer, CargoType> CODE_LOOKUP = new HashMap<>();

    static {
        for (CargoType t : values()) {
            CODE_LOOKUP.put(t.code, t);
        }
    }

    CargoType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CargoType valueOfCode(int code) {
        return CODE_LOOKUP.get(code);
    }

    public String getDisplayName(){
        return description;
    }
}
