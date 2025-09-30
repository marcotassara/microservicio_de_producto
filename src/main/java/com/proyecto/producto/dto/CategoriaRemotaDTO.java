package com.proyecto.producto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoriaRemotaDTO {

    @JsonProperty("id")
    
    private Long categoriaId;
    private String nombre;
    private String descripcion;
}
