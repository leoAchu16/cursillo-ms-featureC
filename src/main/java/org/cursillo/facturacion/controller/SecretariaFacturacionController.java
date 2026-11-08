package org.cursillo.facturacion.controller;

import org.cursillo.facturacion.dto.SecretariaResumenDTO;
import org.cursillo.facturacion.service.SecretariaConsultaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facturas/secretarias")
public class SecretariaFacturacionController {

    private final SecretariaConsultaService service;

    public SecretariaFacturacionController(SecretariaConsultaService service) {
        this.service = service;
    }

    @GetMapping("/{idSecretaria}")
    @PreAuthorize("hasAnyAuthority('SECRETARIA','ALUMNO')")
    public SecretariaResumenDTO getResumen(@PathVariable Integer idSecretaria) {
        return service.getResumen(idSecretaria);
    }
}
