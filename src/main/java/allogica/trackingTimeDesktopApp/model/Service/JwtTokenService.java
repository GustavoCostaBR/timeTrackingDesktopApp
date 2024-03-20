package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import allogica.trackingTimeDesktopApp.model.entity.User;

@Service
public class JwtTokenService {

	private static final String SECRET_KEY = "abacatesupersupertretado9000"; // Secret key to generate and validate the token 

    private static final String ISSUER = "Allogica.com/trackingtimeapp"; // Token issuer

    public String generateToken(User user) {
        try {
            // It defines the HMAC SHA256 algorithm to create and sign the token with the secret key
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(ISSUER) // It defines the token issuer
                    .withIssuedAt(creationDate()) // It defines the creation date of the token
                    .withExpiresAt(expirationDate()) // It defines the expiration date of the token
                    .withSubject(user.getUsername()) // It defines the token subject (username in this case)
                    .sign(algorithm); // Signs the token using the specified algorithm
        } catch (JWTCreationException exception){
            throw new JWTCreationException("Erro ao gerar token.", exception);
        }
    }

    public String getSubjectFromToken(String token) {
        try {
            // It defines the HMAC SHA256 algorithm to verify the token signature passing the defined secret key
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER) // It defines the token issuer
                    .build()
                    .verify(token) // It verifies the token authenticity and validity
                    .getSubject(); // It gets the token subject (username in this case)
        } catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token inválido ou expirado.");
        }
    }

    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
    }

    private Instant expirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(4).toInstant();
    }
}
