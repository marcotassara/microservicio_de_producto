package com.proyecto.producto.service;

import com.proyecto.producto.dto.ProductoDTO;
import com.proyecto.producto.dto.CategoriaRemotaDTO;
import com.proyecto.producto.model.Producto;
import com.proyecto.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final WebClient webClientCategorias;

    @Autowired
    public ProductoService(ProductoRepository productoRepository,
                           @Qualifier("webClientCategorias") WebClient webClientCategorias) {
        this.productoRepository = productoRepository;
        this.webClientCategorias = webClientCategorias;
    }

    /**
     * Busca una lista de productos a partir de sus IDs.
     * Ideal para consultas "batch" desde otros microservicios.
     * @param ids La lista de IDs de productos a buscar.
     * @return Una lista de ProductoDTO.
     */
    public List<ProductoDTO> obtenerProductosPorIds(List<Long> ids) {
        // No necesitas modificar el ProductoRepository, JpaRepository ya tiene este método.
        return productoRepository.findAllById(ids).stream()
                .map(this::convertToDTO) // Asumo que tienes un método para convertir Entidad a DTO
                .collect(Collectors.toList());
    }


    // ================== Llamada al Microservicio Categorías ==================
    private String obtenerNombreCategoria(Long categoriaId) {
        if (categoriaId == null) return "Sin Categoría";
        try {
            CategoriaRemotaDTO cat = webClientCategorias.get()
                    .uri("/{id}", categoriaId) // 👈 ejemplo: http://localhost:8081/api/v1/categorias/1
                    .retrieve()
                    .bodyToMono(CategoriaRemotaDTO.class)
                    .block();

            return (cat != null) ? cat.getNombre() : "ID Categoría Desconocido";

        } catch (Exception e) {
            System.err.println("Error al consultar Microservicio de Categorías: " + e.getMessage());
            return "ERROR REMOTO";
        }
    }

    // ================== CRUD de Productos ==================
    public List<ProductoDTO> obtenerTodosProductos() {
        return productoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public ProductoDTO guardarProducto(ProductoDTO productoDTO) {
        Producto producto = convertToEntity(productoDTO);
        Producto productoSaved = productoRepository.save(producto);
        return convertToDTO(productoSaved);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<ProductoDTO> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ================== Conversión Entidad <-> DTO ==================
    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setMarca(producto.getMarca());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecioCompra(producto.getPrecioCompra());
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setImagenUrl(producto.getImagenUrl());

        // Guardamos el ID en el DTO
        dto.setCategoriaId(producto.getCategoriaId());
        // Enriquecemos con el nombre desde el microservicio de categorías
        dto.setCategoriaNombre(obtenerNombreCategoria(producto.getCategoriaId()));

        return dto;
    }

    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setMarca(dto.getMarca());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioCompra(dto.getPrecioCompra());
        producto.setPrecioVenta(dto.getPrecioVenta());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setCategoriaId(dto.getCategoriaId()); // se guarda solo el ID de la categoría
        return producto;
    }
}
