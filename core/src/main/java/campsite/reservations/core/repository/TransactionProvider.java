package campsite.reservations.core.repository;

import java.util.function.Supplier;

public interface TransactionProvider {

    <T> T executeTransactionally(Supplier<T> supplier);
}
