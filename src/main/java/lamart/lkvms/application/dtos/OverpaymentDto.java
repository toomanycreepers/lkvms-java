package lamart.lkvms.application.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class OverpaymentDto {
    public Long id;
    public double paid;
    public String currency;
    public LocalDate dateCreated;
    public SkinnyOrganizationDto payer;
}
