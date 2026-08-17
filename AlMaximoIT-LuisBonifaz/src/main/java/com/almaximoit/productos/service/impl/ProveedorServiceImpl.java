package com.almaximoit.productos.service.impl;

import com.almaximoit.productos.model.Proveedor;
import com.almaximoit.productos.repository.ProveedorRepository;
import com.almaximoit.productos.service.ProveedorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Proveedor> listarProveedores() {
        return proveedorRepository.listarProveedores();
    }

    @Override
    public Proveedor crearProveedor(String nombre, String descripcion) {
        Integer idCreado = proveedorRepository.insertarProveedor(nombre, descripcion);
        return new Proveedor(idCreado, nombre, descripcion);
    }
}
