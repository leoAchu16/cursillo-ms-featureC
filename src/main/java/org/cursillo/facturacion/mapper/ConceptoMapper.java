package org.cursillo.facturacion.mapper;

import org.cursillo.commons.dto.ConceptoDTO;
import org.cursillo.commons.entities.Conceptos.Concepto;
import org.springframework.stereotype.Component;

@Component
public class ConceptoMapper {

    // Entity -> DTO: para cuando devolvemos datos al cliente
    public ConceptoDTO toDTO(Concepto entity) {
        if (entity == null) return null;

        ConceptoDTO dto = new ConceptoDTO();
        dto.setIdConcepto(entity.getIdConcepto());
        dto.setNombre(entity.getNombre());
        dto.setMontoBase(entity.getMontoBase());
        return dto;
    }

    // DTO -> Entity: para cuando recibimos datos del cliente y guardamos
    public Concepto toEntity(ConceptoDTO dto) {
        if (dto == null) return null;

        Concepto entity = new Concepto();
        entity.setIdConcepto(dto.getIdConcepto());
        entity.setNombre(dto.getNombre());
        entity.setMontoBase(dto.getMontoBase());
        return entity;
    }
}