-- =============================================================================
-- PROYECTO: AlMaximoIT - Luis Bonifaz
-- SCRIPT DE BASE DE DATOS Y PROCEDIMIENTOS ALMACENADOS (H2 IN-MEMORY)
-- TABLAS, ALIAS DE JAVA PROCEDURES Y DATOS SEMILLA
-- =============================================================================

DROP TABLE IF EXISTS producto_proveedores;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS tipos_producto;

CREATE TABLE tipos_producto (
    id_tipo_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo INT DEFAULT 1
);

CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo INT DEFAULT 1
);

CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    id_tipo_producto INT NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo INT DEFAULT 1,
    FOREIGN KEY (id_tipo_producto) REFERENCES tipos_producto(id_tipo_producto) ON DELETE CASCADE
);

CREATE TABLE producto_proveedores (
    id_producto_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_proveedor INT NOT NULL,
    clave_proveedor VARCHAR(50) NOT NULL,
    costo DECIMAL(10,2) NOT NULL,
    fecha_asociacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto) ON DELETE CASCADE,
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- REGISTRO DE ALIAS DE PROCEDIMIENTOS ALMACENADOS (COMPATIBILIDAD H2)
-- -----------------------------------------------------------------------------

DROP ALIAS IF EXISTS SP_BUSCAR_PRODUCTOS;
DROP ALIAS IF EXISTS SP_OBTENER_PRODUCTO_POR_ID;
DROP ALIAS IF EXISTS SP_INSERTAR_PRODUCTO;
DROP ALIAS IF EXISTS SP_ACTUALIZAR_PRODUCTO;
DROP ALIAS IF EXISTS SP_ELIMINAR_PRODUCTO;
DROP ALIAS IF EXISTS SP_LISTAR_TIPOS_PRODUCTO;
DROP ALIAS IF EXISTS SP_LISTAR_PROVEEDORES;
DROP ALIAS IF EXISTS SP_INSERTAR_PROVEEDOR;
DROP ALIAS IF EXISTS SP_LISTAR_PROVEEDORES_POR_PRODUCTO;
DROP ALIAS IF EXISTS SP_ASIGNAR_PROVEEDOR_PRODUCTO;
DROP ALIAS IF EXISTS SP_ELIMINAR_PROVEEDORES_PRODUCTO;

CREATE ALIAS SP_BUSCAR_PRODUCTOS FOR "com.almaximoit.productos.config.H2StoredProcedures.spBuscarProductos";
CREATE ALIAS SP_OBTENER_PRODUCTO_POR_ID FOR "com.almaximoit.productos.config.H2StoredProcedures.spObtenerProductoPorId";
CREATE ALIAS SP_INSERTAR_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spInsertarProducto";
CREATE ALIAS SP_ACTUALIZAR_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spActualizarProducto";
CREATE ALIAS SP_ELIMINAR_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spEliminarProducto";
CREATE ALIAS SP_LISTAR_TIPOS_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spListarTiposProducto";
CREATE ALIAS SP_LISTAR_PROVEEDORES FOR "com.almaximoit.productos.config.H2StoredProcedures.spListarProveedores";
CREATE ALIAS SP_INSERTAR_PROVEEDOR FOR "com.almaximoit.productos.config.H2StoredProcedures.spInsertarProveedor";
CREATE ALIAS SP_LISTAR_PROVEEDORES_POR_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spListarProveedoresPorProducto";
CREATE ALIAS SP_ASIGNAR_PROVEEDOR_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spAsignarProveedorProducto";
CREATE ALIAS SP_ELIMINAR_PROVEEDORES_PRODUCTO FOR "com.almaximoit.productos.config.H2StoredProcedures.spEliminarProveedoresProducto";

-- -----------------------------------------------------------------------------
-- DATOS SEMILLA ENRIQUECIDOS (SEED DATA)
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

-- 3. Catálogo de Productos (14 Productos Realistas y Coherentes)
INSERT INTO productos (clave, nombre, precio, id_tipo_producto) VALUES
('PROD-001', 'Smart TV 55 Crystal UHD 4K', 11999.00, 1),
('PROD-002', 'Barra de Sonido Bluetooth 2.1 Ch con Subwoofer', 3499.00, 1),
('PROD-003', 'Audífonos Inalámbricos Cancelación de Ruido', 2199.00, 1),
('PROD-004', 'Leche Entera Ultrapasteurizada 1 Litro', 27.50, 2),
('PROD-005', 'Yogurt Griego Natural Sin Azúcar 1kg', 78.00, 2),
('PROD-006', 'Café Soluble Clásico Frasco 200g', 94.50, 3),
('PROD-007', 'Aceite Vegetal Comestible 800ml', 42.00, 3),
('PROD-008', 'Pan de Caja Blanco Extra Grande 680g', 48.50, 3),
('PROD-009', 'Jabón Corporal Líquido Humectante 500ml', 62.00, 4),
('PROD-010', 'Shampoo Control Caspa y Fuerza 700ml', 89.00, 4),
('PROD-011', 'Detergente Líquido Concentrado Ropa 3L', 165.00, 5),
('PROD-012', 'Papel Higiénico Doble Hoja 12 Rollos', 115.00, 5),
('PROD-013', 'Refresco Sabor Cola No Retornable 2.5L', 38.00, 6),
('PROD-014', 'Agua Purificada de Manantial 1.5L (Genérico)', 14.00, 6);

-- 4. Relación Producto - Proveedores (Con Clave de Proveedor y Costo)
-- Nota: El producto 14 ('PROD-014') se mantiene intencionalmente SIN PROVEEDORES asignados
INSERT INTO producto_proveedores (id_producto, id_proveedor, clave_proveedor, costo) VALUES
(1, 1, 'SAM-TV55-4K', 8900.00),
(1, 4, 'LG-PANEL55-ALT', 9200.00),
(2, 1, 'SAM-HW-B450', 2550.00),
(2, 4, 'LG-SND-SP2', 2600.00),
(3, 1, 'SAM-BUDS-FE', 1450.00),
(4, 2, 'NES-LEC-ENT1L', 20.50),
(5, 2, 'NES-YOG-GR1K', 56.00),
(6, 2, 'NES-CAF-CL200', 68.00),
(7, 3, 'UNI-CAP-800ML', 29.50),
(8, 6, 'BIM-PAN-BLA680', 36.00),
(9, 3, 'UNI-DOV-L500', 41.00),
(10, 5, 'PG-HND-SH700', 61.50),
(10, 3, 'UNI-CLR-SH700', 63.00),
(11, 5, 'PG-ARI-LIQ3L', 118.00),
(12, 8, 'KIM-KLE-12R', 82.00),
(13, 7, 'FEM-CC-2500ML', 26.50);
