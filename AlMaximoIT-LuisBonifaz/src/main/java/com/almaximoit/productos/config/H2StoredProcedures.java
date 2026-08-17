package com.almaximoit.productos.config;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Procedimientos almacenados para el motor H2 Database (modo en memoria).
 * Emula la lógica y firmas de los procedimientos almacenados de MySQL mediante funciones estáticas de Java.
 */
public class H2StoredProcedures {

    public static ResultSet spBuscarProductos(Connection conn, String pClave, Integer pIdTipoProducto) throws SQLException {
        String sql = "SELECT p.id_producto, p.clave, p.nombre, p.precio, p.id_tipo_producto, " +
                "tp.nombre AS tipo_producto_nombre, COUNT(pp.id_proveedor) AS cantidad_proveedores, " +
                "FORMATDATETIME(p.fecha_creacion, 'dd/MM/yyyy HH:mm') AS fecha_creacion, p.activo " +
                "FROM productos p " +
                "INNER JOIN tipos_producto tp ON p.id_tipo_producto = tp.id_tipo_producto " +
                "LEFT JOIN producto_proveedores pp ON p.id_producto = pp.id_producto " +
                "WHERE p.activo = 1 " +
                "  AND (? IS NULL OR ? = '' OR LOWER(p.clave) LIKE LOWER(CONCAT('%', ?, '%')) OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', ?, '%'))) " +
                "  AND (? IS NULL OR ? = 0 OR p.id_tipo_producto = ?) " +
                "GROUP BY p.id_producto, p.clave, p.nombre, p.precio, p.id_tipo_producto, tp.nombre, p.fecha_creacion, p.activo " +
                "ORDER BY p.id_producto DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pClave);
        ps.setString(2, pClave);
        ps.setString(3, pClave);
        ps.setString(4, pClave);
        ps.setObject(5, pIdTipoProducto);
        ps.setObject(6, pIdTipoProducto);
        ps.setObject(7, pIdTipoProducto);
        return ps.executeQuery();
    }

    public static ResultSet spBuscarProductos(Connection conn) throws SQLException {
        return spBuscarProductos(conn, null, null);
    }

    public static ResultSet spObtenerProductoPorId(Connection conn, Integer pIdProducto) throws SQLException {
        String sql = "SELECT p.id_producto, p.clave, p.nombre, p.precio, p.id_tipo_producto, " +
                "tp.nombre AS tipo_producto_nombre, 0 AS cantidad_proveedores, " +
                "FORMATDATETIME(p.fecha_creacion, 'dd/MM/yyyy HH:mm') AS fecha_creacion, p.activo " +
                "FROM productos p " +
                "INNER JOIN tipos_producto tp ON p.id_tipo_producto = tp.id_tipo_producto " +
                "WHERE p.id_producto = ? AND p.activo = 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pIdProducto != null ? pIdProducto : 0);
        return ps.executeQuery();
    }

    public static ResultSet spObtenerProductoPorId(Connection conn) throws SQLException {
        return spObtenerProductoPorId(conn, 0);
    }

    public static int spInsertarProducto(Connection conn, String pClave, String pNombre, BigDecimal pPrecio, Integer pIdTipoProducto) throws SQLException {
        String checkSql = "SELECT id_producto, activo FROM productos WHERE clave = ?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, pClave);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    int idExistente = rs.getInt("id_producto");
                    int activo = rs.getInt("activo");
                    if (activo == 0) {
                        String updateSql = "UPDATE productos SET nombre = ?, precio = ?, id_tipo_producto = ?, activo = 1, fecha_modificacion = CURRENT_TIMESTAMP WHERE id_producto = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                            updatePs.setString(1, pNombre);
                            updatePs.setBigDecimal(2, pPrecio);
                            updatePs.setInt(3, pIdTipoProducto);
                            updatePs.setInt(4, idExistente);
                            updatePs.executeUpdate();
                        }
                        try (PreparedStatement delPs = conn.prepareStatement("DELETE FROM producto_proveedores WHERE id_producto = ?")) {
                            delPs.setInt(1, idExistente);
                            delPs.executeUpdate();
                        }
                        return idExistente;
                    } else {
                        throw new SQLException("Ya existe un producto activo registrado con la clave: " + pClave);
                    }
                }
            }
        }

        String sql = "INSERT INTO productos (clave, nombre, precio, id_tipo_producto, fecha_creacion, activo) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 1)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, pClave);
        ps.setString(2, pNombre);
        ps.setBigDecimal(3, pPrecio);
        ps.setInt(4, pIdTipoProducto);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public static void spActualizarProducto(Connection conn, Integer pIdProducto, String pClave, String pNombre, BigDecimal pPrecio, Integer pIdTipoProducto) throws SQLException {
        String sql = "UPDATE productos SET clave = ?, nombre = ?, precio = ?, id_tipo_producto = ?, fecha_modificacion = CURRENT_TIMESTAMP WHERE id_producto = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, pClave);
        ps.setString(2, pNombre);
        ps.setBigDecimal(3, pPrecio);
        ps.setInt(4, pIdTipoProducto);
        ps.setInt(5, pIdProducto);
        ps.executeUpdate();
    }

    public static void spEliminarProducto(Connection conn, Integer pIdProducto) throws SQLException {
        String sql = "UPDATE productos SET activo = 0 WHERE id_producto = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pIdProducto);
        ps.executeUpdate();
    }

    public static ResultSet spListarTiposProducto(Connection conn) throws SQLException {
        String sql = "SELECT id_tipo_producto, nombre, descripcion, FORMATDATETIME(fecha_creacion, 'dd/MM/yyyy') AS fecha_creacion FROM tipos_producto WHERE activo = 1 ORDER BY nombre ASC";
        return conn.createStatement().executeQuery(sql);
    }

    public static ResultSet spListarProveedores(Connection conn) throws SQLException {
        String sql = "SELECT id_proveedor, nombre, descripcion, FORMATDATETIME(fecha_creacion, 'dd/MM/yyyy') AS fecha_creacion FROM proveedores WHERE activo = 1 ORDER BY nombre ASC";
        return conn.createStatement().executeQuery(sql);
    }

    public static int spInsertarProveedor(Connection conn, String pNombre, String pDescripcion) throws SQLException {
        String sql = "INSERT INTO proveedores (nombre, descripcion, fecha_creacion, activo) VALUES (?, ?, CURRENT_TIMESTAMP, 1)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, pNombre);
        ps.setString(2, pDescripcion);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : 0;
    }

    public static ResultSet spListarProveedoresPorProducto(Connection conn, Integer pIdProducto) throws SQLException {
        String sql = "SELECT pp.id_producto_proveedor, pp.id_producto, pp.id_proveedor, " +
                "pr.nombre AS proveedor_nombre, pp.clave_proveedor, pp.costo " +
                "FROM producto_proveedores pp " +
                "INNER JOIN proveedores pr ON pp.id_proveedor = pr.id_proveedor " +
                "WHERE pp.id_producto = ? AND pr.activo = 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pIdProducto != null ? pIdProducto : 0);
        return ps.executeQuery();
    }

    public static ResultSet spListarProveedoresPorProducto(Connection conn) throws SQLException {
        return spListarProveedoresPorProducto(conn, 0);
    }

    public static void spAsignarProveedorProducto(Connection conn, Integer pIdProducto, Integer pIdProveedor, String pClaveProveedor, BigDecimal pCosto) throws SQLException {
        String sql = "INSERT INTO producto_proveedores (id_producto, id_proveedor, clave_proveedor, costo, fecha_asociacion) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pIdProducto);
        ps.setInt(2, pIdProveedor);
        ps.setString(3, pClaveProveedor);
        ps.setBigDecimal(4, pCosto);
        ps.executeUpdate();
    }

    public static void spEliminarProveedoresProducto(Connection conn, Integer pIdProducto) throws SQLException {
        String sql = "DELETE FROM producto_proveedores WHERE id_producto = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, pIdProducto);
        ps.executeUpdate();
    }
}
