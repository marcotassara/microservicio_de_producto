package com.proyecto.producto.controller;

import com.proyecto.producto.dto.ProductoDTO;
import com.proyecto.producto.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/productos") 
public class ProductoController {

    private final ProductoService productoService;

    @Autowired
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // 2. RETORNA la lista de productos (JSON)
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = productoService.obtenerTodosProductos();
        return ResponseEntity.ok(productos);
    }

    // 3. Obtener Producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Long id) {
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }

    // 4. CREAR Producto (solo catálogo)
    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO producto) {
        ProductoDTO nuevoProducto = productoService.guardarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // 5. ACTUALIZAR Producto (solo catálogo)
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody ProductoDTO producto) {
        producto.setId(id); // Asegurar que el ID es el correcto para la actualización
        ProductoDTO productoActualizado = productoService.guardarProducto(producto);
        return ResponseEntity.ok(productoActualizado);
    }

    // 6. ELIMINAR Producto (Retorna 204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
    
    // 7. Buscar productos por nombre (queda como un endpoint de búsqueda simple)
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    // ❌ Lógica eliminada:
    // - /vendidos
    // - /vender
    // - /devolver
    // - /nuestros-productos (vistas)
}