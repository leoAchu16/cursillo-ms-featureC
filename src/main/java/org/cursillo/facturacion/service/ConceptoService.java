package org.cursillo.facturacion.service;

import org.cursillo.commons.dto.ConceptoDTO;
import org.cursillo.commons.entities.Conceptos.Concepto;
import org.cursillo.facturacion.exception.ResourceNotFoundException;
import org.cursillo.facturacion.mapper.ConceptoMapper;
import org.cursillo.facturacion.repository.ConceptoRepository;
import org.springframework.cache.annotation.CacheEvict; // Agregado
import org.springframework.cache.annotation.CachePut;   // Agregado
import org.springframework.cache.annotation.Cacheable; // Agregado
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConceptoService {

    private final ConceptoRepository repository;
    private final ConceptoMapper mapper;

    public ConceptoService(ConceptoRepository repository, ConceptoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ConceptoDTO create(ConceptoDTO dto) {
        Concepto entity = mapper.toEntity(dto);
        entity.setIdConcepto(null); // por si mandan un id, lo ignoramos: lo genera la base
        return mapper.toDTO(repository.save(entity));
    }

    // Actualiza el objeto en Redis si existe, o guarda el nuevo valor resultante
    @CachePut(value = "conceptos", key = "#id")
    public ConceptoDTO update(Integer id, ConceptoDTO dto) {
        Concepto existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));
        existente.setNombre(dto.getNombre());
        existente.setMontoBase(dto.getMontoBase());
        return mapper.toDTO(repository.save(existente));
    }

    // Busca primero en Redis (clave conceptos::<id>). Si no existe, consulta Supabase y lo guarda en Redis.
    @Cacheable(value = "conceptos", key = "#id")
    public ConceptoDTO getById(Integer id) {
        Concepto entity = repository.findByIdConceptoAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));
        return mapper.toDTO(entity);
    }

    // Sin caché: Las búsquedas y paginados no se cachean directamente
    public Page<ConceptoDTO> getAll(Pageable pageable) {
        return repository.findAllByActivoTrue(pageable).map(mapper::toDTO);
    }

    // Sin caché: Las búsquedas y paginados no se cachean directamente
    public Page<ConceptoDTO> search(String nombre, Pageable pageable) {
        return repository.search(nombre, pageable).map(mapper::toDTO);
    }

    // Elimina el registro guardado en Redis cuando se realiza el borrado lógico
    @CacheEvict(value = "conceptos", key = "#id")
    public void delete(Integer id) {
        Concepto entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado: " + id));
        entity.setActivo(false); // borrado lógico
        repository.save(entity);
    }
}