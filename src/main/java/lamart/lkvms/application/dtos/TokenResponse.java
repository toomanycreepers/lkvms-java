package lamart.lkvms.application.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenResponse {
    public String refresh;
    public String access;
}
