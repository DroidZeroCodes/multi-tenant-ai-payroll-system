package org.droid.zero.multitenantaipayrollsystem.system;

import org.droid.zero.multitenantaipayrollsystem.system.context.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class TenantExecutor {

    private final TransactionTemplate transactionTemplate;

    // Inject the PlatformTransactionManager to build a template with REQUIRES_NEW
    public TenantExecutor(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public <T> T executeAsTenant(UUID targetTenantId, Supplier<T> action) {
        UUID originalTenantId = TenantContext.getTenantId();

        try {
            // 1. Switch the Context FIRST
            TenantContext.setTenantId(targetTenantId);

            // 2. NOW start the Transaction.
            // Hibernate will resolve the Tenant ID *now*, seeing the correct new ID.
            return transactionTemplate.execute(status -> action.get());

        } finally {
            // 3. Cleanup
            if (originalTenantId != null) {
                TenantContext.setTenantId(originalTenantId);
            } else {
                TenantContext.clear();
            }
        }
    }

    public void runAsTenant(UUID targetTenantId, Runnable action) {
        executeAsTenant(targetTenantId, () -> {
            action.run();
            return null;
        });
    }
}