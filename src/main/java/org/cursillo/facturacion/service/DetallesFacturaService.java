package org.cursillo.facturacion.service;

import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.commons.entities.Factura.DetallesFactura;
import org.cursillo.facturacion.exception.ResourceNotFoundException;
import org.cursillo.facturacion.mapper.DetallesFacturaMapper;
import org.cursillo.facturacion.repository.DetallesFacturaRepository;
import org.springframework.cache.annotation.CacheEvict; // Agregado
import org.springframework.cache.annotation.CachePut;   // Agregado
import org.springframework.cache.annotation.Cacheable; // Agregado
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DetallesFacturaService {

    private final DetallesFacturaRepository repository;
    private final DetallesFacturaMapper mapper;

    public DetallesFacturaService(DetallesFacturaRepository repository, DetallesFacturaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public DetallesFacturaDTO create(DetallesFacturaDTO dto) {
        if (dto.getCantidad() == null) {
            throw new IllegalArgumentException("cantidad es obligatoria");
        }
        DetallesFactura entity = mapper.toEntity(dto);
        entity.setIdDetalleFactura(null);
        return mapper.toDTO(repository.save(entity));
    }

    // Actualiza en la memoria caché cuando se edita un detalle de factura
    @CachePut(value = "detallesFactura", key = "#id")
    public DetallesFacturaDTO update(Integer id, DetallesFacturaDTO dto) {
        DetallesFactura existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura no encontrado: " + id));
        existente.setSubtotal(dto.getSubtotal());
        existente.setCantidad(dto.getCantidad());
        return mapper.toDTO(repository.save(existente));
    }

    // Busca primero en Redis; si no existe, consulta la base de datos y guarda el resultado en caché
    @Cacheable(value = "detallesFactura", key = "#id")
    public DetallesFacturaDTO getById(Integer id) {
        DetallesFactura entity = repository.findByIdDetalleFacturaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura no encontrado: " + id));
        return mapper.toDTO(entity);
    }

    // Las listas paginadas no se almacenan en caché para evitar datos desactualizados
    public Page<DetallesFacturaDTO> getAll(Pageable pageable) {
        return repository.findAllByActivoTrue(pageable).map(mapper::toDTO);
    }

    // Elimina la entrada correspondiente de Redis al borrar lógicamente el detalle
    @CacheEvict(value = "detallesFactura", key = "#id")
    public void delete(Integer id) {
        DetallesFactura entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura no encontrado: " + id));
        entity.setActivo(false);
        repository.save(entity);
    }
}