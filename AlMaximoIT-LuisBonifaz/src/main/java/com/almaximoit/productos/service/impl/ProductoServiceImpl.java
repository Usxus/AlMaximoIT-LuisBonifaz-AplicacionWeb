package com.almaximoit.productos.service.impl;

import com.almaximoit.productos.dto.ProductoFormDTO;
import com.almaximoit.productos.dto.ProductoProveedorDTO;
import com.almaximoit.productos.exception.ResourceNotFoundException;
import com.almaximoit.productos.model.Producto;
import com.almaximoit.productos.repository.ProductoRepository;
import com.almaximoit.productos.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> buscarProductos(String clave, Integer idTipoProducto) {
        return productoRepository.buscarProductos(clave, idTipoProducto);
    }

    @Override
    public ProductoFormDTO obtenerProductoFormPorId(Integer idProducto) {
        Producto p = productoRepository.obtenerProductoPorId(idProducto);
        if (p == null) {
            throw new ResourceNotFoundException("El producto con ID " + idProducto + " no existe o se encuentra inactivo.");
        }

        ProductoFormDTO form = new ProductoFormDTO();
        form.setIdProducto(p.getIdProducto());
        form.setClave(p.getClave());
        form.setNombre(p.getNombre());
        form.setPrecio(p.getPrecio());
        form.setIdTipoProducto(p.getIdTipoProducto());

        List<ProductoProveedorDTO> proveedores = productoRepository.listarProveedoresPorProducto(idProducto);
        form.setProveedores(proveedores);

        return form;
    }

    @Override
    public Integer guardarProducto(ProductoFormDTO form) {
        Integer idProducto = form.getIdProducto();

        if (idProducto == null || idProducto == 0) {
            idProducto = productoRepository.insertarProducto(
                    form.getClave(),
                    form.getNombre(),
                    form.getPrecio(),
                    form.getIdTipoProducto()
            );
        } else {
            productoRepository.actualizarProducto(
                    idProducto,
                    form.getClave(),
                    form.getNombre(),
                    form.getPrecio(),
                    form.getIdTipoProducto()
            );
            productoRepository.eliminarProveedoresProducto(idProducto);
        }

        if (form.getProveedores() != null) {
            for (ProductoProveedorDTO pp : form.getProveedores()) {
                if (pp.getIdProveedor() != null && pp.getIdProveedor() > 0) {
                    productoRepository.asignarProveedorProducto(
                            idProducto,
                            pp.getIdProveedor(),
                            pp.getClaveProveedor(),
                            pp.getCosto()
                    );
                }
            }
        }

        return idProducto;
    }

    @Override
    public void eliminarProducto(Integer idProducto) {
        Producto p = productoRepository.obtenerProductoPorId(idProducto);
        if (p == null) {
            throw new ResourceNotFoundException("No se puede eliminar: el producto con ID " + idProducto + " no existe.");
        }
        productoRepository.eliminarProducto(idProducto);
    }
}
