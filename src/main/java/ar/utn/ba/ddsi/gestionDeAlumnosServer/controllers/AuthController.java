package ar.utn.ba.ddsi.gestionDeAlumnosServer.controllers;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.NotFoundException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.RefreshRequest;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.TokenResponse;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.services.LoginService;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<AuthResponseDTO> loginApi(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            // Validación básica de credenciales
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Autenticar usuario usando el LoginService
            loginService.autenticarUsuario(username, password);
            
            // Generar tokens
            String accessToken = loginService.generarAccessToken(username);
            String refreshToken = loginService.generarRefreshToken(username);
            
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            log.info("El usuario {} está logueado. El token generado es {}", username, accessToken);
            
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            String username = JwtUtil.validarToken(request.getRefreshToken());

            // Validar que el token sea de tipo refresh
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(request.getRefreshToken())
                    .getBody();

            if (!"refresh".equals(claims.get("type"))) {
                return ResponseEntity.badRequest().build();
            }

            String newAccessToken = JwtUtil.generarAccessToken(username);
            TokenResponse response = new TokenResponse(newAccessToken, request.getRefreshToken());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/roles-permisos")
    public ResponseEntity<UserRolesPermissionsDTO> getUserRolesAndPermissions(Authentication authentication) {
        try {
            String username = authentication.getName();
            UserRolesPermissionsDTO response = loginService.obtenerRolesYPermisosUsuario(username);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            log.error("Usuario no encontrado", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error al obtener roles y permisos del usuario", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
