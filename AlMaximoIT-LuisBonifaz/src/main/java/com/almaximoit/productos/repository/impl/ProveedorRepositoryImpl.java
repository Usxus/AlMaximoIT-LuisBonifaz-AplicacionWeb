package com.almaximoit.productos.repository.impl;

import com.almaximoit.productos.model.Proveedor;
import com.almaximoit.productos.repository.ProveedorRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class ProveedorRepositoryImpl implements ProveedorRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProveedorRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Proveedor> rowMapper = (rs, rowNum) -> new Proveedor(
            rs.getInt("id_proveedor"),
            rs.getString("nombre"),
            rs.getString("descripcion")
    );

    @Override
    @SuppressWarnings("unchecked")
    public List<Proveedor> listarProveedores() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_listar_proveedores")
                .withoutProcedureColumnMetaDataAccess()
                .returningResultSet("result", rowMapper);

        Map<String, Object> out = jdbcCall.execute();
        return (List<Proveedor>) out.get("result");
    }

    @Override
    public Integer insertarProveedor(String nombre, String descripcion) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_insertar_proveedor")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("p_nombre", Types.VARCHAR),
                            new SqlParameter("p_descripcion", Types.VARCHAR),
                            new SqlOutParameter("p_id_proveedor", Types.INTEGER)
                    );

            MapSqlParameterSource in = new MapSqlParameterSource()
                    .addValue("p_nombre", nombre)
                    .addValue("p_descripcion", descripcion);

            Map<String, Object> out = jdbcCall.execute(in);
            if (out.containsKey("p_id_proveedor") && out.get("p_id_proveedor") != null) {
                return ((Number) out.get("p_id_proveedor")).intValue();
            }
        } catch (Exception ex) {
            return jdbcTemplate.queryForObject(
                    "CALL SP_INSERTAR_PROVEEDOR(?, ?)",
                    Integer.class,
                    nombre, descripcion
            );
        }
        return null;
    }
}
