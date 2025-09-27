package ar.utn.ba.ddsi.gestionDeAlumnosServer.models.repositories;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.Alumno;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.Contacto;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.TipoContacto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AlumnosRepository {
    private final List<Alumno> alumnos = new ArrayList<>();
    private Long nextContactoId = 1L;

    public AlumnosRepository() {
        inicializarDatos();
    }

    public List<Alumno> findAll() {
        return this.alumnos;
    }

    public Optional<Alumno> findByLegajo(String legajo) {
        return alumnos.stream()
                .filter(alumno -> alumno.getLegajo().equals(legajo.trim()))
                .findFirst();
    }

    public void save(Alumno alumno) {
        // Si el alumno ya existe, actualizarlo
        Optional<Alumno> existingAlumno = findByLegajo(alumno.getLegajo());
        if (existingAlumno.isPresent()) {
            int index = alumnos.indexOf(existingAlumno.get());
            alumnos.set(index, alumno);
        } else {
            // Asignar IDs a los contactos si no los tienen
            if (alumno.getContactos() != null) {
                alumno.getContactos().forEach(contacto -> {
                    if (contacto.getId() == null) {
                        contacto.setId(nextContactoId++);
                    }
                });
            }
            alumnos.add(alumno);
        }
    }

    public void deleteByLegajo(String legajo) {
        alumnos.removeIf(alumno -> alumno.getLegajo().equals(legajo.trim()));
    }

    private void inicializarDatos() {
        // Alumno 1: Juan Pérez
        Alumno alumno1 = new Alumno();
        alumno1.setLegajo("A001");
        alumno1.setNombre("Juan");
        alumno1.setApellido("Pérez");
        
        Contacto contacto1_1 = new Contacto();
        contacto1_1.setId(nextContactoId++);
        contacto1_1.setTipoContacto(TipoContacto.EMAIL);
        contacto1_1.setValor("juan.perez@email.com");
        alumno1.agregarContacto(contacto1_1);
        
        Contacto contacto1_2 = new Contacto();
        contacto1_2.setId(nextContactoId++);
        contacto1_2.setTipoContacto(TipoContacto.TELEFONO);
        contacto1_2.setValor("+54 11 1234-5678");
        alumno1.agregarContacto(contacto1_2);
        
        alumnos.add(alumno1);

        // Alumno 2: María González
        Alumno alumno2 = new Alumno();
        alumno2.setLegajo("A002");
        alumno2.setNombre("María");
        alumno2.setApellido("González");
        
        Contacto contacto2_1 = new Contacto();
        contacto2_1.setId(nextContactoId++);
        contacto2_1.setTipoContacto(TipoContacto.EMAIL);
        contacto2_1.setValor("maria.gonzalez@email.com");
        alumno2.agregarContacto(contacto2_1);
        
        Contacto contacto2_2 = new Contacto();
        contacto2_2.setId(nextContactoId++);
        contacto2_2.setTipoContacto(TipoContacto.TELEFONO);
        contacto2_2.setValor("+54 11 2345-6789");
        alumno2.agregarContacto(contacto2_2);
        
        alumnos.add(alumno2);

        // Alumno 3: Carlos Rodríguez
        Alumno alumno3 = new Alumno();
        alumno3.setLegajo("A003");
        alumno3.setNombre("Carlos");
        alumno3.setApellido("Rodríguez");
        
        Contacto contacto3_1 = new Contacto();
        contacto3_1.setId(nextContactoId++);
        contacto3_1.setTipoContacto(TipoContacto.EMAIL);
        contacto3_1.setValor("carlos.rodriguez@email.com");
        alumno3.agregarContacto(contacto3_1);
        
        alumnos.add(alumno3);

        // Alumno 4: Ana López
        Alumno alumno4 = new Alumno();
        alumno4.setLegajo("A004");
        alumno4.setNombre("Ana");
        alumno4.setApellido("López");
        
        Contacto contacto4_1 = new Contacto();
        contacto4_1.setId(nextContactoId++);
        contacto4_1.setTipoContacto(TipoContacto.EMAIL);
        contacto4_1.setValor("ana.lopez@email.com");
        alumno4.agregarContacto(contacto4_1);
        
        Contacto contacto4_2 = new Contacto();
        contacto4_2.setId(nextContactoId++);
        contacto4_2.setTipoContacto(TipoContacto.TELEFONO);
        contacto4_2.setValor("+54 11 4567-8901");
        alumno4.agregarContacto(contacto4_2);
        
        alumnos.add(alumno4);

        // Alumno 5: Diego Martínez
        Alumno alumno5 = new Alumno();
        alumno5.setLegajo("A005");
        alumno5.setNombre("Diego");
        alumno5.setApellido("Martínez");
        
        Contacto contacto5_1 = new Contacto();
        contacto5_1.setId(nextContactoId++);
        contacto5_1.setTipoContacto(TipoContacto.EMAIL);
        contacto5_1.setValor("diego.martinez@email.com");
        alumno5.agregarContacto(contacto5_1);
        
        Contacto contacto5_2 = new Contacto();
        contacto5_2.setId(nextContactoId++);
        contacto5_2.setTipoContacto(TipoContacto.TELEFONO);
        contacto5_2.setValor("+54 11 5678-9012");
        alumno5.agregarContacto(contacto5_2);
        
        alumnos.add(alumno5);
    }
}
