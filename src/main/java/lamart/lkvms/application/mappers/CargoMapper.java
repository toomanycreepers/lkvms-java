package lamart.lkvms.application.mappers;

import java.util.Collection;
import java.util.List;

import lamart.lkvms.application.dtos.LatestCargoDto;
import lamart.lkvms.application.dtos.SkinnyCargoDto;
import lamart.lkvms.core.entities.logistic.Cargo;

public class CargoMapper {
    private CargoMapper(){}

    public static SkinnyCargoDto toDto(Cargo cargo){
        return new SkinnyCargoDto(cargo.getId(), cargo.getNumber());
    }

    public static LatestCargoDto toLatestDto(Cargo cargo){
        return new LatestCargoDto(cargo.getId(), cargo.getNumber(), cargo.getStatus().getCode());
    }

    public static List<SkinnyCargoDto> toDtoList(Collection<Cargo> cargos){
        return cargos.stream()
                          .map(CargoMapper::toDto)
                          .toList();
    }
}
