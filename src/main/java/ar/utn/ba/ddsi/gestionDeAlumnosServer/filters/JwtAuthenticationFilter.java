package ar.utn.ba.ddsi.gestionDeAlumnosServer.filters;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.parsearClaims(token);

                // Un refresh token no debe poder usarse como access token.
                if (JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                    return;
                }

                String username = claims.getSubject();
                List<GrantedAuthority> authorities = construirAuthorities(claims);

                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> construirAuthorities(Claims claims) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        String rol = claims.get(JwtUtil.CLAIM_ROL, String.class);
        if (rol != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
        }

        List<?> permisos = claims.get(JwtUtil.CLAIM_PERMISOS, List.class);
        if (permisos != null) {
            permisos.forEach(permiso -> authorities.add(new SimpleGrantedAuthority(String.valueOf(permiso))));
        }

        return authorities;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // No aplicar el filtro JWT solo a los endpoints públicos de autenticación
        return path.equals("/api/auth") || path.equals("/api/auth/refresh");
    }
}
