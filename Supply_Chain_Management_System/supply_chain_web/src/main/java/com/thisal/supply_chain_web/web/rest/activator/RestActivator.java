package com.thisal.supply_chain_web.web.rest.activator;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DeclareRoles({"ADMIN", "WAREHOUSE_MANAGER", "VENDOR", "USER"})
public class RestActivator extends Application {
}