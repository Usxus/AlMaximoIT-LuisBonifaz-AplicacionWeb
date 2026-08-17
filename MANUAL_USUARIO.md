# Manual de Usuario y Operación
## Sistema de Gestión de Productos - AlMaximoIT (Luis Bonifaz)

Guía técnica y funcional para la operación del catálogo de productos y servicios REST.

---

## 1. Puntos de Acceso al Sistema

* **Interfaz Web (Catálogo)**: `http://localhost:8080/AlMaximoIT-LuisBonifaz/productos`
* **Swagger UI (Documentación API)**: `http://localhost:8080/AlMaximoIT-LuisBonifaz/swagger-ui.html`
* **Estado y Diagnóstico de BD**: `http://localhost:8080/AlMaximoIT-LuisBonifaz/api/status`
* **Consola H2 Database** *(En perfil H2)*: `http://localhost:8080/AlMaximoIT-LuisBonifaz/h2-console`

---

## 2. Operación del Catálogo Web

### A. Búsqueda y Filtrado
1. Ingrese texto en **Clave o Nombre de Producto** (búsqueda por SKU o coincidencia de texto).
2. Seleccione un **Tipo de Producto** en el menú desplegable (o *Todos los Tipos*).
3. Haga clic en **Filtrar** (`sp_buscar_productos`).
4. Para limpiar los filtros y ver todos los registros, presione el botón de reinicio.

---

### B. Registro de Nuevo Producto (`/productos/nuevo`)
El formulario utiliza navegación guiada en 2 pasos:

1. **Pestaña 1: Información General**:
   * Complete los campos obligatorios: **Clave Interna (SKU)**, **Nombre**, **Tipo de Producto** y **Precio de Venta ($)**.
   * Haga clic en **Configurar Proveedores** (valida campos y avanza automáticamente a la Pestaña 2).

2. **Pestaña 2: Proveedores Relacionados**:
   * *(Opcional)* Asocie uno o más distribuidores con el botón **+ Agregar Fila**.
   * Indique: **Proveedor**, **Clave Producto del Proveedor** y **Costo ($)**.
   * Haga clic en **Guardar Producto** (Verde) para persistir los datos en base de datos.

---

### C. Modificación de Producto Existente (`/productos/editar/{id}`)
* **Guardar Cambios Directo**: Si solo desea actualizar precio, nombre o categoría, use el botón verde **Guardar Cambios** desde la Pestaña 1 sin necesidad de entrar a proveedores.
* **Modificar Proveedores**: Si desea ajustar distribuidores o costos, use **Configurar Proveedores**, realice los ajustes y guarde con **Guardar Cambios** en la Pestaña 2.

---

### D. Eliminación Segura (Soft Delete)
1. En la tabla principal, haga clic en el botón de **Eliminar** (bote de basura rojo).
2. En la notificación emergente de confirmación:
   * **Cancelar**: Cancela la acción sin alterar datos.
   * **Aceptar**: Ejecuta la baja lógica (`sp_eliminar_producto` -> `activo = 0`), preservando el histórico y desvinculando asociaciones.
3. *Reactivación automática*: Si posteriormente vuelve a registrar un producto con la misma clave (SKU), el sistema reactiva el registro existente (`activo = 1`) y actualiza sus valores.

---

## 3. Resumen de Endpoints API REST

| Método | Endpoint | Descripción | Stored Procedure Invocado |
|---|---|---|---|
| `GET` | `/api/productos` | Consulta y filtrado de catálogo | `sp_buscar_productos` |
| `GET` | `/api/productos/{id}` | Ficha de producto con proveedores | `sp_obtener_producto_por_id` |
| `POST` | `/api/productos` | Creación / Edición transaccional | `sp_insertar_producto` / `sp_actualizar_producto` |
| `DELETE` | `/api/productos/{id}` | Baja lógica (*Soft Delete*) | `sp_eliminar_producto` |
| `GET` | `/api/proveedores` | Lista de distribuidores activos | `sp_listar_proveedores` |
| `GET` | `/api/tipos-producto` | Lista de categorías activas | `sp_listar_tipos_producto` |
| `GET` | `/api/health` | Diagnóstico de disponibilidad | Metadata JDBC |
| `GET` | `/api/status` | Estado del motor activo (`MySQL`/`H2`) | Metadata JDBC |
