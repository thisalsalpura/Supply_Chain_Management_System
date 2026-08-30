package com.thisal.supply_chain_core.util.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.eclipse.microprofile.config.ConfigProvider;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JWTUtil {

    private static final String SECRET = ConfigProvider.getConfig()
            .getOptionalValue("jwt.secret", String.class)
            .orElseThrow(() -> new IllegalStateException("jwt.secret is not configured! Set JWT_SECRET Environment Variable."));
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);
    private static final long EXPIRATION_TIME = 3600;
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();

    public static String generateToken(String username, List<String> roles) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(EXPIRATION_TIME)))
                .sign(ALGORITHM);
    }

    public static DecodedJWT parseToken(String token) {
        return VERIFIER.verify(token);
    }

    public static boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public static List<String> getRolesFromToken(String token) {
        List<String> roles = parseToken(token).getClaim("roles").asList(String.class);
        return roles != null ? roles : new ArrayList<>();
    }

}