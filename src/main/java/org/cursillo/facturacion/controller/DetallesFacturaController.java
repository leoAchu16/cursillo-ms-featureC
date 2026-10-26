package org.cursillo.facturacion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.facturacion.service.DetallesFacturaService;
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
@RequestMapping("/detalles-facturas")
@Tag(name = "Detalles de factura", description = "Líneas de cada factura: relación muchos a muchos entre Factura y Concepto")
@ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado")
@ApiResponse(responseCode = "403", description = "El rol no tiene permiso para esta acción")
public class DetallesFacturaController {

    private static final Logger log = LoggerFactory.getLogger(DetallesFacturaController.class);

    private final DetallesFacturaService service;

    public DetallesFacturaController(DetallesFacturaService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un detalle de factura",
            description = "Asocia una factura con un concepto. La cantidad es obligatoria.")
    @ApiResponse(responseCode = "201", description = "Detalle creado")
    @ApiResponse(responseCode = "400", description = "Falta la cantidad")
    @PostMapping
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<DetallesFacturaDTO> create(@RequestBody DetallesFacturaDTO dto) {
        log.info("POST /detalles-facturas - creando detalle para factura id={}", dto.getIdFactura());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @Operation(summary = "Actualizar un detalle de factura",
            description = "Actualiza subtotal y cantidad.")
    @ApiResponse(responseCode = "200", description = "Detalle actualizado")
    @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<DetallesFacturaDTO> update(@PathVariable Integer id, @RequestBody DetallesFacturaDTO dto) {
        log.info("PUT /detalles-facturas/{} - actualizando detalle", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Obtener un detalle de factura por id")
    @ApiResponse(responseCode = "200", description = "Detalle encontrado")
    @ApiResponse(responseCode = "404", description = "Detalle no encontrado o inactivo")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<DetallesFacturaDTO> getById(@PathVariable Integer id) {
        log.info("GET /detalles-facturas/{}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Listar detalles de factura activos (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de detalles")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ALUMNO', 'SECRETARIA')")
    public ResponseEntity<Page<DetallesFacturaDTO>> getAll(@ParameterObject Pageable pageable) {
        log.info("GET /detalles-facturas - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(summary = "Eliminar un detalle de factura (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Detalle marcado como inactivo")
    @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SECRETARIA')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("DELETE /detalles-facturas/{}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}