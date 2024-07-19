package pl.well_eater.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import pl.well_eater.dto.ErrorDTO;

@ToString
@Getter
public class UnauthorizedRequestException extends AbstractRestException {

    public UnauthorizedRequestException() {
        this(new ErrorDTO("request", "unauthorized"));
    }

    public UnauthorizedRequestException(final @NonNull ErrorDTO validation) {
        super(validation);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}