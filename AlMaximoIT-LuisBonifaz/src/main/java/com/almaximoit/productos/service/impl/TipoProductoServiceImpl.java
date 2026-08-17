package com.almaximoit.productos.service.impl;

import com.almaximoit.productos.model.TipoProducto;
import com.almaximoit.productos.repository.TipoProductoRepository;
import com.almaximoit.productos.service.TipoProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TipoProductoServiceImpl implements TipoProductoService {

    private final TipoProductoRepository tipoProductoRepository;

    public TipoProductoServiceImpl(TipoProductoRepository tipoProductoRepository) {
        this.tipoProductoRepository = tipoProductoRepository;
    }

    @Override
    public List<TipoProducto> listarTiposProducto() {
        return tipoProductoRepository.listarTiposProducto();
    }
}
