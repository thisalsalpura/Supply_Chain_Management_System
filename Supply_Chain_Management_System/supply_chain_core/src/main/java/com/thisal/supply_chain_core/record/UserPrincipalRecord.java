package com.thisal.supply_chain_core.record;

import java.util.List;

public record UserPrincipalRecord(String username, List<String> roles) {
}