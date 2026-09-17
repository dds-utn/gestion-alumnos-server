package ar.utn.ba.ddsi.gestionDeAlumnosServer.services;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.UserRolesPermissionsDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.NotFoundException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Usuario;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.repositories.UsuariosRepository;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UsuariosRepository usuariosRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginService(UsuariosRepository usuariosRepository, JwtUtil jwtUtil) {
        this.usuariosRepository = usuariosRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario autenticarUsuario(String username, String password) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByNombreDeUsuario(username);

        if (usuarioOpt.isEmpty()) {
            throw new NotFoundException("Usuario", username);
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar la contraseña usando BCrypt
        if (!passwordEncoder.matches(password, usuario.getContrasenia())) {
            throw new NotFoundException("Usuario", username);
        }

        return usuario;
    }

    public String generarAccessToken(Usuario usuario) {
        return jwtUtil.generarAccessToken(usuario);
    }

    public String generarRefreshToken(String username) {
        return jwtUtil.generarRefreshToken(username);
    }

    /**
     * Regenera el access token a partir del username validado en un refresh token,
     * releyendo el usuario para que el rol y los permisos viajen siempre actualizados.
     */
    public String generarAccessTokenPorUsername(String username) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByNombreDeUsuario(username);

        if (usuarioOpt.isEmpty()) {
            throw new NotFoundException("Usuario", username);
        }

        return jwtUtil.generarAccessToken(usuarioOpt.get());
    }

    public UserRolesPermissionsDTO obtenerRolesYPermisosUsuario(String username) {
        Optional<Usuario> usuarioOpt = usuariosRepository.findByNombreDeUsuario(username);

        if (usuarioOpt.isEmpty()) {
            throw new NotFoundException("Usuario", username);
        }

        Usuario usuario = usuarioOpt.get();

        return UserRolesPermissionsDTO.builder()
                .username(usuario.getNombreDeUsuario())
                .rol(usuario.getRol())
                .permisos(usuario.getPermisos())
                .build();
    }
}
