package com.almaximoit.productos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoProveedorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProductoProveedor;
    private Integer idProducto;
    private Integer idProveedor;
    private String proveedorNombre;
    private String claveProveedor;
    private BigDecimal costo;
}
