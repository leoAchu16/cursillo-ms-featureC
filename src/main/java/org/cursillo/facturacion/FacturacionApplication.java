package org.cursillo.facturacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.cursillo")
@EntityScan(basePackages = "org.cursillo.commons.entities")
@EnableJpaRepositories(basePackages = "org.cursillo.facturacion.repository")
@EnableCaching
public class FacturacionApplication {

    public static void main(String[] args) {

        SpringApplication.run(FacturacionApplication.class, args);
    }
}