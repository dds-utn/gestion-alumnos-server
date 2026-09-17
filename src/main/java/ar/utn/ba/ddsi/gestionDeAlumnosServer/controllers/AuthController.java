package ar.utn.ba.ddsi.gestionDeAlumnosServer.controllers;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.NotFoundException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.AuthResponseDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.RefreshRequest;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.TokenResponse;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Usuario;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.services.LoginService;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.utils.JwtUtil;
import io.jsonwebtoken.Claims;
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
    private final JwtUtil jwtUtil;

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
            Usuario usuario = loginService.autenticarUsuario(username, password);

            // Generar tokens. El access token ya viaja con el rol y los permisos
            // del usuario como claims, para no requerir una llamada aparte.
            String accessToken = loginService.generarAccessToken(usuario);
            String refreshToken = loginService.generarRefreshToken(username);

            AuthResponseDTO response = AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            log.info("El usuario {} está logueado.", username);

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
            Claims claims = jwtUtil.parsearClaims(request.getRefreshToken());

            // Validar que el token sea de tipo refresh
            if (!JwtUtil.TYPE_REFRESH.equals(claims.get(JwtUtil.CLAIM_TYPE))) {
                return ResponseEntity.badRequest().build();
            }

            String username = claims.getSubject();
            String newAccessToken = loginService.generarAccessTokenPorUsername(username);
            TokenResponse response = new TokenResponse(newAccessToken, request.getRefreshToken());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Se mantiene por compatibilidad con clientes existentes, pero ya no es necesario
     * llamarlo tras el login: el rol y los permisos vienen en los claims del access token.
     */
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
