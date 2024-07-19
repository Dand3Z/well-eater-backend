package pl.well_eater.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import pl.well_eater.dto.ErrorDTO;

@ToString
@Getter
public class EntityNotFoundException extends AbstractRestException {

    public EntityNotFoundException(final @NonNull String entity) {
        this(new ErrorDTO(entity, "not-found"));
    }

    public EntityNotFoundException() {
        this(new ErrorDTO("request", "entity-not-found"));
    }

    public EntityNotFoundException(final @NonNull ErrorDTO validation) {
        super(validation);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}