package com.almaximoit.productos.controller.web;

import com.almaximoit.productos.dto.ProductoFormDTO;
import com.almaximoit.productos.dto.ProductoProveedorDTO;
import com.almaximoit.productos.model.Producto;
import com.almaximoit.productos.service.ProductoService;
import com.almaximoit.productos.service.ProveedorService;
import com.almaximoit.productos.service.TipoProductoService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProductoWebController {

    private final ProductoService productoService;
    private final TipoProductoService tipoProductoService;
    private final ProveedorService proveedorService;

    public ProductoWebController(ProductoService productoService,
                                  TipoProductoService tipoProductoService,
                                  ProveedorService proveedorService) {
        this.productoService = productoService;
        this.tipoProductoService = tipoProductoService;
        this.proveedorService = proveedorService;
    }

    /**
     * Vinculador de datos: Convierte cadenas vacías en null para evitar errores de enlace de tipos.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Redirección a la vista principal del catálogo.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/productos";
    }

    /**
     * Vista principal: listado y filtrado de productos.
     */
    @GetMapping("/productos")
    public String listarProductos(
            @RequestParam(required = false) String clave,
            @RequestParam(required = false) Integer idTipoProducto,
            Model model) {

        List<Producto> productos = productoService.buscarProductos(clave, idTipoProducto);

        model.addAttribute("productos", productos);
        model.addAttribute("tiposProducto", tipoProductoService.listarTiposProducto());
        model.addAttribute("claveParam", clave != null ? clave : "");
        model.addAttribute("idTipoProductoParam", idTipoProducto != null ? idTipoProducto : 0);

        return "productos/list";
    }

    /**
     * Vista de formulario: registro de un nuevo producto.
     */
    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(Model model) {
        ProductoFormDTO form = new ProductoFormDTO();
        form.getProveedores().add(new ProductoProveedorDTO());

        model.addAttribute("productoForm", form);
        model.addAttribute("tiposProducto", tipoProductoService.listarTiposProducto());
        model.addAttribute("proveedoresCatalogo", proveedorService.listarProveedores());
        model.addAttribute("esEdicion", false);

        return "productos/form";
    }

    /**
     * Vista de formulario: edición de un producto existente.
     */
    @GetMapping("/productos/editar/{id}")
    public String editarProductoForm(@PathVariable Integer id, Model model, RedirectAttributes flash) {
        ProductoFormDTO form = productoService.obtenerProductoFormPorId(id);
        if (form == null) {
            flash.addFlashAttribute("error", "El producto solicitado no existe.");
            return "redirect:/productos";
        }

        if (form.getProveedores().isEmpty()) {
            form.getProveedores().add(new ProductoProveedorDTO());
        }

        model.addAttribute("productoForm", form);
        model.addAttribute("tiposProducto", tipoProductoService.listarTiposProducto());
        model.addAttribute("proveedoresCatalogo", proveedorService.listarProveedores());
        model.addAttribute("esEdicion", true);

        return "productos/form";
    }

    /**
     * Vista dedicada para la configuración de proveedores de un producto.
     */
    @GetMapping("/productos/proveedores/{id}")
    public String gestionarProveedoresView(@PathVariable Integer id, Model model, RedirectAttributes flash) {
        ProductoFormDTO form = productoService.obtenerProductoFormPorId(id);
        if (form == null) {
            flash.addFlashAttribute("error", "El producto solicitado no existe.");
            return "redirect:/productos";
        }

        if (form.getProveedores().isEmpty()) {
            form.getProveedores().add(new ProductoProveedorDTO());
        }

        model.addAttribute("productoForm", form);
        model.addAttribute("proveedoresCatalogo", proveedorService.listarProveedores());

        return "productos/proveedores";
    }

    /**
     * Procesamiento del formulario de creación y actualización de productos con control seguro de errores.
     */
    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute("productoForm") ProductoFormDTO form,
                                  BindingResult bindingResult,
                                  RedirectAttributes flash) {
        try {
            if (form.getProveedores() != null) {
                form.getProveedores().removeIf(p -> p == null || p.getIdProveedor() == null || p.getIdProveedor() == 0);
            }

            if (form.getClave() == null || form.getClave().trim().isEmpty() ||
                form.getNombre() == null || form.getNombre().trim().isEmpty() ||
                form.getIdTipoProducto() == null || form.getIdTipoProducto() == 0 ||
                form.getPrecio() == null) {
                flash.addFlashAttribute("error", "Favor de completar todos los campos obligatorios del producto.");
                return "redirect:/productos";
            }

            productoService.guardarProducto(form);
            flash.addFlashAttribute("mensaje", "Producto '" + form.getNombre() + "' guardado con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        return "redirect:/productos";
    }

    /**
     * Eliminación de producto desde la interfaz gráfica web.
     */
    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            productoService.eliminarProducto(id);
            flash.addFlashAttribute("mensaje", "Producto eliminado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }
        return "redirect:/productos";
    }
}
