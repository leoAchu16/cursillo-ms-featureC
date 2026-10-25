package org.cursillo.facturacion.service;

import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.commons.entities.Factura.DetallesFactura;
import org.cursillo.facturacion.exception.ResourceNotFoundException;
import org.cursillo.facturacion.mapper.DetallesFacturaMapper;
import org.cursillo.facturacion.repository.DetallesFacturaRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DetallesFacturaService {

    private final DetallesFacturaRepository repository;
    private final DetallesFacturaMapper mapper;
    private final CacheManager cacheManager;

    public DetallesFacturaService(DetallesFacturaRepository repository,
                                  DetallesFacturaMapper mapper,
                                  CacheManager cacheManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.cacheManager = cacheManager;
    }

    // La factura cacheada lleva sus detalles adentro: si un detalle cambia, hay que invalidarla
    private void invalidarFactura(Integer idFactura) {
        Cache cache = cacheManager.getCache("facturas");
        if (cache != null && idFactura != null) {
            cache.evict(idFactura);
        }
    }

    @CachePut(value = "detalles-facturas", key = "#result.idDetalleFactura")
    public DetallesFacturaDTO create(DetallesFacturaDTO dto) {
        if (dto.getCantidad() == null) {
            throw new IllegalArgumentException("cantidad es obligatoria");
        }
        DetallesFactura entity = mapper.toEntity(dto);
        entity.setIdDetalleFactura(null);
        DetallesFacturaDTO creado = mapper.toDTO(repository.save(entity));
        invalidarFactura(creado.getIdFactura());
        return creado;
    }

    @CachePut(value = "detalles-facturas", key = "#id")
    public DetallesFacturaDTO update(Integer id, DetallesFacturaDTO dto) {
        DetallesFactura existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura no encontrado: " + id));
        existente.setSubtotal(dto.getSubtotal());
        existente.setCantidad(dto.getCantidad());
        DetallesFacturaDTO actualizado = mapper.toDTO(repository.save(existente));
        invalidarFactura(actualizado.getIdFactura());
        return actualizado;
    }

    @Cacheable(value = "detalles-facturas", key = "#id")
    public DetallesFacturaDTO getById(Integer id) {
        DetallesFactura entity = repository.findByIdDetalleFacturaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura no encontrado: " + id));
        return mapper.toDTO(entity);
    }

    // Los paginados no se cachean
    public Page<DetallesFacturaDTO> getAll(Pageable pageable) {
        return repository.findAllByActivoTrue(pageable).map(mapper::toDTO);
    }

    @CacheEvict(value = "detalles-facturas", key = "#id")
    public void delete(Integer id) {
        DetallesFactura entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetalleFactura no encontrado: " + id));
        entity.setActivo(false);
        repository.save(entity);
        invalidarFactura(entity.getFactura().getIdFactura());
    }
}