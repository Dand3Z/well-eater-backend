package pl.well_eater.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@ToString
public final class ErrorDTO {

    private final Map<String, String> messages;

    public ErrorDTO(final Map<String, String> messages){
        this.messages = Collections.unmodifiableMap(messages);
    }

    public ErrorDTO(final String key, final String value) {
        this.messages = Collections.singletonMap(key, value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<String, String> messages = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder put(final String key, final String value) {
            messages.put(key, value);
            return this;
        }

        public ErrorDTO build() {
            if(messages.isEmpty())
                throw new IllegalStateException("ErrorDto builder cannot be empty");

            return new ErrorDTO(messages);
        }
    }
}