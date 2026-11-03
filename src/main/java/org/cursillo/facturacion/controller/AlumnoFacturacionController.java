package org.cursillo.facturacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cursillo.facturacion.dto.AlumnoResumenDTO;
import org.cursillo.facturacion.service.AlumnoConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas/alumnos")
@Tag(name = "Facturas - Alumnos", description = "Consulta del nombre del alumno para mostrar en facturas")
public class AlumnoFacturacionController {

    private static final Logger log = LoggerFactory.getLogger(AlumnoFacturacionController.class);

    private final AlumnoConsultaService service;

    public AlumnoFacturacionController(AlumnoConsultaService service) {
        this.service = service;
    }

    @GetMapping("/{idAlumno}")
    @PreAuthorize("hasAnyAuthority('SECRETARIA','ALUMNO')")
    @Operation(summary = "Obtener nombre del alumno por id")
    public ResponseEntity<AlumnoResumenDTO> getAlumno(@PathVariable Integer idAlumno) {
        log.info("GET /facturas/alumnos/{}", idAlumno);
        return ResponseEntity.ok(service.getResumen(idAlumno));
    }
}