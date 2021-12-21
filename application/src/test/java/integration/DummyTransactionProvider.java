package integration;

import campsite.reservations.core.repository.TransactionProvider;

import java.util.function.Supplier;

public class DummyTransactionProvider implements TransactionProvider {

    @Override
    public <T> T executeAtomically(final Supplier<T> supplier) {
        return supplier.get();
    }
}
