package com.almaximoit.productos.service;

import com.almaximoit.productos.model.Proveedor;
import java.util.List;

public interface ProveedorService {

    /**
     * Obtiene el listado completo de proveedores disponibles.
     *
     * @return Lista de entidades Proveedor activas.
     */
    List<Proveedor> listarProveedores();

    /**
     * Registra un nuevo proveedor validando duplicidades y reglas de negocio.
     *
     * @param nombre      Nombre de la empresa proveedora.
     * @param descripcion Descripción o rubro comercial.
     * @return Entidad del proveedor recién creado con su ID generado.
     */
    Proveedor crearProveedor(String nombre, String descripcion);
}
