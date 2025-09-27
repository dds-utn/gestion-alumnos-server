package ar.utn.ba.ddsi.gestionDeAlumnosServer.dto;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Permiso;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.usuarios.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRolesPermissionsDTO {
    private String username;
    private Rol rol;
    private List<Permiso> permisos;
}
