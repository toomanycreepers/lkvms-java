package lamart.lkvms.application.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CombinedPaymentDto {
    public Long id;
    public String name;
    public String number;
    public Double amount;
    public Double paid;
    public String currency;
    public String billingDate;
    public String paymentDate;
    public SkinnyOrganizationDto payer;
    public List<SkinnyCargoDto> cargos;
    public List<FilepathDto> invoicedocumentSet;
    public String financialEntity;
}
