package org.cursillo.facturacion.controller;

import org.cursillo.commons.dto.ConceptoDTO;
import org.cursillo.facturacion.service.ConceptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conceptos")
public class ConceptoController {

    private static final Logger log = LoggerFactory.getLogger(ConceptoController.class);

    private final ConceptoService service;

    public ConceptoController(ConceptoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<ConceptoDTO> create(@RequestBody ConceptoDTO dto) {
        log.info("POST /conceptos - creando concepto: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<ConceptoDTO> update(@PathVariable Integer id, @RequestBody ConceptoDTO dto) {
        log.info("PUT /conceptos/{} - actualizando concepto", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<ConceptoDTO> getById(@PathVariable Integer id) {
        log.info("GET /conceptos/{}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<Page<ConceptoDTO>> getAll(Pageable pageable) {
        log.info("GET /conceptos - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<Page<ConceptoDTO>> search(
            @RequestParam(required = false) String nombre,
            Pageable pageable) {
        log.info("GET /conceptos/search - nombre={}", nombre);
        return ResponseEntity.ok(service.search(nombre, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /conceptos/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}