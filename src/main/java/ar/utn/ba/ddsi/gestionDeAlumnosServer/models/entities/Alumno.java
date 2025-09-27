package ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Alumno {
    private String legajo;
    private String nombre;
    private String apellido;
    private List<Contacto> contactos;

    public Alumno() {
        this.contactos = new ArrayList<>();
    }

    public void agregarContacto(Contacto contacto) {
        this.contactos.add(contacto);
    }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }
}
