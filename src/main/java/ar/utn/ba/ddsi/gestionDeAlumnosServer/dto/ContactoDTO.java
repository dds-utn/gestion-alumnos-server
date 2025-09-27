package ar.utn.ba.ddsi.gestionDeAlumnosServer.dto;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.TipoContacto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoDTO {
    private Long id;
    private TipoContacto tipoContacto;
    private String valor;
    private String tipoContactoDescripcion;
}
