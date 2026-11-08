package org.cursillo.facturacion.dto;

import java.time.LocalDate;
import java.util.List;

public record InscripcionPendienteDTO(
        Integer idInscripcion,
        LocalDate fechaInscripcion,
        Integer total,
        List<String> conceptos) {
}
