package pl.well_eater.exception;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import pl.well_eater.dto.ErrorDTO;

@Getter
public abstract class AbstractRestException extends RuntimeException {

    protected final ErrorDTO validationDto;

    AbstractRestException(final @NonNull String key, final @NonNull String value) {
        this(new ErrorDTO(key, value));
    }

    AbstractRestException(final @NonNull ErrorDTO validation) {
        super(validation.toString());
        this.validationDto = validation;
    }

    public abstract HttpStatus getStatus();
}