package ua.pp.mcpe.server.config.security.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final long jwtRefreshExpirationMs;
    private final String jwtIssuser;
    private final Key key;
    private final JwtParser JWT_PARSER;

    public JwtUtils(@Value("${app.jwtSecret}") String jwtSecret,
                    @Value("${app.jwtExpirationMs}") long jwtExpirationMs,
                    @Value("${app.jwtRefreshExpirationMs}") long jwtRefreshExpirationMs,
                    @Value("${app.jwtIssuser}") String jwtIssuser) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.jwtRefreshExpirationMs = jwtRefreshExpirationMs;
        this.jwtIssuser = jwtIssuser;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.JWT_PARSER = Jwts.parserBuilder()
                .requireIssuer(jwtIssuser)
                .setSigningKey(key)
                .build();
    }

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("userid", userPrincipal.getId());
        data.put("username", userPrincipal.getUsername());

        return Jwts.builder()
                .setIssuer(jwtIssuser)
                .setIssuedAt(
                        Date.from(
                                LocalDateTime.now()
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant()
                        )
                )
                .setExpiration(
                        Date.from(
                                LocalDateTime.now()
                                        .plus(jwtExpirationMs, ChronoUnit.MILLIS)
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant()
                        )
                )
                .addClaims(data)
                .signWith(key)
                .compact();
    }

    public String generateJwtRefreshToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("userid", userPrincipal.getId());
        data.put("username", userPrincipal.getUsername());

        return Jwts.builder()
                .setIssuer(jwtIssuser)
                .setIssuedAt(
                        Date.from(
                                LocalDateTime.now()
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant()
                        )
                )
                .setExpiration(
                        Date.from(
                                LocalDateTime.now()
                                        .plus(jwtRefreshExpirationMs, ChronoUnit.MILLIS)
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant()
                        )
                )
                .addClaims(data)
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return  (String) JWT_PARSER.parseClaimsJws(token).getBody().get("username");
    }


    public boolean validateJwtToken(String authToken) {
        try {
            JWT_PARSER.parseClaimsJws(authToken).getBody();
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }


}
