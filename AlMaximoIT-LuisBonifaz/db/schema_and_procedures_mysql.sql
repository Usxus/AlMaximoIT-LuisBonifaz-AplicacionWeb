-- =============================================================================
-- PROYECTO: AlMaximoIT - Luis Bonifaz
-- SCRIPT DE BASE DE DATOS Y PROCEDIMIENTOS ALMACENADOS (MYSQL 8.0+)
-- TABLAS, PROCEDIMIENTOS ALMACENADOS Y DATOS SEMILLA
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_productos 
DEFAULT CHARACTER SET utf8mb4;

USE db_productos;

-- -----------------------------------------------------------------------------
-- 1. ELIMINACIÓN DE TABLAS PREVIAS (ORDEN DE DEPENDENCIAS)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS producto_proveedores;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS tipos_producto;

-- -----------------------------------------------------------------------------
-- 2. DEFINICIÓN DE TABLAS CON AUDITORÍA Y LLAVES FORÁNEAS
-- -----------------------------------------------------------------------------

CREATE TABLE tipos_producto (
    id_tipo_producto INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Clave primaria autoincremental',
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre de la categoría',
    descripcion VARCHAR(255) COMMENT 'Descripción funcional del tipo de producto',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Auditoría: Fecha de creación',
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Auditoría: Última modificación',
    activo TINYINT(1) DEFAULT 1 COMMENT 'Auditoría: 1=Activo, 0=Inactivo (Soft Delete)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Catálogo maestro de tipos de producto';

CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Clave primaria autoincremental',
    nombre VARCHAR(100) NOT NULL COMMENT 'Nombre o razón social del proveedor',
    descripcion VARCHAR(255) COMMENT 'Descripción o tipo de distribución',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Auditoría: Fecha de creación',
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Auditoría: Última modificación',
    activo TINYINT(1) DEFAULT 1 COMMENT 'Auditoría: 1=Activo, 0=Inactivo (Soft Delete)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Catálogo maestro de proveedores';

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Clave primaria autoincremental',
    clave VARCHAR(50) NOT NULL UNIQUE COMMENT 'Clave interna única (SKU)',
    nombre VARCHAR(150) NOT NULL COMMENT 'Nombre comercial del producto',
    precio DECIMAL(10,2) NOT NULL COMMENT 'Precio de venta al público',
    id_tipo_producto INT NOT NULL COMMENT 'Llave foránea a tipos_producto',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Auditoría: Fecha de registro del producto',
    fecha_modificacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Auditoría: Última actualización',
    activo TINYINT(1) DEFAULT 1 COMMENT 'Auditoría: 1=Activo, 0=Inactivo (Soft Delete)',
    CONSTRAINT fk_producto_tipo FOREIGN KEY (id_tipo_producto) REFERENCES tipos_producto(id_tipo_producto) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Catálogo principal de productos';

CREATE TABLE producto_proveedores (
    id_producto_proveedor INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Clave primaria autoincremental',
    id_producto INT NOT NULL COMMENT 'Llave foránea a productos',
    id_proveedor INT NOT NULL COMMENT 'Llave foránea a proveedores',
    clave_proveedor VARCHAR(50) NOT NULL COMMENT 'Clave asignada por el proveedor',
    costo DECIMAL(10,2) NOT NULL COMMENT 'Precio de costo otorgado por el proveedor',
    fecha_asociacion DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Auditoría: Fecha de vinculación',
    CONSTRAINT fk_pp_producto FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE CASCADE,
    CONSTRAINT fk_pp_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Relación N:M de productos con proveedores, costos y claves';

-- -----------------------------------------------------------------------------
-- 3. PROCEDIMIENTOS ALMACENADOS (STORED PROCEDURES MYSQL)
-- -----------------------------------------------------------------------------

DELIMITER //

DROP PROCEDURE IF EXISTS sp_buscar_productos //
CREATE PROCEDURE sp_buscar_productos(
    IN p_clave VARCHAR(50) CHARACTER SET utf8mb4,
    IN p_id_tipo_producto INT
)
BEGIN
    SELECT 
        p.id_producto,
        p.clave,
        p.nombre,
        p.precio,
        p.id_tipo_producto,
        tp.nombre AS tipo_producto_nombre,
        COUNT(pp.id_proveedor) AS cantidad_proveedores,
        DATE_FORMAT(p.fecha_creacion, '%d/%m/%Y %H:%i') AS fecha_creacion,
        p.activo
    FROM productos p
    INNER JOIN tipos_producto tp ON p.id_tipo_producto = tp.id_tipo_producto
    LEFT JOIN producto_proveedores pp ON p.id_producto = pp.id_producto
    WHERE p.activo = 1
      AND (p_clave IS NULL OR p_clave = '' OR p.clave LIKE CONCAT('%', p_clave, '%') OR p.nombre LIKE CONCAT('%', p_clave, '%'))
      AND (p_id_tipo_producto IS NULL OR p_id_tipo_producto = 0 OR p.id_tipo_producto = p_id_tipo_producto)
    GROUP BY p.id_producto, p.clave, p.nombre, p.precio, p.id_tipo_producto, tp.nombre, p.fecha_creacion, p.activo
    ORDER BY p.id_producto DESC;
END //

DROP PROCEDURE IF EXISTS sp_obtener_producto_por_id //
CREATE PROCEDURE sp_obtener_producto_por_id(
    IN p_id_producto INT
)
BEGIN
    SELECT 
        p.id_producto,
        p.clave,
        p.nombre,
        p.precio,
        p.id_tipo_producto,
        tp.nombre AS tipo_producto_nombre,
        DATE_FORMAT(p.fecha_creacion, '%d/%m/%Y %H:%i') AS fecha_creacion,
        p.activo
    FROM productos p
    INNER JOIN tipos_producto tp ON p.id_tipo_producto = tp.id_tipo_producto
    WHERE p.id_producto = p_id_producto AND p.activo = 1;
END //

DROP PROCEDURE IF EXISTS sp_insertar_producto //
CREATE PROCEDURE sp_insertar_producto(
    IN p_clave VARCHAR(50) CHARACTER SET utf8mb4,
    IN p_nombre VARCHAR(150) CHARACTER SET utf8mb4,
    IN p_precio DECIMAL(10,2),
    IN p_id_tipo_producto INT,
    OUT p_id_producto INT
)
BEGIN
    DECLARE v_id_existente INT DEFAULT NULL;
    DECLARE v_activo TINYINT DEFAULT NULL;

    SELECT id_producto, activo INTO v_id_existente, v_activo
    FROM productos
    WHERE clave = p_clave
    LIMIT 1;

    IF v_id_existente IS NOT NULL THEN
        IF v_activo = 0 THEN
            -- Reactivar el producto previamente eliminado con los nuevos datos
            UPDATE productos
            SET nombre = p_nombre,
                precio = p_precio,
                id_tipo_producto = p_id_tipo_producto,
                activo = 1,
                fecha_modificacion = NOW()
            WHERE id_producto = v_id_existente;
            
            -- Limpiar proveedores anteriores para que queden solo los nuevos
            DELETE FROM producto_proveedores WHERE id_producto = v_id_existente;
            
            SET p_id_producto = v_id_existente;
        ELSE
            -- El producto ya existe y está activo, lanzar error descriptivo
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Ya existe un producto activo registrado con la clave especificada.';
        END IF;
    ELSE
        -- No existe, insertar nuevo registro
        INSERT INTO productos (clave, nombre, precio, id_tipo_producto, fecha_creacion, activo)
        VALUES (p_clave, p_nombre, p_precio, p_id_tipo_producto, NOW(), 1);
        SET p_id_producto = LAST_INSERT_ID();
    END IF;
END //

DROP PROCEDURE IF EXISTS sp_actualizar_producto //
CREATE PROCEDURE sp_actualizar_producto(
    IN p_id_producto INT,
    IN p_clave VARCHAR(50) CHARACTER SET utf8mb4,
    IN p_nombre VARCHAR(150) CHARACTER SET utf8mb4,
    IN p_precio DECIMAL(10,2),
    IN p_id_tipo_producto INT
)
BEGIN
    UPDATE productos
    SET clave = p_clave,
        nombre = p_nombre,
        precio = p_precio,
        id_tipo_producto = p_id_tipo_producto,
        fecha_modificacion = NOW()
    WHERE id_producto = p_id_producto;
END //

DROP PROCEDURE IF EXISTS sp_eliminar_producto //
CREATE PROCEDURE sp_eliminar_producto(
    IN p_id_producto INT
)
BEGIN
    UPDATE productos SET activo = 0, fecha_modificacion = NOW() WHERE id_producto = p_id_producto;
END //

DROP PROCEDURE IF EXISTS sp_listar_tipos_producto //
CREATE PROCEDURE sp_listar_tipos_producto()
BEGIN
    SELECT id_tipo_producto, nombre, descripcion, DATE_FORMAT(fecha_creacion, '%d/%m/%Y') AS fecha_creacion
    FROM tipos_producto
    WHERE activo = 1
    ORDER BY nombre ASC;
END //

DROP PROCEDURE IF EXISTS sp_listar_proveedores //
CREATE PROCEDURE sp_listar_proveedores()
BEGIN
    SELECT id_proveedor, nombre, descripcion, DATE_FORMAT(fecha_creacion, '%d/%m/%Y') AS fecha_creacion
    FROM proveedores
    WHERE activo = 1
    ORDER BY nombre ASC;
END //

DROP PROCEDURE IF EXISTS sp_insertar_proveedor //
CREATE PROCEDURE sp_insertar_proveedor(
    IN p_nombre VARCHAR(100) CHARACTER SET utf8mb4,
    IN p_descripcion VARCHAR(255) CHARACTER SET utf8mb4,
    OUT p_id_proveedor INT
)
BEGIN
    INSERT INTO proveedores (nombre, descripcion, fecha_creacion, activo)
    VALUES (p_nombre, p_descripcion, NOW(), 1);
    SET p_id_proveedor = LAST_INSERT_ID();
END //

DROP PROCEDURE IF EXISTS sp_listar_proveedores_por_producto //
CREATE PROCEDURE sp_listar_proveedores_por_producto(
    IN p_id_producto INT
)
BEGIN
    SELECT 
        pp.id_producto_proveedor,
        pp.id_producto,
        pp.id_proveedor,
        pr.nombre AS proveedor_nombre,
        pp.clave_proveedor,
        pp.costo
    FROM producto_proveedores pp
    INNER JOIN proveedores pr ON pp.id_proveedor = pr.id_proveedor
    WHERE pp.id_producto = p_id_producto AND pr.activo = 1;
END //

DROP PROCEDURE IF EXISTS sp_asignar_proveedor_producto //
CREATE PROCEDURE sp_asignar_proveedor_producto(
    IN p_id_producto INT,
    IN p_id_proveedor INT,
    IN p_clave_proveedor VARCHAR(50) CHARACTER SET utf8mb4,
    IN p_costo DECIMAL(10,2)
)
BEGIN
    INSERT INTO producto_proveedores (id_producto, id_proveedor, clave_proveedor, costo, fecha_asociacion)
    VALUES (p_id_producto, p_id_proveedor, p_clave_proveedor, p_costo, NOW());
END //

DROP PROCEDURE IF EXISTS sp_eliminar_proveedores_producto //
CREATE PROCEDURE sp_eliminar_proveedores_producto(
    IN p_id_producto INT
)
BEGIN
    DELETE FROM producto_proveedores WHERE id_producto = p_id_producto;
END //

DELIMITER ;

-- -----------------------------------------------------------------------------
-- 4. DATOS SEMILLA ENRIQUECIDOS (SEED DATA)
-- -----------------------------------------------------------------------------

-- 1. Catálogo de Tipos de Producto (6 Categorías)
INSERT INTO tipos_producto (nombre, descripcion) VALUES
('Electrónica y Audio', 'Dispositivos electrónicos, componentes, pantallas y equipos de sonido'),
('Lácteos y Refrigerados', 'Productos lácteos pasteurizados, yogures, quesos y embutidos'),
('Abarrotes y Despensa', 'Granos, pastas, aceites, enlatados y productos de consumo diario'),
('Cuidado Personal', 'Artículos de higiene diaria, cuidado de la piel, cabello y aseo dental'),
('Hogar y Limpieza', 'Detergentes, suavizantes, desinfectantes y consumibles del hogar'),
('Bebidas y Refrescos', 'Aguas purificadas, jugos naturales, refrescos y bebidas energéticas');

-- 2. Catálogo de Proveedores (8 Proveedores)
INSERT INTO proveedores (nombre, descripcion) VALUES
('Samsung Electronics México', 'Distribuidor líder oficial de pantallas, smartphones y línea blanca'),
('Nestlé México S.A.', 'Empresa de alimentos, fórmulas lácteas, café soluble y confitería'),
('Unilever de México', 'Distribuidor masivo de marcas de higiene, cuidado del hogar y alimentos'),
('LG Electronics México', 'Fabricante global de electrodomésticos inteligentes y pantallas OLED/4K'),
('Procter & Gamble (P&G)', 'Multinacional proveedora de cuidado personal, limpieza y salud'),
('Grupo Bimbo S.A. de C.V.', 'Compañía global de panificación, botanas, panes y repostería'),
('Coca-Cola FEMSA', 'Principal embotellador y distribuidor de bebidas y refrescos'),
('Kimberly-Clark de México', 'Fabricante de productos de papel, pañuelos e higiene institucional');

-- 3. Catálogo de Productos y Relaciones de Proveedores (14 Productos)
CALL sp_insertar_producto('PROD-001', 'Smart TV 55" Crystal UHD 4K', 11999.00, 1, @p1);
CALL sp_asignar_proveedor_producto(@p1, 1, 'SAM-TV55-4K', 8900.00);
CALL sp_asignar_proveedor_producto(@p1, 4, 'LG-PANEL55-ALT', 9200.00);

CALL sp_insertar_producto('PROD-002', 'Barra de Sonido Bluetooth 2.1 Ch con Subwoofer', 3499.00, 1, @p2);
CALL sp_asignar_proveedor_producto(@p2, 1, 'SAM-HW-B450', 2550.00);
CALL sp_asignar_proveedor_producto(@p2, 4, 'LG-SND-SP2', 2600.00);

CALL sp_insertar_producto('PROD-003', 'Audífonos Inalámbricos Cancelación de Ruido', 2199.00, 1, @p3);
CALL sp_asignar_proveedor_producto(@p3, 1, 'SAM-BUDS-FE', 1450.00);

CALL sp_insertar_producto('PROD-004', 'Leche Entera Ultrapasteurizada 1 Litro', 27.50, 2, @p4);
CALL sp_asignar_proveedor_producto(@p4, 2, 'NES-LEC-ENT1L', 20.50);

CALL sp_insertar_producto('PROD-005', 'Yogurt Griego Natural Sin Azúcar 1kg', 78.00, 2, @p5);
CALL sp_asignar_proveedor_producto(@p5, 2, 'NES-YOG-GR1K', 56.00);

CALL sp_insertar_producto('PROD-006', 'Café Soluble Clásico Frasco 200g', 94.50, 3, @p6);
CALL sp_asignar_proveedor_producto(@p6, 2, 'NES-CAF-CL200', 68.00);

CALL sp_insertar_producto('PROD-007', 'Aceite Vegetal Comestible 800ml', 42.00, 3, @p7);
CALL sp_asignar_proveedor_producto(@p7, 3, 'UNI-CAP-800ML', 29.50);

CALL sp_insertar_producto('PROD-008', 'Pan de Caja Blanco Extra Grande 680g', 48.50, 3, @p8);
CALL sp_asignar_proveedor_producto(@p8, 6, 'BIM-PAN-BLA680', 36.00);

CALL sp_insertar_producto('PROD-009', 'Jabón Corporal Líquido Humectante 500ml', 62.00, 4, @p9);
CALL sp_asignar_proveedor_producto(@p9, 3, 'UNI-DOV-L500', 41.00);

CALL sp_insertar_producto('PROD-010', 'Shampoo Control Caspa y Fuerza 700ml', 89.00, 4, @p10);
CALL sp_asignar_proveedor_producto(@p10, 5, 'PG-HND-SH700', 61.50);
CALL sp_asignar_proveedor_producto(@p10, 3, 'UNI-CLR-SH700', 63.00);

CALL sp_insertar_producto('PROD-011', 'Detergente Líquido Concentrado Ropa 3L', 165.00, 5, @p11);
CALL sp_asignar_proveedor_producto(@p11, 5, 'PG-ARI-LIQ3L', 118.00);

CALL sp_insertar_producto('PROD-012', 'Papel Higiénico Doble Hoja 12 Rollos', 115.00, 5, @p12);
CALL sp_asignar_proveedor_producto(@p12, 8, 'KIM-KLE-12R', 82.00);

CALL sp_insertar_producto('PROD-013', 'Refresco Sabor Cola No Retornable 2.5L', 38.00, 6, @p13);
CALL sp_asignar_proveedor_producto(@p13, 7, 'FEM-CC-2500ML', 26.50);

-- Producto 14: Intencionalmente SIN PROVEEDORES vinculados
CALL sp_insertar_producto('PROD-014', 'Agua Purificada de Manantial 1.5L (Genérico)', 14.00, 6, @p14);
