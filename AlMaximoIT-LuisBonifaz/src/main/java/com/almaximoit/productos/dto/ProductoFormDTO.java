package com.almaximoit.productos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProducto;

    @NotBlank(message = "La clave interna del producto es obligatoria.")
    @Size(max = 50, message = "La clave no debe superar los 50 caracteres.")
    private String clave;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 150, message = "El nombre no debe superar los 150 caracteres.")
    private String nombre;

    @NotNull(message = "El precio de venta es obligatorio.")
    @Positive(message = "El precio debe ser mayor a cero.")
    private BigDecimal precio;

    @NotNull(message = "Debe seleccionar un tipo de producto.")
    @Positive(message = "Debe seleccionar un tipo de producto válido.")
    private Integer idTipoProducto;

    @Builder.Default
    private List<ProductoProveedorDTO> proveedores = new ArrayList<>();

    public ProductoFormDTO(Integer idProducto, String clave, String nombre, BigDecimal precio, Integer idTipoProducto) {
        this.idProducto = idProducto;
        this.clave = clave;
        this.nombre = nombre;
        this.precio = precio;
        this.idTipoProducto = idTipoProducto;
        this.proveedores = new ArrayList<>();
    }
}
