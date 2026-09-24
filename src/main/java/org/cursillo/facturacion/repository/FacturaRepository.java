package org.cursillo.facturacion.repository;

import org.cursillo.commons.entities.Factura.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.cursillo.commons.entities.Enums.MetodoPago;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    @Query("SELECT f FROM Factura f WHERE (:ruc IS NULL OR LOWER(f.ruc) LIKE LOWER(CONCAT('%', :ruc, '%'))) AND (:metodoPago IS NULL OR f.metodoPago = :metodoPago) AND f.activo = true")
    Page<Factura> search(@Param("ruc") String ruc, @Param("metodoPago") MetodoPago metodoPago, Pageable pageable);
    Page<Factura> findAllByActivoTrue(Pageable pageable);
    Optional<Factura> findByIdFacturaAndActivoTrue(Integer idFactura);
}