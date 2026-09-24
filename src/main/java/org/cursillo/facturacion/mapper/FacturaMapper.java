// FacturaMapper.java
package org.cursillo.facturacion.mapper;

import org.cursillo.commons.dto.FacturaDTO;
import org.cursillo.commons.dto.DetallesFacturaDTO;
import org.cursillo.commons.entities.Factura.Factura;
import org.cursillo.facturacion.repository.AlumnoRepository;
import org.cursillo.facturacion.repository.SecretariaRepository;
import org.cursillo.facturacion.repository.InscripcionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FacturaMapper {

    private final AlumnoRepository alumnoRepository;
    private final SecretariaRepository secretariaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final DetallesFacturaMapper detallesFacturaMapper;

    public FacturaMapper(AlumnoRepository alumnoRepository,
                         SecretariaRepository secretariaRepository,
                         InscripcionRepository inscripcionRepository,
                         DetallesFacturaMapper detallesFacturaMapper) {
        this.alumnoRepository = alumnoRepository;
        this.secretariaRepository = secretariaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.detallesFacturaMapper = detallesFacturaMapper;
    }

    public FacturaDTO toDTO(Factura entity) {
        if (entity == null) return null;

        FacturaDTO dto = new FacturaDTO();
        dto.setIdFactura(entity.getIdFactura());
        dto.setIdAlumno(entity.getAlumno().getIdUsuario());
        dto.setIdSecretaria(entity.getSecretaria().getIdUsuario());
        dto.setIdInscripcion(entity.getInscripcion() != null ? entity.getInscripcion().getIdInscripcion() : null);
        dto.setRuc(entity.getRuc());
        dto.setTotal(entity.getTotal());
        dto.setMetodoPago(org.cursillo.commons.dto.MetodoPago.valueOf(entity.getMetodoPago().name()));
        dto.setFechaEmision(entity.getFechaEmision());

        if (entity.getDetalles() != null) {
            List<DetallesFacturaDTO> detallesDTO = entity.getDetalles().stream()
                    .map(detallesFacturaMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setDetallesFactura(detallesDTO);
        }
        return dto;
    }

    public Factura toEntity(FacturaDTO dto) {
        if (dto == null) return null;

        Factura entity = new Factura();
        entity.setIdFactura(dto.getIdFactura());
        entity.setAlumno(alumnoRepository.getReferenceById(dto.getIdAlumno()));
        entity.setSecretaria(secretariaRepository.getReferenceById(dto.getIdSecretaria()));
        if (dto.getIdInscripcion() != null) {
            entity.setInscripcion(inscripcionRepository.getReferenceById(dto.getIdInscripcion()));
        }
        entity.setRuc(dto.getRuc());
        entity.setTotal(dto.getTotal());
        entity.setMetodoPago(org.cursillo.commons.entities.Enums.MetodoPago.valueOf(dto.getMetodoPago().name()));
        entity.setFechaEmision(dto.getFechaEmision());
        // Los detalles se arman aparte en el Service
        return entity;
    }
}