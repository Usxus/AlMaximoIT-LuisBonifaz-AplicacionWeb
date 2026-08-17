package com.almaximoit.productos.repository;

import com.almaximoit.productos.model.TipoProducto;

import java.util.List;

public interface TipoProductoRepository {

    /**
     * Recupera todas las categorías o tipos de producto activos en el sistema.
     *
     * @return Lista de tipos de producto ordenados por nombre.
     */
    List<TipoProducto> listarTiposProducto();
}
