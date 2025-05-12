package lamart.lkvms.core.utilities.enumerables;

public enum LogisticDocumentType {
    RAILWAY_WAYBILL("ЖД накладная"),
    TRANSPORT_WAYBILL("Транспортная накладная"),
    CONSIGMENT("Коносамент"),
    EMPTY_CONTAINER_HANDOVER_ACT("Акт сдачи порожнего контейнера");
    
    private final String typeName;
    
    LogisticDocumentType(String typeName) {
        this.typeName = typeName;
    }
    
    public String getTypeName() {
        return typeName;
    }
}
