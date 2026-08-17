package com.almaximoit.productos.repository;

import com.almaximoit.productos.model.Proveedor;

import java.util.List;

public interface ProveedorRepository {

    /**
     * Recupera todos los proveedores activos registrados en el catálogo maestro.
     *
     * @return Lista de proveedores activos ordenados alfabéticamente.
     */
    List<Proveedor> listarProveedores();

    /**
     * Inserta un nuevo proveedor en la base de datos a través del Stored Procedure correspondiente.
     *
     * @param nombre      Nombre o razón social del proveedor.
     * @param descripcion Descripción o categoría de suministros.
     * @return Identificador único autogenerado para el nuevo proveedor.
     */
    Integer insertarProveedor(String nombre, String descripcion);
}
