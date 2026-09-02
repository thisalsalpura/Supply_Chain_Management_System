package com.thisal.supply_chain_ejb.ejb.user;

import com.thisal.supply_chain_core.annotation.Audited;
import com.thisal.supply_chain_core.annotation.Console;
import com.thisal.supply_chain_core.annotation.Validated;
import com.thisal.supply_chain_core.entity.User;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.enums.Role;
import com.thisal.supply_chain_core.exception.UserAlreadyExistsException;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.record.UserPrincipalRecord;
import com.thisal.supply_chain_core.service.UserService;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Audited
@Validated
public class UserBean implements UserService {

    @PersistenceContext(unitName = "supply_chainPU")
    private EntityManager entityManager;

    @Inject
    private com.thisal.supply_chain_core.util.security.PasswordHasher passwordHasher;

    @Inject
    @Console
    private Event<String> logEvent;

    @Override
    public ResponseModel register(String username, String password, Set<String> roleNames) {
        boolean existingUser = !entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username.trim().toLowerCase())
                .getResultList().isEmpty();
        if (existingUser) {
            logEvent.fire("User with " + username + " is already Registered!");
            throw new UserAlreadyExistsException("User with " + username + " is already Registered!");
        } else {
            Set<Role> roles = roleNames.stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
            User user = User.builder()
                    .username(username.trim().toLowerCase())
                    .password(passwordHasher.hash(password))
                    .roles(roles)
                    .build();
            try {
                entityManager.persist(user);
                entityManager.flush();
                logEvent.fire("User created Successfully!");
                return ResponseModel.builder()
                        .status(ResponseStatus.OK)
                        .message("User created Successfully!")
                        .build();
            } catch (PersistenceException e) {
                logEvent.fire(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Optional<UserPrincipalRecord> login(String username, String password) {
        User user = entityManager.createNamedQuery("User.findByUsername", User.class)
                .setParameter("username", username.trim().toLowerCase())
                .getResultList().stream().findFirst().orElse(null);
        if (user != null) {
            if (passwordHasher.verify(password, user.getPassword())) {
                List<String> roles = user.getRoles().stream()
                        .map(Enum::name)
                        .toList();
                return Optional.of(new UserPrincipalRecord(user.getUsername(), roles));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

}