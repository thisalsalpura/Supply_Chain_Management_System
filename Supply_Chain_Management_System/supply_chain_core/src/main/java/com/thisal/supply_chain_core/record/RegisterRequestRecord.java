package com.thisal.supply_chain_core.record;

import java.util.Set;

public record RegisterRequestRecord(String username, String password, Set<String> roleNames) {
}