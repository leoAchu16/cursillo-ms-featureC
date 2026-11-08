package org.cursillo.facturacion.repository;

import org.cursillo.commons.entities.Enums.MetodoPago;
import org.cursillo.commons.entities.Factura.Factura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    /**
     * Búsqueda por texto libre: nombre del alumno, RUC, número de factura,
     * fecha (dd/mm/aaaa o aaaa-mm-dd) y total. Opcionalmente filtra por método de pago.
     * Se usa "" (vacío) en lugar de null para que PostgreSQL pueda inferir el tipo.
     */
    @Query(value = """
            SELECT f.* FROM facturas f
            LEFT JOIN usuarios u ON u.id_usuario = f.id_alumno
            WHERE f.activo = true
              AND (:metodoPago = '' OR CAST(f.metodo_pago AS TEXT) = :metodoPago)
              AND (:q = ''
                   OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(f.ruc) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR CAST(f.id_factura AS TEXT) = :q
                   OR TO_CHAR(f.fecha_emision, 'DD/MM/YYYY') LIKE CONCAT('%', :q, '%')
                   OR TO_CHAR(f.fecha_emision, 'YYYY-MM-DD') LIKE CONCAT('%', :q, '%')
                   OR CAST(f.total AS TEXT) LIKE CONCAT('%', REPLACE(:q, '.', ''), '%'))
            ORDER BY f.id_factura DESC
            """,
            countQuery = """
            SELECT COUNT(*) FROM facturas f
            LEFT JOIN usuarios u ON u.id_usuario = f.id_alumno
            WHERE f.activo = true
              AND (:metodoPago = '' OR CAST(f.metodo_pago AS TEXT) = :metodoPago)
              AND (:q = ''
                   OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR LOWER(f.ruc) LIKE LOWER(CONCAT('%', :q, '%'))
                   OR CAST(f.id_factura AS TEXT) = :q
                   OR TO_CHAR(f.fecha_emision, 'DD/MM/YYYY') LIKE CONCAT('%', :q, '%')
                   OR TO_CHAR(f.fecha_emision, 'YYYY-MM-DD') LIKE CONCAT('%', :q, '%')
                   OR CAST(f.total AS TEXT) LIKE CONCAT('%', REPLACE(:q, '.', ''), '%'))
            """,
            nativeQuery = true)
    Page<Factura> buscar(@Param("q") String q, @Param("metodoPago") String metodoPago, Pageable pageable);

    // Mantiene la firma que ya usa FacturaService
    default Page<Factura> search(String texto, MetodoPago metodoPago, Pageable pageable) {
        String q = texto == null ? "" : texto.trim();
        String metodo = metodoPago == null ? "" : metodoPago.name();
        return buscar(q, metodo, pageable);
    }

    Page<Factura> findAllByActivoTrue(Pageable pageable);

    Optional<Factura> findByIdFacturaAndActivoTrue(Integer idFactura);

    // Indica si una inscripción todavía tiene alguna factura activa
    @Query("select count(f) > 0 from Factura f "
            + "where f.inscripcion.idInscripcion = :idInscripcion and f.activo = true")
    boolean existeFacturaActivaDeInscripcion(@Param("idInscripcion") Integer idInscripcion);
}
