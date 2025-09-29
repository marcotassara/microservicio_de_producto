package com.proyecto.producto.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "PRODUCTOS")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "PRODUCTO_SEQ", allocationSize = 1)
    @Column(name = "IDPRODUCTO", length = 50)
    private Long id;
    
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "MARCA", length = 50)
    private String marca;
    
    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;
    
    @Column(name = "PRECIO_COMPRA", nullable = false)
    private Double precioCompra;
    
    @Column(name = "PRECIO_VENTA", nullable = false)
    private Double precioVenta;

    @Column(name = "IMAGEN_URL", length = 300)
    private String imagenUrl;

    @Column(name = "IDCATEGORIA", length = 50)
    private Long categoriaId;
    
}