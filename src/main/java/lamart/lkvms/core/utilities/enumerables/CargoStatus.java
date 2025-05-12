package lamart.lkvms.core.utilities.enumerables;

import java.util.HashMap;
import java.util.Map;

public enum CargoStatus {
    UNDEFINED("000000001", "Не определен"),
    CONTAINER_LOADING("000000100", "Загрузка контейнера"),
    LEFT_PORT("000000150", "Вышел из порта"),
    ARRIVED_AT_PORT("000000200", "Прибыл в порт"),
    CUSTOMS_CLEARANCE("000000300", "Таможенное оформление"),
    CUSTOMS_DECLARATION("000000320", "Подача ДТ"),
    CUSTOMS_RELEASE("000000350", "Выпуск из таможни"),
    SHIPMENT_PLANNING("000000400", "планирование на отгрузку/вывоз"),
    CONTAINER_REMOVED("000000500", "Контейнер вывезен из порта"),
    RAILWAY_DOCS_SUBMITTED("000000600", "Документы на отгрузку по ЖД сданы"),
    RAILWAY_TRANSPORTATION("000000700", "ЖД доставка"),
    ARRIVED_AT_STATION("000000800", "Прибыл на станцию"),
    DELIVERED("000000900", "Груз доставлен получателю"),
    TRANSPORTATION_COMPLETED("000001300", "Перевозка окончена");

    private final String code;
    private final String description;

    private static final Map<String, CargoStatus> CODE_LOOKUP = new HashMap<>();

    static {
        for (CargoStatus s : values()) {
            CODE_LOOKUP.put(s.code, s);
        }
    }

    CargoStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CargoStatus valueOfCode(String code) {
        return CODE_LOOKUP.get(code);
    }

    public String getCode(){
        return code;
    }

    public String getDisplayName(){
        return description;
    }
}
