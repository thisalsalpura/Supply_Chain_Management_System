package com.thisal.supply_chain_ejb.ejb.interceptor;

import com.thisal.supply_chain_core.annotation.Validated;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 10)
@Validated
public class ValidateInterceptor {

    @AroundInvoke
    public Object validate(InvocationContext ctx) throws Exception {
        for (Object param : ctx.getParameters()) {
            if (param instanceof String stringParam && stringParam.trim().isEmpty()) {
                throw new IllegalArgumentException("Validation failed on " + ctx.getMethod().getName() + "(). String parameter cannot be Empty!");
            }
        }
        return ctx.proceed();
    }

}