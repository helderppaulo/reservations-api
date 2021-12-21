package campsite.reservations.core;

import campsite.reservations.core.repository.TransactionProvider;

import java.util.function.Supplier;

public class NoopTransactionProvider implements TransactionProvider {

    @Override
    public <T> T executeAtomically(final Supplier<T> supplier) {
        return supplier.get();
    }
};