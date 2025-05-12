package lamart.lkvms.application.services.logistic;

import java.math.BigDecimal;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lamart.lkvms.application.dtos.CargoStatusCountsDto;
import lamart.lkvms.application.dtos.CargoTrackingTimelineDto;
import lamart.lkvms.application.dtos.LatestCargoDto;
import lamart.lkvms.application.dtos.SlimCargoDto;
import lamart.lkvms.application.mappers.CargoMapper;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.repositories.CargoRepository;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    public boolean doesCargoExist(Long cargoId) {
        return cargoRepository.existsById(cargoId);
    }

    public Page<SlimCargoDto> getCargos(int page, int size, String status, Boolean dbu, String search, Organization org) {

        List<CargoStatus> statusList;
        if (status != null && !status.isEmpty())
            statusList = List.of(status.split(",")).stream().map(CargoStatus::valueOfCode).toList();
        else
            statusList = null;
        
        if (search == null){
            search = "";
        } 
        
        Page<Object[]> results = cargoRepository.findCargosWithSums(
            org,
            statusList,
            dbu,
            search,
            PageRequest.of(page, size, Sort.by("id"))
        );
        

        return results.map(arr -> 
        toSlimDto(
            (Cargo) arr[0],
            (BigDecimal) arr[1],
            (BigDecimal) arr[2]
        )
    );
    }

    public Cargo getCargoDetails(Long cargoId, Organization organization) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));
        if (cargo.getReceiver() == organization)
            return cargo;
        throw new EntityNotFoundException(String.format("No cargo with this id in org %d.", organization.getId()));
    }

    public CargoTrackingTimelineDto getCargoTracking(Long cargoId, Organization organization) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));
        if (cargo.getReceiver() == organization)
            return CargoTrackingTimelineDto.fromEntity(cargo);
        throw new EntityNotFoundException("No cargo with this id in your organization.");
    }


    public List<LatestCargoDto> getLatestCargos(Organization organization) throws BadRequestException {
        if (organization == null) {
            throw new BadRequestException("Organization not selected");
        }

        return cargoRepository.findTop10ByReceiverOrderByStatusAscDateUpdatedDesc(organization)
            .stream()
            .map(CargoMapper::toLatestDto)
            .toList();
    }

    public List<Cargo> getCargosByOrganization(Organization organization){
        return cargoRepository.findAllByReceiver(organization);
    }

    public CargoStatusCountsDto getStatusCounts(Organization org){
        return new CargoStatusCountsDto(
            cargoRepository.countActiveByReceiver(org),
            cargoRepository.countActiveByReceiverAndStatus(org, CargoStatus.TRANSPORTATION_COMPLETED)
        );
    }

    private SlimCargoDto toSlimDto(Cargo cargo, BigDecimal totalAmount, BigDecimal totalPaid) {        
        return new SlimCargoDto(
            cargo.getId(),
            cargo.getOrderNumber(),
            cargo.getNumber(),
            cargo.getRoute(),
            cargo.getStatus().getCode(),
            cargo.getStatus() == CargoStatus.TRANSPORTATION_COMPLETED && 
                totalPaid.compareTo(totalAmount) < 0,
            cargo.getStatusChangedTime(),
            cargo.getReadinessDate(),
            cargo.getLoadingDate(),
            cargo.getEtdDate(),
            cargo.getBillOfLandingToBrokerDate(),
            cargo.getWarehouseClosingDate(),
            cargo.getKtkRemovalFromPortDate(),
            cargo.getTransportationEndedDate(),
            cargo.getEtaDate(),
            cargo.getArrivedInPortDate(),
            cargo.getCustomsDeclarationSubmissionDate(),
            cargo.getCustomsReleaseDate(),
            cargo.getReadyForRailroadDate(),
            cargo.getLoadedOntoRailroadDate(),
            cargo.getRailroadStationArrivalDate(),
            cargo.getWarehouseArrivalDate()
        );
    }
}
