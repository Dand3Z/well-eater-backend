package pl.well_eater.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingUpRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
