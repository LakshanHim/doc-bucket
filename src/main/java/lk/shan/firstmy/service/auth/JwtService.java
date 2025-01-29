package lk.shan.firstmy.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Service
public class JwtService {
    private static final String SECRET="1C47D1FA8E5DCB38DC1317DE413F111DBED589F039CD6F9665004F046B76199BCA534C4540671FEF1700FDB73E85450D849BEAE15720DC3FCDEB3A9AAEB848D5";
    private static final long VALIDITY= TimeUnit.MINUTES.toMillis(30);

    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }
    private SecretKey generateKey(){
        byte [] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }
    private Claims getClaims(String jwt){
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
    public String extractUserid(String jwt){
        Claims claims=getClaims(jwt);
        return claims.getSubject();
    }
    public boolean isTokenValid(String jwt){
        Claims claims=getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
