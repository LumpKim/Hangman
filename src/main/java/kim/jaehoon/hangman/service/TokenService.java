package kim.jaehoon.hangman.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kim.jaehoon.hangman.exception.TokenError;
import kim.jaehoon.hangman.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;

@Service
public class TokenService {

    @Value("${JWT_ACCESS_EXP}")
    private Long accessExp;

    @Value("${JWT_REFRESH_EXP}")
    private Long refreshExp;

    private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String getIdentity(String jwt) {
        try {
            Claims parsed = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
            if (Date.from(Instant.now()).before(parsed.getExpiration())) {
                throw new TokenExpiredException();
            }
            return parsed.getSubject();
        } catch (SignatureException e) {
            throw new TokenError(e.getMessage());
        }
    }

    public String createAccessToken(String identity) {
        return _generateToken(identity, accessExp);
    }

    public String createRefreshToken(String identity) {
        return _generateToken(identity, refreshExp);
    }

    private String _generateToken(String identity, Long expSeconds) {
        return Jwts.builder()
                .setSubject(identity)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(expSeconds)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}
