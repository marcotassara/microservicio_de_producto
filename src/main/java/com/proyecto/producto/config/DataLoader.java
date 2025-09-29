package com.proyecto.producto.config;

import com.proyecto.producto.dto.CategoriaRemotaDTO;
import com.proyecto.producto.model.Producto;
import com.proyecto.producto.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final WebClient webClientCategorias;

    @Autowired
    public DataLoader(ProductoRepository productoRepository,
                      @Qualifier("webClientCategorias") WebClient webClientCategorias) {
        this.productoRepository = productoRepository;
        this.webClientCategorias = webClientCategorias;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            cargarProductosIniciales();
        }
    }

    private void cargarProductosIniciales() {
        CategoriaRemotaDTO[] categorias = obtenerCategorias();

        if (categorias.length == 0) {
            System.out.println("❌ No hay categorías disponibles desde el microservicio. Se canceló la carga de productos.");
            return;
        }

        Producto p1 = crearProducto("Coca-Cola 500ml", "Coca-Cola", "Refresco gaseoso sabor cola",
                500.0, 750.0, "https://imagen.com/cocacola.jpg", buscarCategoriaId(categorias, "Bebestibles"));

        Producto p2 = crearProducto("Arroz Integral 1kg", "Granja Feliz", "Arroz integral orgánico",
                1200.0, 1500.0, "https://imagen.com/arroz.jpg", buscarCategoriaId(categorias, "Víveres"));

        Producto p3 = crearProducto("Detergente Líquido", "Aseolimp", "Detergente para ropa",
                2000.0, 2500.0, "https://imagen.com/detergente.jpg", buscarCategoriaId(categorias, "Limpieza"));

        productoRepository.saveAll(Arrays.asList(p1, p2, p3));
        System.out.println("✅ Productos cargados inicialmente: " + productoRepository.count());
    }

    private CategoriaRemotaDTO[] obtenerCategorias() {
        try {
            CategoriaRemotaDTO[] categorias = webClientCategorias.get()
                    .uri("")
                    .retrieve()
                    .bodyToMono(CategoriaRemotaDTO[].class)
                    .block();

            return categorias != null ? categorias : new CategoriaRemotaDTO[0];

        } catch (WebClientResponseException e) {
            System.out.println("❌ Error en la respuesta del microservicio de categorías: " + e.getStatusCode());
        } catch (Exception e) {
            System.out.println("❌ No se pudo conectar al microservicio de categorías: " + e.getMessage());
        }

        return new CategoriaRemotaDTO[0];
    }

    private Producto crearProducto(String nombre, String marca, String descripcion,
                                   Double precioCompra, Double precioVenta,
                                   String imagenUrl, Long categoriaId) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setDescripcion(descripcion);
        producto.setPrecioCompra(precioCompra);
        producto.setPrecioVenta(precioVenta);
        producto.setImagenUrl(imagenUrl);
        producto.setCategoriaId(categoriaId);
        return producto;
    }

    private Long buscarCategoriaId(CategoriaRemotaDTO[] categorias, String nombre) {
        Optional<CategoriaRemotaDTO> cat = Arrays.stream(categorias)
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
        if (cat.isEmpty()) {
            System.out.println("⚠️ Categoría no encontrada: " + nombre);
        }
        return cat.map(CategoriaRemotaDTO::getCategoriaId).orElse(null);
    }
}
