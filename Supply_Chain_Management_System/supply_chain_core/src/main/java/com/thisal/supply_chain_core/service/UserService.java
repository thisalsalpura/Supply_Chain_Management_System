package com.thisal.supply_chain_core.service;

import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.record.UserPrincipalRecord;
import jakarta.ejb.Local;

import java.util.Optional;
import java.util.Set;

@Local
public interface UserService {

    ResponseModel register(String username, String password, Set<String> roleNames);

    Optional<UserPrincipalRecord> login(String username, String password);

}