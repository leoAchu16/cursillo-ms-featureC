// FacturaService.java
package org.cursillo.facturacion.service;

import org.cursillo.commons.dto.FacturaDTO;
import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.commons.entities.Enums.MetodoPago;
import org.cursillo.commons.entities.Factura.DetallesFactura;
import org.cursillo.commons.entities.Factura.Factura;
import org.cursillo.facturacion.exception.ResourceNotFoundException;
import org.cursillo.facturacion.mapper.FacturaMapper;
import org.cursillo.facturacion.mapper.DetallesFacturaMapper;
import org.cursillo.facturacion.repository.FacturaRepository;
import org.cursillo.facturacion.repository.DetallesFacturaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetallesFacturaRepository detallesFacturaRepository;
    private final FacturaMapper facturaMapper;
    private final DetallesFacturaMapper detallesFacturaMapper;

    public FacturaService(FacturaRepository facturaRepository,
                          DetallesFacturaRepository detallesFacturaRepository,
                          FacturaMapper facturaMapper,
                          DetallesFacturaMapper detallesFacturaMapper) {
        this.facturaRepository = facturaRepository;
        this.detallesFacturaRepository = detallesFacturaRepository;
        this.facturaMapper = facturaMapper;
        this.detallesFacturaMapper = detallesFacturaMapper;
    }

    public FacturaDTO create(FacturaDTO dto) {
        // Validación manual: la Entity exige idInscripcion aunque el DTO no lo marque required
        if (dto.getIdInscripcion() == null) {
            throw new IllegalArgumentException("idInscripcion es obligatorio para crear una Factura");
        }

        Factura entity = facturaMapper.toEntity(dto);
        entity.setIdFactura(null);
        Factura guardada = facturaRepository.save(entity);

        // Guardamos los detalles por separado (no hay cascade configurado en la Entity)
        if (dto.getDetallesFactura() != null) {
            for (DetallesFacturaDTO detalleDTO : dto.getDetallesFactura()) {
                if (detalleDTO.getCantidad() == null) {
                    throw new IllegalArgumentException("cantidad es obligatoria en cada detalle de factura");
                }
                DetallesFactura detalle = detallesFacturaMapper.toEntity(detalleDTO);
                detalle.setIdDetalleFactura(null);
                detalle.setFactura(guardada);
                detallesFacturaRepository.save(detalle);
            }
        }

        return facturaMapper.toDTO(facturaRepository.findById(guardada.getIdFactura()).orElseThrow());
    }

    public FacturaDTO update(Integer id, FacturaDTO dto) {
        Factura existente = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));

        existente.setRuc(dto.getRuc());
        existente.setTotal(dto.getTotal());
        existente.setMetodoPago(MetodoPago.valueOf(dto.getMetodoPago().name()));
        existente.setFechaEmision(dto.getFechaEmision());
        // Nota: actualizar alumno/secretaria/inscripcion/detalles no lo cubrimos acá
        // para no complicar el update - se puede agregar después si el profesor lo pide.

        return facturaMapper.toDTO(facturaRepository.save(existente));
    }

    public FacturaDTO getById(Integer id) {
        Factura entity = facturaRepository.findByIdFacturaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));
        return facturaMapper.toDTO(entity);
    }

    public Page<FacturaDTO> getAll(Pageable pageable) {
        return facturaRepository.findAllByActivoTrue(pageable).map(facturaMapper::toDTO);
    }

    public Page<FacturaDTO> search(String ruc, MetodoPago metodoPago, Pageable pageable) {
        return facturaRepository.search(ruc, metodoPago, pageable).map(facturaMapper::toDTO);
    }

    public void delete(Integer id) {
        Factura entity = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));
        entity.setActivo(false);
        facturaRepository.save(entity);
    }
}