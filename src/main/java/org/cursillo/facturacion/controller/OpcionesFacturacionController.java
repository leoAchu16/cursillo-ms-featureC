package org.cursillo.facturacion.controller;

import org.cursillo.facturacion.dto.AlumnoResumenDTO;
import org.cursillo.facturacion.dto.InscripcionPendienteDTO;
import org.cursillo.facturacion.service.OpcionesFacturacionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/facturas/alumnos")
public class OpcionesFacturacionController {

    private final OpcionesFacturacionService service;

    public OpcionesFacturacionController(OpcionesFacturacionService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('SECRETARIA')")
    public List<AlumnoResumenDTO> listarAlumnos(@RequestParam(defaultValue = "") String q) {
        return service.listarAlumnos(q);
    }

    @GetMapping("/{idAlumno}/inscripciones-pendientes")
    @PreAuthorize("hasRole('SECRETARIA')")
    public List<InscripcionPendienteDTO> listarInscripcionesPendientes(@PathVariable Integer idAlumno) {
        return service.listarInscripcionesPendientes(idAlumno);
    }
}
