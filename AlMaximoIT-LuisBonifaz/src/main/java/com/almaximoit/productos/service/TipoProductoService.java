package com.almaximoit.productos.service;

import com.almaximoit.productos.model.TipoProducto;
import java.util.List;

public interface TipoProductoService {

    /**
     * Obtiene el listado completo de tipos de producto activos.
     *
     * @return Lista de categorías disponibles.
     */
    List<TipoProducto> listarTiposProducto();
}
