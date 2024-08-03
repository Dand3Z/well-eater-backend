package pl.well_eater.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private Set<String> roles;
}
