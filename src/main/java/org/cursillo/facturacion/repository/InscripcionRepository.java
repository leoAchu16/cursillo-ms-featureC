package org.cursillo.facturacion.repository;

import org.cursillo.commons.entities.Enums.EstadoPago;
import org.cursillo.commons.entities.Inscripciones.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    // Inscripciones de un alumno según su estado de pago, con sus detalles
    @Query("select distinct i from Inscripcion i "
            + "left join fetch i.detalles "
            + "where i.alumno.idUsuario = :idAlumno "
            + "and i.estadoPago = :estado "
            + "and i.activo = true "
            + "order by i.fechaInscripcion desc, i.idInscripcion desc")
    List<Inscripcion> findByAlumnoYEstado(@Param("idAlumno") Integer idAlumno,
                                          @Param("estado") EstadoPago estado);
}
