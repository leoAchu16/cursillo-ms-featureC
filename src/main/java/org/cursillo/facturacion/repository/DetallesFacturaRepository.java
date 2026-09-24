package org.cursillo.facturacion.repository;

import org.cursillo.commons.entities.Factura.DetallesFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface DetallesFacturaRepository extends JpaRepository<DetallesFactura, Integer> {

    Page<DetallesFactura> findAllByActivoTrue(Pageable pageable);
    Optional<DetallesFactura> findByIdDetalleFacturaAndActivoTrue(Integer idDetalleFactura);
}