package campsite.reservations.application.resources;

import campsite.reservations.core.base.Result;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
@UtilityClass
public class ResultResponseUnwrapper {

    public static <T> ResponseEntity<ResourceResult<T>> unwrap(final Result<T> result) {
        if (result.isSuccess()) {
            return result.map(ResourceResult::success).unwrap(ResponseEntity::ok);
        } else if (result.isBusinessError()) {
            final ResourceResult<T> body = result.unwrapBusinessError(ResourceResult::failure);
            return ResponseEntity.badRequest().body(body);
        } else {
            final ResourceResult<T> body = result.unwrapException((e) -> {
                log.error("result=unexpected_error", e);
                return ResourceResult.failure(List.of());
            });
            return ResponseEntity.internalServerError().body(body);
        }
    }
}
