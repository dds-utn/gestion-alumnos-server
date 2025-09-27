package ar.utn.ba.ddsi.gestionDeAlumnosServer.services;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.DuplicateLegajoException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.NotFoundException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.ValidationException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.AlumnoDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.ContactoDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.Alumno;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.Contacto;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.entities.TipoContacto;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.models.repositories.AlumnosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {
    @Autowired
    private AlumnosRepository alumnosRepository;

    public List<AlumnoDTO> obtenerTodosLosAlumnos() {
        return this.alumnosRepository.findAll().stream().map(this::convertirADTO).toList();
    }

    public Optional<AlumnoDTO> obtenerAlumnoPorLegajo(String legajo) {
        Alumno alumno = intentarRecuperarAlumno(legajo);
        return Optional.of(convertirADTO(alumno));
    }

    public AlumnoDTO crearAlumno(AlumnoDTO alumnoDTO) {
        validarDatosBasicos(alumnoDTO);
        validarContactos(alumnoDTO);
        validarDuplicidadDeAlumno(alumnoDTO);
        
        Alumno alumno = convertirDTOAEntity(alumnoDTO);
        alumnosRepository.save(alumno);
        
        return convertirADTO(alumno);
    }

    public AlumnoDTO actualizarAlumno(String legajo, AlumnoDTO alumnoDTO) {
        // Verificar que el alumno existe
        intentarRecuperarAlumno(legajo);
        
        validarDatosBasicos(alumnoDTO);
        validarContactos(alumnoDTO);
        
        // Si el legajo cambió, verificar que no exista otro con el nuevo legajo
        if (!legajo.equals(alumnoDTO.getLegajo().trim())) {
            validarDuplicidadDeAlumno(alumnoDTO);
        }
        
        Alumno alumno = convertirDTOAEntity(alumnoDTO);
        alumnosRepository.save(alumno);
        
        return convertirADTO(alumno);
    }

    public void eliminarAlumno(String legajo) {
        var alumno = intentarRecuperarAlumno(legajo);
        alumnosRepository.deleteByLegajo(alumno.getLegajo());
    }

    private void validarDatosBasicos(AlumnoDTO alumnoDTO) {
        ValidationException validationException = new ValidationException("Errores de validación");
        boolean tieneErrores = false;

        if (alumnoDTO.getLegajo() == null || alumnoDTO.getLegajo().trim().isEmpty()) {
            validationException.addFieldError("legajo", "El legajo es obligatorio");
            tieneErrores = true;
        }

        if (alumnoDTO.getNombre() == null || alumnoDTO.getNombre().trim().isEmpty()) {
            validationException.addFieldError("nombre", "El nombre es obligatorio");
            tieneErrores = true;
        }

        if (alumnoDTO.getApellido() == null || alumnoDTO.getApellido().trim().isEmpty()) {
            validationException.addFieldError("apellido", "El apellido es obligatorio");
            tieneErrores = true;
        }

        if (tieneErrores) {
            throw validationException;
        }
    }

    private void validarContactos(AlumnoDTO alumnoDTO) {
        if(alumnoDTO.getContactos().size() == 0) {
            return;
        }

        ValidationException validationException = new ValidationException("Errores de validación");
        boolean tieneErrores = false;

        for(int i = 0; i < alumnoDTO.getContactos().size(); i++) {
            ContactoDTO contacto = alumnoDTO.getContactos().get(i);

            // Si el contacto tiene algún campo completado, validar que esté completo
            boolean tieneTipo = contacto.getTipoContacto() != null;
            boolean tieneValor = contacto.getValor() != null && !contacto.getValor().trim().isEmpty();

            if (tieneTipo && !tieneValor) {
                validationException.addFieldError("contactos[" + i + "].valor", "El valor del contacto es obligatorio");
                tieneErrores = true;
            }

            if (tieneValor && !tieneTipo) {
                validationException.addFieldError("contactos[" + i + "].tipoContacto", "El tipo de contacto es obligatorio");
                tieneErrores = true;
            }

            // Validar formato de email
            if (tieneTipo && tieneValor && contacto.getTipoContacto().equals(TipoContacto.EMAIL)) {
                if (!contacto.getValor().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    validationException.addFieldError("contactos[" + i + "].valor", "El formato del email no es válido");
                    tieneErrores = true;
                }
            }
        }

        if (tieneErrores) {
            throw validationException;
        }
    }

    private void validarDuplicidadDeAlumno(AlumnoDTO alumnoDTO) {
        Optional<Alumno> alumnoExistente = alumnosRepository.findByLegajo(alumnoDTO.getLegajo().trim());
        if(alumnoExistente.isPresent()) {
            throw new DuplicateLegajoException(alumnoDTO.getLegajo().trim());
        }
    }

    private Alumno intentarRecuperarAlumno(String legajo) {
        Optional<Alumno> alumno = alumnosRepository.findByLegajo(legajo.trim());
        if(alumno.isEmpty()) {
            throw new NotFoundException("Alumno", legajo);
        }
        return alumno.get();
    }

    private AlumnoDTO convertirADTO(Alumno alumno) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setLegajo(alumno.getLegajo());
        dto.setNombre(alumno.getNombre());
        dto.setApellido(alumno.getApellido());
        dto.setNombreCompleto(alumno.getNombreCompleto());
        
        List<ContactoDTO> contactosDTO = alumno.getContactos().stream()
                .map(this::convertirContactoADTO)
                .collect(java.util.stream.Collectors.toList());
        dto.setContactos(contactosDTO);
        
        return dto;
    }

    private Alumno convertirDTOAEntity(AlumnoDTO alumnoDTO) {
        Alumno alumno = new Alumno();
        alumno.setLegajo(alumnoDTO.getLegajo().trim());
        alumno.setNombre(alumnoDTO.getNombre().trim());
        alumno.setApellido(alumnoDTO.getApellido().trim());
        
        List<Contacto> contactos = alumnoDTO.getContactos().stream()
                .filter(contacto -> contacto.getTipoContacto() != null && 
                        contacto.getValor() != null && !contacto.getValor().trim().isEmpty())
                .map(this::convertirContactoDTOAEntity)
                .collect(java.util.stream.Collectors.toList());
        alumno.setContactos(contactos);
        
        return alumno;
    }

    private ContactoDTO convertirContactoADTO(Contacto contacto) {
        ContactoDTO dto = new ContactoDTO();
        dto.setId(contacto.getId());
        dto.setTipoContacto(contacto.getTipoContacto());
        dto.setValor(contacto.getValor());
        dto.setTipoContactoDescripcion(contacto.getTipoContacto().toString());
        return dto;
    }

    private Contacto convertirContactoDTOAEntity(ContactoDTO contactoDTO) {
        Contacto contacto = new Contacto();
        contacto.setId(contactoDTO.getId());
        contacto.setTipoContacto(contactoDTO.getTipoContacto());
        contacto.setValor(contactoDTO.getValor().trim());
        return contacto;
    }
}
