package com.almaximoit.productos.repository.impl;

import com.almaximoit.productos.model.TipoProducto;
import com.almaximoit.productos.repository.TipoProductoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TipoProductoRepositoryImpl implements TipoProductoRepository {

    private final JdbcTemplate jdbcTemplate;

    public TipoProductoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TipoProducto> rowMapper = (rs, rowNum) -> new TipoProducto(
            rs.getInt("id_tipo_producto"),
            rs.getString("nombre"),
            rs.getString("descripcion")
    );

    @Override
    @SuppressWarnings("unchecked")
    public List<TipoProducto> listarTiposProducto() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_listar_tipos_producto")
                .withoutProcedureColumnMetaDataAccess()
                .returningResultSet("result", rowMapper);

        Map<String, Object> out = jdbcCall.execute();
        return (List<TipoProducto>) out.get("result");
    }
}
