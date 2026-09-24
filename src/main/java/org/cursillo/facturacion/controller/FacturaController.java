package org.cursillo.facturacion.controller;

import org.cursillo.commons.dto.FacturaDTO;
import org.cursillo.commons.entities.Enums.MetodoPago;
import org.cursillo.facturacion.service.FacturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FacturaDTO> create(@RequestBody FacturaDTO dto) {
        log.info("POST /facturas - creando factura para alumno id={}", dto.getIdAlumno());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> update(@PathVariable Integer id, @RequestBody FacturaDTO dto) {
        log.info("PUT /facturas/{} - actualizando factura", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getById(@PathVariable Integer id) {
        log.info("GET /facturas/{}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<FacturaDTO>> getAll(Pageable pageable) {
        log.info("GET /facturas - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FacturaDTO>> search(
            @RequestParam(required = false) String ruc,
            @RequestParam(required = false) MetodoPago metodoPago,
            Pageable pageable) {
        log.info("GET /facturas/search - ruc={}, metodoPago={}", ruc, metodoPago);
        return ResponseEntity.ok(service.search(ruc, metodoPago, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /facturas/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}