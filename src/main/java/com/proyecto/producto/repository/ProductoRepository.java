package com.proyecto.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.producto.model.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
