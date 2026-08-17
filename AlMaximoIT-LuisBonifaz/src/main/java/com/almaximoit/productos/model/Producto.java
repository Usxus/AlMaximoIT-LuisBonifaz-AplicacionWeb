package com.almaximoit.productos.model;

import jakarta.persistence.*;
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
@Entity
@Table(name = "productos")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "clave", nullable = false, unique = true, length = 50)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "id_tipo_producto", nullable = false)
    private Integer idTipoProducto;

    @Transient
    private String tipoProductoNombre;

    @Transient
    private Integer cantidadProveedores;

    @Column(name = "fecha_creacion")
    private String fechaCreacion;

    @Column(name = "activo")
    private Integer activo;

    public Producto(Integer idProducto, String clave, String nombre, BigDecimal precio, Integer idTipoProducto) {
        this.idProducto = idProducto;
        this.clave = clave;
        this.nombre = nombre;
        this.precio = precio;
        this.idTipoProducto = idTipoProducto;
    }

    public String getNombreTipoProducto() {
        return this.tipoProductoNombre;
    }

    public void setNombreTipoProducto(String nombreTipoProducto) {
        this.tipoProductoNombre = nombreTipoProducto;
    }
}
