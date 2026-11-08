package org.cursillo.facturacion.repository;

import org.cursillo.commons.entities.Usuarios.Alumno;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {

    // Alumnos activos ordenados por nombre; q vacío trae todos (limitado por el Pageable)
    @Query("select a from Alumno a "
            + "where a.activo = true "
            + "and lower(a.nombre) like lower(concat('%', :q, '%')) "
            + "order by a.nombre")
    List<Alumno> buscarActivos(@Param("q") String q, Pageable pageable);
}
