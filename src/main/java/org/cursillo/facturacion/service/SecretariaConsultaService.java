package org.cursillo.facturacion.service;

import org.cursillo.commons.entities.Usuarios.Secretaria;
import org.cursillo.facturacion.dto.SecretariaResumenDTO;
import org.cursillo.facturacion.repository.SecretariaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SecretariaConsultaService {

    private final SecretariaRepository secretariaRepository;

    public SecretariaConsultaService(SecretariaRepository secretariaRepository) {
        this.secretariaRepository = secretariaRepository;
    }

    public SecretariaResumenDTO getResumen(Integer idSecretaria) {
        Secretaria secretaria = secretariaRepository.findById(idSecretaria)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Secretaria no encontrada"));
        return new SecretariaResumenDTO(secretaria.getIdUsuario(), secretaria.getNombre());
    }
}
