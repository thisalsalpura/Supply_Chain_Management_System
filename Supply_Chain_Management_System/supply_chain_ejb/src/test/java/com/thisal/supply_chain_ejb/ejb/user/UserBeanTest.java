package com.thisal.supply_chain_ejb.ejb.user;

import com.thisal.supply_chain_core.entity.User;
import com.thisal.supply_chain_core.enums.ResponseStatus;
import com.thisal.supply_chain_core.enums.Role;
import com.thisal.supply_chain_core.exception.UserAlreadyExistsException;
import com.thisal.supply_chain_core.model.ResponseModel;
import com.thisal.supply_chain_core.record.UserPrincipalRecord;
import com.thisal.supply_chain_core.util.security.PasswordHasher;
import jakarta.enterprise.event.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Event<String> logEvent;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TypedQuery<User> userQuery;

    @InjectMocks
    private UserBean userBean;

    @BeforeEach
    void setUp() {
        lenient().when(entityManager.createNamedQuery("User.findByUsername", User.class)).thenReturn(userQuery);
        lenient().when(userQuery.setParameter(eq("username"), anyString())).thenReturn(userQuery);
    }

    @Test
    void registerShouldCreateNewUserSuccessfully() {
        when(userQuery.getResultList()).thenReturn(Collections.emptyList());
        when(passwordHasher.hash("secret123")).thenReturn("hashed-password");

        ResponseModel response = userBean.register("Test", "secret123", Set.of("ADMIN"));

        assertEquals(ResponseStatus.OK, response.getStatus());
        assertEquals("User created Successfully!", response.getMessage());
        verify(entityManager).persist(any(User.class));
        verify(logEvent).fire("User created Successfully!");
    }

    @Test
    void registerShouldFailWhenUserAlreadyExists() {
        User existingUser = User.builder()
                .username("Test")
                .password("hashed-password")
                .roles(Set.of(Role.ADMIN))
                .build();
        when(userQuery.getResultList()).thenReturn(List.of(existingUser));

        assertThrows(UserAlreadyExistsException.class,
                () -> userBean.register("Test", "secret123", Set.of("ADMIN")));
        verify(logEvent).fire("User with Test is already Registered!");
    }

    @Test
    void loginShouldReturnUserPrincipalWhenPasswordMatches() {
        User user = User.builder()
                .username("Test")
                .password("hashed-password")
                .roles(Set.of(Role.ADMIN, Role.USER))
                .build();
        when(userQuery.getResultList()).thenReturn(List.of(user));
        when(passwordHasher.verify("secret123", "hashed-password")).thenReturn(true);

        Optional<UserPrincipalRecord> principal = userBean.login("Test", "secret123");

        assertTrue(principal.isPresent());
        assertEquals("Test", principal.get().username());
        assertTrue(principal.get().roles().contains("ADMIN"));
    }

}