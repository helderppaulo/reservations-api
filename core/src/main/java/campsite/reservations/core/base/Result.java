package campsite.reservations.core.base;

import campsite.reservations.core.domain.BusinessViolation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableList;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Result<T> {
    private final T content;
    private final List<BusinessViolation> violations;
    private final Exception exception;

    public static <U> Result<U> successful(final U content) {
        return new Result<>(content, List.of(), null);
    }

    public static <U> Result<U> businessError(final List<BusinessViolation> violations) {
        return new Result<U>(null, unmodifiableList(violations), null);
    }

    public static <U> Result<U> businessError(final BusinessViolation violation) {
        return new Result<U>(null, List.of(violation), null);
    }

    public static <U> Result<U> fromSupplier(final Supplier<U> supplier) {
        try {
            return successful(supplier.get());
        } catch (final Exception e) {
            return new Result<>(null, List.of(), e);
        }
    }

    public <U> Result<U> map(final Function<T, U> fn) {
        if (!isSuccess()) return (Result<U>) this;
        try {
            return successful(fn.apply(this.content));
        } catch (final Exception e) {
            return new Result<>(null, List.of(), e);
        }
    }

    public <U> Result<U> flatMap(final Function<T, Result<U>> fn) {
        if (!isSuccess()) return (Result<U>) this;
        try {
            return fn.apply(this.content);
        } catch (final Exception e) {
            return new Result<>(null, List.of(), e);
        }
    }

    public <U> U unwrap(final Function<T, U> fn) {
        return fn.apply(this.content);
    }

    public <U> U unwrapBusinessError(final Function<List<BusinessViolation>, U> fn) {
        return fn.apply(this.violations);
    }

    public List<BusinessViolation> unwrapBusinessError() {
        return this.violations;
    }

    public <U> U unwrapException(final Function<Exception, U> fn) {
        return fn.apply(this.exception);
    }

    public Exception unwrapException() {
        return this.exception;
    }

    public boolean isSuccess() {
        return !this.isBusinessError() && !this.isException();
    }

    public boolean isException() {
        return this.exception != null;
    }

    public boolean isBusinessError() {
        return !this.violations.isEmpty();
    }
}
