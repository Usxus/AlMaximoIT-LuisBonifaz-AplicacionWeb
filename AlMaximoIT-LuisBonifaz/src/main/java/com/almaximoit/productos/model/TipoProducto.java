package com.almaximoit.productos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tipos_producto")
public class TipoProducto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_producto")
    private Integer idTipoProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha_creacion")
    private String fechaCreacion;

    @Column(name = "activo")
    private Integer activo;

    public TipoProducto(Integer idTipoProducto, String nombre, String descripcion) {
        this.idTipoProducto = idTipoProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
