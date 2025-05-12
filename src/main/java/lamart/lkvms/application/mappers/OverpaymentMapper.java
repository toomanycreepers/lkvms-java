package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.OverpaymentDto;
import lamart.lkvms.core.entities.logistic.Overpayment;

public class OverpaymentMapper {
    private OverpaymentMapper(){}

    public static OverpaymentDto toDto(Overpayment op){
        return new OverpaymentDto(
            op.getId(),
            op.getPaid().doubleValue(),
            op.getCurrency(),
            op.getDateCreated().toLocalDate(),
            OrganizationMapper.toDto(op.getPayer())
            );
    }
}
