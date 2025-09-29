package com.proyecto.producto.repository;

import com.proyecto.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoria(String categoria);
    List<Producto> findByStock(Integer stock);
    List<Producto> findByTotalGananciaGreaterThan(Double totalGanancia);
}