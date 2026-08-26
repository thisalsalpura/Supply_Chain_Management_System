package com.thisal.supply_chain_ejb.ejb.interceptor;

import com.thisal.supply_chain_core.annotation.Audited;
import com.thisal.supply_chain_core.annotation.Console;
import jakarta.annotation.Priority;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.time.LocalDateTime;
import java.util.Arrays;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@Audited
public class AuditInterceptor {

    @Inject
    @Console
    private Event<String> logEvent;

    @AroundInvoke
    public Object audit(InvocationContext ctx) throws Exception {
        String className = ctx.getTarget().getClass().getSimpleName();
        String methodName = ctx.getMethod().getName();
        LocalDateTime start = LocalDateTime.now();
        logEvent.fire(className + "." + methodName + " called with " + Arrays.asList(ctx.getParameters()) + " parameters at " + start + "!");
        try {
            Object result = ctx.proceed();
            logEvent.fire(className + "." + methodName + " completed Successfully!");
            return result;
        } catch (Exception e) {
            logEvent.fire(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}