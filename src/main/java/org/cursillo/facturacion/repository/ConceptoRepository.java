package org.cursillo.facturacion.repository;

import org.cursillo.commons.entities.Conceptos.Concepto;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface ConceptoRepository extends JpaRepository<Concepto, Integer> {

    @Query("SELECT c FROM Concepto c WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND c.activo = true")
    Page<Concepto> search(@Param("nombre") String nombre, Pageable pageable);
    Page<Concepto> findAllByActivoTrue(Pageable pageable);
    Optional<Concepto> findByIdConceptoAndActivoTrue(Integer idConcepto);
}