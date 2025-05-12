package lamart.lkvms.core.entities.logistic;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lamart.lkvms.core.baseclasses.TextEntityBase;
import lamart.lkvms.core.utilities.enumerables.CargoSizeType;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;
import lamart.lkvms.core.utilities.enumerables.CargoType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_cargo", indexes = {
    @Index(name = "idx_cargo_1c", columnList = "ref_1c"),
    @Index(name = "idx_cargo_number", columnList = "number"),
    @Index(name = "idx_cargo_waybill_number", columnList = "waybill_number"),
    @Index(name = "idx_cargo_bill_of_landing", columnList = "bill_of_landing"),
    @Index(name = "idx_cargo_consignment_note", columnList = "consignment_note"),
    @Index(name = "idx_cargo_order", columnList = "order_number")
})
@Getter
@Setter
public class Cargo extends TextEntityBase{
    @Column(length = 512)
    private String number;

    @Column
    private LocalDateTime date; 

    @Column(name = "waybill_number", length = 510)
    private String waybillNumber;

    @Column(name = "bill_of_landing", length = 510)
    private String billOfLanding;

    @Column(name = "consignment_note", length = 510)
    private String consignmentNote;

    @Column(length = 510)
    private String sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Organization receiver;

    @Column(length = 510)
    private String importer;

    @Column(name = "client_number", length = 510)
    private String clientNumber;

    @Enumerated(EnumType.ORDINAL)
    private CargoType type;

    @Column(length = 9)
    private CargoStatus status = CargoStatus.UNDEFINED;

    @Column(name = "status_changed_time")
    private LocalDateTime statusChangedTime;

    @Column(length = 1022)
    private String carrying;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "size_type")
    private CargoSizeType sizeType = CargoSizeType.NOT_SPECIFIED;

    @Column(length = 510)
    private String route;

    @Column(name = "departure_point", length = 510)
    private String departurePoint;

    @Column(name = "destination_point", length = 510)
    private String destinationPoint;

    @Column(name = "transshipment_port", length = 510)
    private String transshipmentPort;

    @Column(length = 510)
    private String line;

    @Column(length = 510)
    private String dislocation;

    @Column(name = "is_mbl_released")
    private Boolean isMblReleased;

    @Column(name = "readiness_date")
    private LocalDateTime readinessDate;

    @Column(name = "loading_date")
    private LocalDateTime loadingDate;

    @Column(name = "etd_date")
    private LocalDateTime etdDate;

    @Column(name = "bill_of_landing_to_broker_date")
    private LocalDateTime billOfLandingToBrokerDate;

    @Column(name = "warehouse_closing_date")
    private LocalDateTime warehouseClosingDate;

    @Column(name = "ktk_removal_from_port_date")
    private LocalDateTime ktkRemovalFromPortDate;

    @Column(name = "transportation_ended_date")
    private LocalDateTime transportationEndedDate;

    @Column(name = "eta_date")
    private LocalDateTime etaDate;

    @Column(name = "arrived_in_port_date")
    private LocalDateTime arrivedInPortDate;

    @Column(name = "customs_declaration_submission_date")
    private LocalDateTime customsDeclarationSubmissionDate;

    @Column(name = "customs_release_date")
    private LocalDateTime customsReleaseDate;

    @Column(name = "ready_for_railroad_date")
    private LocalDateTime readyForRailroadDate;

    @Column(name = "loaded_onto_railroad_date")
    private LocalDateTime loadedOntoRailroadDate;

    @Column(name = "railroad_station_arrival_date")
    private LocalDateTime railroadStationArrivalDate;

    @Column(name = "warehouse_arrival_date")
    private LocalDateTime warehouseArrivalDate;

    @Column(length = 2046)
    private String comment;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "manager_full_name", length = 510)
    private String managerFullName;

    @Column(name = "manager_phone_number", length = 255)
    private String managerPhoneNumber;

    @Column(name = "manager_email", length = 255)
    private String managerEmail;

    @ManyToMany(mappedBy = "cargos")
    private Set<Invoice> invoices;

    @Transient
    private BigDecimal totalAmount;

    @Transient
    private BigDecimal totalPaid;

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        if (statusChangedTime == null) {
            statusChangedTime = LocalDateTime.now();
        }
    }

    public boolean isCargoDeliveredButHasUnpaidInvoices() {
        if (totalAmount == null || totalPaid == null) {
            throw new NullPointerException();
        }
        return status == CargoStatus.TRANSPORTATION_COMPLETED && totalAmount.compareTo(totalPaid)>0;
    }

    @Override
    public String toString() {
        return number != null ? number : "Без номера";
    }  
}
    