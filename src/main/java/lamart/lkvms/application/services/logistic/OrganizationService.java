package lamart.lkvms.application.services.logistic;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lamart.lkvms.application.dtos.ManagerDto;
import lamart.lkvms.application.dtos.OrganizationWithMembersDto;
import lamart.lkvms.application.dtos.SkinnyOrganizationDto;
import lamart.lkvms.application.mappers.OrganizationMapper;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.repositories.CargoRepository;
import lamart.lkvms.core.repositories.OrganizationRepository;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepo;
    private final CargoRepository cargoRepository;

    OrganizationService(OrganizationRepository organizationRepo, CargoRepository cargoRepository) {
        this.organizationRepo = organizationRepo;
        this.cargoRepository = cargoRepository;
    }

    public List<SkinnyOrganizationDto> getUserOrganizations(UUID userId){
        return organizationRepo.findAllOrganizationsByUserIdAndNotDeleted(userId)
                .stream()
                .map(OrganizationMapper::toDto)
                .toList();
    }

    public Organization getOrganizationById(Long orgId){
        return organizationRepo.findById(orgId).orElseThrow(
            () -> new EntityNotFoundException("Incorrect organization id provided.")
        );
    }

    public OrganizationWithMembersDto getOrganizationDetails(Long orgId){
        Organization org = organizationRepo.findById(orgId).orElseThrow(
            () -> new EntityNotFoundException("Incorrect organization id provided.")
        );
        return OrganizationMapper.toMembersDto(org);
    }
    
    public ManagerDto getCurrentManagerForUser(Long orgId){
        Cargo cargo = cargoRepository.findTopByReceiverIdAndManagerFullNameIsNotNullAndManagerPhoneNumberIsNotNullOrderByDateUpdatedDesc(orgId)
        .orElseThrow(() -> new EntityNotFoundException("No manager"));
        return new ManagerDto(
            cargo.getManagerFullName(), 
            cargo.getManagerPhoneNumber(), 
            cargo.getManagerEmail()
        );
    }
}
