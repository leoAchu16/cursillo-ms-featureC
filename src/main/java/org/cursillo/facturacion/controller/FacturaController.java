package org.cursillo.facturacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cursillo.commons.dto.FacturaDTO;
import org.cursillo.commons.entities.Enums.MetodoPago;
import org.cursillo.facturacion.service.FacturaService;
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
@RequestMapping("/facturas")
@Tag(name = "Facturas", description = "Facturas emitidas a alumnos, con sus detalles anidados")
@ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado")
@ApiResponse(responseCode = "403", description = "El rol no tiene permiso para esta acción")
public class FacturaController {

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una factura con sus detalles",
            description = "Requiere idAlumno, idSecretaria e idInscripcion. Marca la inscripción como pagada.")
    @ApiResponse(responseCode = "201", description = "Factura creada")
    @ApiResponse(responseCode = "400", description = "Falta idInscripcion o la cantidad de un detalle")
    @PostMapping
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<FacturaDTO> create(@RequestBody FacturaDTO dto) {
        log.info("POST /facturas - creando factura para alumno id={}", dto.getIdAlumno());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Actualizar una factura",
            description = "Actualiza RUC, total, método de pago y fecha. No modifica los detalles.")
    @ApiResponse(responseCode = "200", description = "Factura actualizada")
    @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<FacturaDTO> update(@PathVariable Integer id, @RequestBody FacturaDTO dto) {
        log.info("PUT /facturas/{} - actualizando factura", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Obtener una factura por id")
    @ApiResponse(responseCode = "200", description = "Factura encontrada")
    @ApiResponse(responseCode = "404", description = "Factura no encontrada o inactiva")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<FacturaDTO> getById(@PathVariable Integer id) {
        log.info("GET /facturas/{}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Listar facturas activas (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de facturas")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<Page<FacturaDTO>> getAll(@ParameterObject Pageable pageable) {
        log.info("GET /facturas - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Buscar facturas por RUC y método de pago (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de facturas que coinciden con los filtros")
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<Page<FacturaDTO>> search(
            @Parameter(description = "Texto a buscar dentro del RUC (opcional)")
            @RequestParam(required = false) String ruc,
            @Parameter(description = "Método de pago exacto (opcional)")
            @RequestParam(required = false) MetodoPago metodoPago,
            @ParameterObject Pageable pageable) {
        log.info("GET /facturas/search - ruc={}, metodoPago={}", ruc, metodoPago);
        return ResponseEntity.ok(service.search(ruc, metodoPago, pageable));
    }

    @Operation(summary = "Eliminar una factura (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Factura marcada como inactiva")
    @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /facturas/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}