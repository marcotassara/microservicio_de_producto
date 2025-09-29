package com.proyecto.producto.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String marca;
    private String descripcion;
    private Double precioCompra;
    private Double precioVenta;
    private String imagenUrl;

    private Long categoriaId;      // 👈 para guardar en BD
    private String categoriaNombre; // 👈 enriquecido desde el microservicio
}
