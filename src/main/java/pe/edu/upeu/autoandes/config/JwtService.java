package pe.edu.upeu.autoandes.config;
import org.springframework.stereotype.Service; import org.springframework.beans.factory.annotation.Value; import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import javax.crypto.SecretKey; import java.time.Clock; import java.util.Date;
@Service public class JwtService {private final SecretKey key;private final Clock clock;
 public JwtService(@Value("${app.jwt.secret}") String secret,Clock clock){this.key=Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secret));this.clock=clock;}
 public String emitir(String username){var now=clock.instant();return Jwts.builder().subject(username).issuer("autoandes").issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(3600))).signWith(key,Jwts.SIG.HS256).compact();}
 public String validar(String token){return Jwts.parser().verifyWith(key).requireIssuer("autoandes").clock(()->Date.from(clock.instant())).build().parseSignedClaims(token).getPayload().getSubject();}
}
