package org.cursillo.facturacion.controller;

import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.facturacion.service.DetallesFacturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/detalles-facturas")
public class DetallesFacturaController {

    private static final Logger log = LoggerFactory.getLogger(DetallesFacturaController.class);

    private final DetallesFacturaService service;

    public DetallesFacturaController(DetallesFacturaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<DetallesFacturaDTO> create(@RequestBody DetallesFacturaDTO dto) {
        log.info("POST /detalles-facturas - creando detalle para factura id={}", dto.getIdFactura());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<DetallesFacturaDTO> update(@PathVariable Integer id, @RequestBody DetallesFacturaDTO dto) {
        log.info("PUT /detalles-facturas/{} - actualizando detalle", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<DetallesFacturaDTO> getById(@PathVariable Integer id) {
        log.info("GET /detalles-facturas/{}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<Page<DetallesFacturaDTO>> getAll(Pageable pageable) {
        log.info("GET /detalles-facturas - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /detalles-facturas/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}