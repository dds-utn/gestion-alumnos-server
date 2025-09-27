package ar.utn.ba.ddsi.gestionDeAlumnosServer.controllers;

import ar.utn.ba.ddsi.gestionDeAlumnosServer.exceptions.NotFoundException;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.dto.AlumnoDTO;
import ar.utn.ba.ddsi.gestionDeAlumnosServer.services.AlumnoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlumnoApiController {
    private static final Logger log = LoggerFactory.getLogger(AlumnoApiController.class);
    private final AlumnoService alumnoService;

    @GetMapping("/alumnos")
    public ResponseEntity<List<AlumnoDTO>> obtenerTodosLosAlumnos() {
        log.info("Obteniendo todos los alumnos");
        List<AlumnoDTO> alumnos = alumnoService.obtenerTodosLosAlumnos();
        return ResponseEntity.ok(alumnos);
    }

    @GetMapping("/alumnos/{legajo}")
    public ResponseEntity<AlumnoDTO> obtenerAlumnoPorLegajo(@PathVariable String legajo) {
        try {
            log.info("Obteniendo al alumno {}", legajo);
            AlumnoDTO alumno = alumnoService.obtenerAlumnoPorLegajo(legajo).orElse(null);
            if (alumno == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(alumno);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/alumnos")
    public ResponseEntity<AlumnoDTO> crearAlumno(@RequestBody AlumnoDTO alumnoDTO) {
        try {
            log.info("Creando al alumno {}", alumnoDTO);
            AlumnoDTO alumnoCreado = alumnoService.crearAlumno(alumnoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(alumnoCreado);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/alumnos/{legajo}")
    public ResponseEntity<AlumnoDTO> actualizarAlumno(@PathVariable String legajo,
                                                      @RequestBody AlumnoDTO alumnoDTO) {
        try {
            log.info("Actualizando al alumno {}", legajo);
            AlumnoDTO alumnoActualizado = alumnoService.actualizarAlumno(legajo, alumnoDTO);
            return ResponseEntity.ok(alumnoActualizado);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/alumnos/{legajo}")
    public ResponseEntity<Void> eliminarAlumno(@PathVariable String legajo) {
        try {
            log.info("Eliminando al alumno {}", legajo);
            alumnoService.eliminarAlumno(legajo);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
