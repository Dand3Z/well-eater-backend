package pl.well_eater.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogInRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}