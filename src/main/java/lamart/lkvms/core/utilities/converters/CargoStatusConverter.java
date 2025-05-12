package lamart.lkvms.core.utilities.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;

@Converter(autoApply = true)
public class CargoStatusConverter implements AttributeConverter<CargoStatus, String> {
    @Override
    public String convertToDatabaseColumn(CargoStatus status) {
        return status != null ? status.getCode() : "000000001";
    }

    @Override
    public CargoStatus convertToEntityAttribute(String code) {
        return CargoStatus.valueOfCode(code);
    }
}
