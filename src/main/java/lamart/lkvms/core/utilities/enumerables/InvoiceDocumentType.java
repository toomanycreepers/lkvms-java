package lamart.lkvms.core.utilities.enumerables;

public enum InvoiceDocumentType {
    INVOICE("Счета"),
    RECONCILIATION_ACT("Акт сверки"),
    UNIVERSAL_TRANSFER_DOCUMENT("УПД"),
    RENDERED_SERVICE_ACT("Акт оказанных услуг");

    private final String typeName;

    InvoiceDocumentType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
