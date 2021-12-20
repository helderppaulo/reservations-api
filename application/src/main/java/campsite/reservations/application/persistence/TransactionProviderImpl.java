package campsite.reservations.application.persistence;

import campsite.reservations.core.repository.TransactionProvider;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.function.Supplier;

@Component
public class TransactionProviderImpl implements TransactionProvider {

    @Override
    @Transactional
    public <T> T executeTransactionally(final Supplier<T> supplier) {
        return supplier.get();
    }
}
