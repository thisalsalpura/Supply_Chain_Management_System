package com.thisal.supply_chain_ejb.ejb.audit;

import com.thisal.supply_chain_core.entity.AuditLogEntry;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class AuditLogBean {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logFailedOperation(String operation, String reason) {
        AuditLogEntry auditLogEntry = AuditLogEntry.builder()
                .operation(operation)
                .reason(reason)
                .build();
        entityManager.persist(auditLogEntry);
        entityManager.flush();
    }

}