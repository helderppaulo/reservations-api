package campsite.reservations.application.resources;

import campsite.reservations.core.domain.BusinessViolation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceResult<T> {
    T data;
    boolean success;
    @Builder.Default
    List<String> errors = List.of();

    public static <U> ResourceResult<U> success(final U data) {
        return ResourceResult.<U>builder()
                .success(true)
                .data(data).build();
    }

    public static <U> ResourceResult<U> failure(final List<BusinessViolation> violations) {
        return ResourceResult.<U>builder()
                .success(false)
                .errors(violations.stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .build();
    }
}
