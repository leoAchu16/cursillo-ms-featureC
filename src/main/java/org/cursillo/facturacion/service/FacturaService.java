package org.cursillo.facturacion.service;

import org.cursillo.commons.dto.FacturaDTO;
import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.commons.entities.Enums.EstadoPago;
import org.cursillo.commons.entities.Enums.MetodoPago;
import org.cursillo.commons.entities.Factura.DetallesFactura;
import org.cursillo.commons.entities.Factura.Factura;
import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.cursillo.facturacion.exception.ResourceNotFoundException;
import org.cursillo.facturacion.mapper.FacturaMapper;
import org.cursillo.facturacion.mapper.DetallesFacturaMapper;
import org.cursillo.facturacion.repository.FacturaRepository;
import org.cursillo.facturacion.repository.DetallesFacturaRepository;
import org.cursillo.facturacion.repository.InscripcionRepository;
import org.springframework.cache.annotation.CacheEvict; // Agregado
import org.springframework.cache.annotation.CachePut;   // Agregado
import org.springframework.cache.annotation.Cacheable; // Agregado
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetallesFacturaRepository detallesFacturaRepository;
    private final FacturaMapper facturaMapper;
    private final DetallesFacturaMapper detallesFacturaMapper;
    private final InscripcionRepository inscripcionRepository;

    public FacturaService(FacturaRepository facturaRepository,
                          DetallesFacturaRepository detallesFacturaRepository,
                          FacturaMapper facturaMapper,
                          DetallesFacturaMapper detallesFacturaMapper, InscripcionRepository inscripcionRepository) {
        this.facturaRepository = facturaRepository;
        this.detallesFacturaRepository = detallesFacturaRepository;
        this.facturaMapper = facturaMapper;
        this.detallesFacturaMapper = detallesFacturaMapper;
        this.inscripcionRepository = inscripcionRepository;
    }

    // Transaccional: si algo falla, no queda la factura guardada a medias ni la inscripcion modificada
    @Transactional
    @CachePut(value = "facturas", key = "#result.idFactura")
    public FacturaDTO create(FacturaDTO dto) {
        if (dto.getIdInscripcion() == null) {
            throw new IllegalArgumentException("idInscripcion es obligatorio para crear una Factura");
        }

        // Se valida la inscripcion antes de guardar nada
        Inscripcion inscripcion = inscripcionRepository.findById(dto.getIdInscripcion())
                .orElseThrow(() -> new ResourceNotFoundException("Inscripcion no encontrada: " + dto.getIdInscripcion()));
        if (inscripcion.getEstadoPago() == EstadoPago.PAGADO) {
            throw new IllegalArgumentException("La inscripcion ya esta pagada");
        }
        if (!Objects.equals(dto.getIdAlumno(), inscripcion.getAlumno().getIdUsuario())) {
            throw new IllegalArgumentException("La inscripcion no pertenece al alumno indicado");
        }

        Factura entity = facturaMapper.toEntity(dto);
        entity.setIdFactura(null);
        Factura guardada = facturaRepository.save(entity);

        List<DetallesFactura> detallesGuardados = new java.util.ArrayList<>();
        if (dto.getDetallesFactura() != null) {
            for (DetallesFacturaDTO detalleDTO : dto.getDetallesFactura()) {
                if (detalleDTO.getCantidad() == null) {
                    throw new IllegalArgumentException("cantidad es obligatoria en cada detalle de factura");
                }
                DetallesFactura detalle = detallesFacturaMapper.toEntity(detalleDTO);
                detalle.setIdDetalleFactura(null);
                detalle.setFactura(guardada);
                detallesGuardados.add(detallesFacturaRepository.save(detalle));
            }
        }
        guardada.setDetalles(detallesGuardados);

        // actualiza el estado de la inscripcion a pagado
        inscripcion.setEstadoPago(EstadoPago.PAGADO);
        inscripcionRepository.save(inscripcion);

        return facturaMapper.toDTO(guardada);
    }

    // Actualiza la factura en la memoria caché cuando se edita
    @CachePut(value = "facturas", key = "#id")
    public FacturaDTO update(Integer id, FacturaDTO dto) {
        Factura existente = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));

        existente.setRuc(dto.getRuc());
        existente.setTotal(dto.getTotal());
        existente.setMetodoPago(MetodoPago.valueOf(dto.getMetodoPago().name()));
        existente.setFechaEmision(dto.getFechaEmision());

        return facturaMapper.toDTO(facturaRepository.save(existente));
    }

    // Busca primero en Redis (clave facturas::<id>). Si no existe, consulta la base de datos y la guarda en caché
    @Cacheable(value = "facturas", key = "#id")
    public FacturaDTO getById(Integer id) {
        Factura entity = facturaRepository.findByIdFacturaAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));
        return facturaMapper.toDTO(entity);
    }

    // Las búsquedas paginadas no se cachean directamente
    public Page<FacturaDTO> getAll(Pageable pageable) {
        return facturaRepository.findAllByActivoTrue(pageable).map(facturaMapper::toDTO);
    }

    // Las búsquedas avanzadas no se cachean directamente
    public Page<FacturaDTO> search(String ruc, MetodoPago metodoPago, Pageable pageable) {
        return facturaRepository.search(ruc, metodoPago, pageable).map(facturaMapper::toDTO);
    }

    // Elimina el registro guardado en Redis al realizar el borrado lógico
    @Transactional
    @CacheEvict(value = "facturas", key = "#id")
    public void delete(Integer id) {
        Factura entity = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));
        entity.setActivo(false);
        facturaRepository.save(entity);

        // Si la inscripcion ya no tiene facturas activas, vuelve a quedar pendiente de pago
        Inscripcion inscripcion = entity.getInscripcion();
        if (inscripcion != null
                && inscripcion.getEstadoPago() == EstadoPago.PAGADO
                && !facturaRepository.existeFacturaActivaDeInscripcion(inscripcion.getIdInscripcion())) {
            inscripcion.setEstadoPago(EstadoPago.PENDIENTE);
            inscripcionRepository.save(inscripcion);
        }
    }
}
