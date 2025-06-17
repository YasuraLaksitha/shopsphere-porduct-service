package com.shopsphere.productservice.audit;

import com.shopsphere.productservice.context.UserContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component(value = "auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(UserContext.get())
                .or(() -> Optional.of("PRODUCT_MS"));
    }
}
