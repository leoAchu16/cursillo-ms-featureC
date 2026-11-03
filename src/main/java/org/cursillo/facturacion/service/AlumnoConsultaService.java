package org.cursillo.facturacion.service;

import org.cursillo.commons.entities.Usuarios.Alumno;
import org.cursillo.facturacion.dto.AlumnoResumenDTO;
import org.cursillo.facturacion.exception.ResourceNotFoundException;
import org.cursillo.facturacion.repository.AlumnoRepository;
import org.springframework.stereotype.Service;

@Service
public class AlumnoConsultaService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoConsultaService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    public AlumnoResumenDTO getResumen(Integer idAlumno) {
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado: " + idAlumno));
        return new AlumnoResumenDTO(alumno.getIdUsuario(), alumno.getNombre());
    }
}