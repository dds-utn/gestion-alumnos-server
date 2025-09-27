package ar.utn.ba.ddsi.gestionDeAlumnosServer.models.repositories;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Permiso;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Rol;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuariosRepository {
    private List<Usuario> usuarios;

    public UsuariosRepository() {
        this.usuarios = new ArrayList<>();
        this.cargarUsuarios();
    }

    private void cargarUsuarios() {
        var encoder = new BCryptPasswordEncoder();
        var usuario1 = new Usuario();
        usuario1.setNombre("Jose");
        usuario1.setNombreDeUsuario("admin");
        usuario1.setContrasenia(encoder.encode("1234"));
        usuario1.setRol(Rol.ADMIN);
        usuario1.agregarPermiso(Permiso.ELIMINAR_ALUMNOS);
        usuario1.agregarPermiso(Permiso.EDITAR_ALUMNOS) ;
        usuario1.agregarPermiso(Permiso.CREAR_ALUMNOS) ;


        var usuario2 = new Usuario();
        usuario2.setNombre("Marlene");
        usuario2.setNombreDeUsuario("docente");
        usuario2.setContrasenia(encoder.encode("1234"));
        usuario2.setRol(Rol.DOCENTE);

        this.usuarios.add(usuario1);
        this.usuarios.add(usuario2);
    }

    public void save(Usuario usuario) {
        this.usuarios.add(usuario);
    }

    public List<Usuario> getUsuarios() {
        return this.usuarios;
    }

    public Optional<Usuario> findByNombreDeUsuario(String nombreDeUsuario) {
        return this.usuarios.stream().filter(usuario -> usuario.getNombreDeUsuario().equals(nombreDeUsuario)).findFirst();
    }
}
