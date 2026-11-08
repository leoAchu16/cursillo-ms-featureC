package org.cursillo.facturacion.service;

import org.cursillo.commons.entities.Enums.EstadoPago;
import org.cursillo.commons.entities.Inscripciones.DetallesInscripcion;
import org.cursillo.facturacion.dto.AlumnoResumenDTO;
import org.cursillo.facturacion.dto.InscripcionPendienteDTO;
import org.cursillo.facturacion.repository.AlumnoRepository;
import org.cursillo.facturacion.repository.InscripcionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Datos de solo lectura que necesita el formulario de factura:
 * lista de alumnos por nombre e inscripciones pendientes de pago de un alumno.
 */
@Service
public class OpcionesFacturacionService {

    private static final int MAX_ALUMNOS = 200;

    private final AlumnoRepository alumnoRepository;
    private final InscripcionRepository inscripcionRepository;

    public OpcionesFacturacionService(AlumnoRepository alumnoRepository,
                                      InscripcionRepository inscripcionRepository) {
        this.alumnoRepository = alumnoRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    @Transactional(readOnly = true)
    public List<AlumnoResumenDTO> listarAlumnos(String q) {
        String texto = q == null ? "" : q.trim();
        return alumnoRepository.buscarActivos(texto, PageRequest.of(0, MAX_ALUMNOS)).stream()
                .map(a -> new AlumnoResumenDTO(a.getIdUsuario(), a.getNombre()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscripcionPendienteDTO> listarInscripcionesPendientes(Integer idAlumno) {
        return inscripcionRepository.findByAlumnoYEstado(idAlumno, EstadoPago.PENDIENTE).stream()
                .map(i -> new InscripcionPendienteDTO(
                        i.getIdInscripcion(),
                        i.getFechaInscripcion(),
                        i.getTotal(),
                        i.getDetalles().stream()
                                .map(DetallesInscripcion::getConceptoDescripcion)
                                .toList()))
                .toList();
    }
}
