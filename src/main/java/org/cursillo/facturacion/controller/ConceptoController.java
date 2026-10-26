package org.cursillo.facturacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cursillo.commons.dto.ConceptoDTO;
import org.cursillo.facturacion.service.ConceptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conceptos")
@Tag(name = "Conceptos", description = "Catálogo de conceptos facturables (cuotas, matrícula, derechos de examen, etc.)")
@ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado")
@ApiResponse(responseCode = "403", description = "El rol no tiene permiso para esta acción")
public class ConceptoController {

    private static final Logger log = LoggerFactory.getLogger(ConceptoController.class);

    private final ConceptoService service;

    public ConceptoController(ConceptoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un concepto")
    @ApiResponse(responseCode = "201", description = "Concepto creado")
    @PostMapping
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<ConceptoDTO> create(@RequestBody ConceptoDTO dto) {
        log.info("POST /conceptos - creando concepto: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Actualizar un concepto")
    @ApiResponse(responseCode = "200", description = "Concepto actualizado")
    @ApiResponse(responseCode = "404", description = "Concepto no encontrado")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<ConceptoDTO> update(@PathVariable Integer id, @RequestBody ConceptoDTO dto) {
        log.info("PUT /conceptos/{} - actualizando concepto", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Obtener un concepto por id")
    @ApiResponse(responseCode = "200", description = "Concepto encontrado")
    @ApiResponse(responseCode = "404", description = "Concepto no encontrado o inactivo")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<ConceptoDTO> getById(@PathVariable Integer id) {
        log.info("GET /conceptos/{}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Listar conceptos activos (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de conceptos")
    @GetMapping
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Page<ConceptoDTO>> getAll(@ParameterObject Pageable pageable) {
        log.info("GET /conceptos - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Buscar conceptos por nombre (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de conceptos que coinciden con el filtro")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Page<ConceptoDTO>> search(
            @Parameter(description = "Texto a buscar dentro del nombre (opcional)")
            @RequestParam(required = false) String nombre,
            @ParameterObject Pageable pageable) {
        log.info("GET /conceptos/search - nombre={}", nombre);
        return ResponseEntity.ok(service.search(nombre, pageable));
    }

    @Operation(summary = "Eliminar un concepto (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Concepto marcado como inactivo")
    @ApiResponse(responseCode = "404", description = "Concepto no encontrado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /conceptos/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}