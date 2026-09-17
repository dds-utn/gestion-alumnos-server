package ar.utn.ba.ddsi.gestionDeAlumnosServer.utils;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_PERMISOS = "permisos";
    public static final String CLAIM_TYPE = "type";
    public static final String TYPE_REFRESH = "refresh";

    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 min
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 días

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera el access token incluyendo el rol y los permisos del usuario como claims,
     * para no tener que exponer un endpoint aparte de "roles y permisos".
     */
    public String generarAccessToken(Usuario usuario) {
        List<String> permisos = usuario.getPermisos().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(usuario.getNombreDeUsuario())
                .setIssuer("gestion-alumnos-server")
                .claim(CLAIM_ROL, usuario.getRol().name())
                .claim(CLAIM_PERMISOS, permisos)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generarRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer("gestion-alumnos-server")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .claim(CLAIM_TYPE, TYPE_REFRESH) // diferenciamos refresh del access
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parsearClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String validarToken(String token) {
        return parsearClaims(token).getSubject();
    }
}
