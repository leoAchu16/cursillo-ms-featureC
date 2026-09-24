// DetallesFacturaMapper.java
package org.cursillo.facturacion.mapper;

import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.commons.entities.Factura.DetallesFactura;
import org.cursillo.facturacion.repository.FacturaRepository;
import org.cursillo.facturacion.repository.ConceptoRepository;
import org.springframework.stereotype.Component;

@Component
public class DetallesFacturaMapper {

    private final FacturaRepository facturaRepository;
    private final ConceptoRepository conceptoRepository;

    public DetallesFacturaMapper(FacturaRepository facturaRepository, ConceptoRepository conceptoRepository) {
        this.facturaRepository = facturaRepository;
        this.conceptoRepository = conceptoRepository;
    }

    public DetallesFacturaDTO toDTO(DetallesFactura entity) {
        if (entity == null) return null;
        DetallesFacturaDTO dto = new DetallesFacturaDTO();
        dto.setIdDetalleFactura(entity.getIdDetalleFactura());
        dto.setIdFactura(entity.getFactura().getIdFactura());
        dto.setIdConcepto(entity.getConcepto().getIdConcepto());
        dto.setSubtotal(entity.getSubtotal());
        dto.setCantidad(entity.getCantidad());
        return dto;
    }

    public DetallesFactura toEntity(DetallesFacturaDTO dto) {
        if (dto == null) return null;
        DetallesFactura entity = new DetallesFactura();
        entity.setIdDetalleFactura(dto.getIdDetalleFactura());
        entity.setFactura(facturaRepository.getReferenceById(dto.getIdFactura()));
        entity.setConcepto(conceptoRepository.getReferenceById(dto.getIdConcepto()));
        entity.setSubtotal(dto.getSubtotal());
        entity.setCantidad(dto.getCantidad());
        return entity;
    }
}