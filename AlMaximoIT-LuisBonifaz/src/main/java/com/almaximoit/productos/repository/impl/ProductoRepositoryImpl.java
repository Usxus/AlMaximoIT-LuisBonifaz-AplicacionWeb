package com.almaximoit.productos.repository.impl;

import com.almaximoit.productos.dto.ProductoProveedorDTO;
import com.almaximoit.productos.model.Producto;
import com.almaximoit.productos.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class ProductoRepositoryImpl implements ProductoRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductoRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public ProductoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Producto> productoRowMapper = new RowMapper<Producto>() {
        @Override
        public Producto mapRow(ResultSet rs, int rowNum) throws SQLException {
            Producto p = new Producto();
            p.setIdProducto(rs.getInt("id_producto"));
            p.setClave(rs.getString("clave"));
            p.setNombre(rs.getString("nombre"));
            p.setPrecio(rs.getBigDecimal("precio"));
            p.setIdTipoProducto(rs.getInt("id_tipo_producto"));
            p.setTipoProductoNombre(rs.getString("tipo_producto_nombre"));
            p.setActivo(rs.getInt("activo"));
            try {
                p.setCantidadProveedores(rs.getInt("cantidad_proveedores"));
            } catch (SQLException ignored) {
                p.setCantidadProveedores(0);
            }
            try {
                p.setFechaCreacion(rs.getString("fecha_creacion"));
            } catch (SQLException ignored) {
            }
            return p;
        }
    };

    private final RowMapper<ProductoProveedorDTO> productoProveedorRowMapper = new RowMapper<ProductoProveedorDTO>() {
        @Override
        public ProductoProveedorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductoProveedorDTO dto = new ProductoProveedorDTO();
            dto.setIdProductoProveedor(rs.getInt("id_producto_proveedor"));
            dto.setIdProducto(rs.getInt("id_producto"));
            dto.setIdProveedor(rs.getInt("id_proveedor"));
            try {
                dto.setProveedorNombre(rs.getString("proveedor_nombre"));
            } catch (SQLException e) {
                try {
                    dto.setProveedorNombre(rs.getString("nombre_proveedor"));
                } catch (SQLException ignored) {
                }
            }
            dto.setClaveProveedor(rs.getString("clave_proveedor"));
            dto.setCosto(rs.getBigDecimal("costo"));
            return dto;
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public List<Producto> buscarProductos(String clave, Integer idTipoProducto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_buscar_productos")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_clave", Types.VARCHAR),
                        new SqlParameter("p_id_tipo_producto", Types.INTEGER)
                )
                .returningResultSet("result", productoRowMapper);

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_clave", clave != null && !clave.trim().isEmpty() ? clave.trim() : null)
                .addValue("p_id_tipo_producto", idTipoProducto != null && idTipoProducto > 0 ? idTipoProducto : null);

        Map<String, Object> out = jdbcCall.execute(in);
        return (List<Producto>) out.get("result");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Producto obtenerProductoPorId(Integer idProducto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_obtener_producto_por_id")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("p_id_producto", Types.INTEGER))
                .returningResultSet("result", productoRowMapper);

        MapSqlParameterSource in = new MapSqlParameterSource().addValue("p_id_producto", idProducto);
        Map<String, Object> out = jdbcCall.execute(in);
        List<Producto> list = (List<Producto>) out.get("result");
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    @Override
    public Integer insertarProducto(String clave, String nombre, BigDecimal precio, Integer idTipoProducto) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("sp_insertar_producto")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("p_clave", Types.VARCHAR),
                            new SqlParameter("p_nombre", Types.VARCHAR),
                            new SqlParameter("p_precio", Types.DECIMAL),
                            new SqlParameter("p_id_tipo_producto", Types.INTEGER),
                            new SqlOutParameter("p_id_producto", Types.INTEGER)
                    );

            MapSqlParameterSource in = new MapSqlParameterSource()
                    .addValue("p_clave", clave)
                    .addValue("p_nombre", nombre)
                    .addValue("p_precio", precio)
                    .addValue("p_id_tipo_producto", idTipoProducto);

            Map<String, Object> out = jdbcCall.execute(in);
            for (Map.Entry<String, Object> entry : out.entrySet()) {
                if ("p_id_producto".equalsIgnoreCase(entry.getKey()) && entry.getValue() != null) {
                    return ((Number) entry.getValue()).intValue();
                }
            }
        } catch (Exception ex) {
            String errorMsg = ex.getMessage() != null ? ex.getMessage() : "";
            if (errorMsg.contains("Ya existe") || errorMsg.contains("Duplicate entry") || ex instanceof org.springframework.dao.DataIntegrityViolationException) {
                log.error("Error de unicidad o validación en sp_insertar_producto: {}", errorMsg);
                throw ex;
            }
            log.warn("Fallo llamada principal sp_insertar_producto, intentando fallback compatible H2: {}", ex.getMessage());
            try {
                return jdbcTemplate.queryForObject(
                        "CALL SP_INSERTAR_PRODUCTO(?, ?, ?, ?)",
                        Integer.class,
                        clave, nombre, precio, idTipoProducto
                );
            } catch (Exception e2) {
                log.error("Error al ejecutar sp_insertar_producto", e2);
                throw ex;
            }
        }
        return 0;
    }

    @Override
    public void actualizarProducto(Integer idProducto, String clave, String nombre, BigDecimal precio, Integer idTipoProducto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_actualizar_producto")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_id_producto", Types.INTEGER),
                        new SqlParameter("p_clave", Types.VARCHAR),
                        new SqlParameter("p_nombre", Types.VARCHAR),
                        new SqlParameter("p_precio", Types.DECIMAL),
                        new SqlParameter("p_id_tipo_producto", Types.INTEGER)
                );

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_id_producto", idProducto)
                .addValue("p_clave", clave)
                .addValue("p_nombre", nombre)
                .addValue("p_precio", precio)
                .addValue("p_id_tipo_producto", idTipoProducto);

        jdbcCall.execute(in);
    }

    @Override
    public void eliminarProducto(Integer idProducto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_eliminar_producto")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("p_id_producto", Types.INTEGER));

        MapSqlParameterSource in = new MapSqlParameterSource().addValue("p_id_producto", idProducto);
        jdbcCall.execute(in);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ProductoProveedorDTO> listarProveedoresPorProducto(Integer idProducto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_listar_proveedores_por_producto")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("p_id_producto", Types.INTEGER))
                .returningResultSet("result", productoProveedorRowMapper);

        MapSqlParameterSource in = new MapSqlParameterSource().addValue("p_id_producto", idProducto);
        Map<String, Object> out = jdbcCall.execute(in);
        return (List<ProductoProveedorDTO>) out.get("result");
    }

    @Override
    public void asignarProveedorProducto(Integer idProducto, Integer idProveedor, String claveProveedor, BigDecimal costo) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_asignar_proveedor_producto")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_id_producto", Types.INTEGER),
                        new SqlParameter("p_id_proveedor", Types.INTEGER),
                        new SqlParameter("p_clave_proveedor", Types.VARCHAR),
                        new SqlParameter("p_costo", Types.DECIMAL)
                );

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_id_producto", idProducto)
                .addValue("p_id_proveedor", idProveedor)
                .addValue("p_clave_proveedor", claveProveedor != null ? claveProveedor : "")
                .addValue("p_costo", costo != null ? costo : BigDecimal.ZERO);

        jdbcCall.execute(in);
    }

    @Override
    public void eliminarProveedoresProducto(Integer idProducto) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_eliminar_proveedores_producto")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("p_id_producto", Types.INTEGER));

        MapSqlParameterSource in = new MapSqlParameterSource().addValue("p_id_producto", idProducto);
        jdbcCall.execute(in);
    }
}
