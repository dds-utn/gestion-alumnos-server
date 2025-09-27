package ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Usuario {
    
    private Long id;
    private String nombre;
    private String nombreDeUsuario;
    private String contrasenia;
    private Rol rol;
    private List<Permiso> permisos;

    public Usuario() {
        this.permisos = new ArrayList<>();
    }

    public void agregarPermiso(Permiso permiso) {
        this.permisos.add(permiso);
    }
}
