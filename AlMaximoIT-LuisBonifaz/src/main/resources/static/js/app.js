function mostrarNotificacionElegante(titulo, mensaje, campos) {
    const container = document.getElementById("toastNotificacionesContainer");
    if (!container) return;

    container.innerHTML = "";

    const toastDiv = document.createElement("div");
    toastDiv.className = "toast-custom shadow-lg";

    const badgesHTML = Array.isArray(campos) && campos.length > 0
        ? `<div class="d-flex flex-wrap gap-1 mt-2">
             ${campos.map(c => `<span class="badge bg-danger text-white border-0 py-1 px-2 font-monospace fs-7">${c}</span>`).join('')}
           </div>`
        : '';

    toastDiv.innerHTML = `
        <div class="text-danger fs-3 d-flex align-items-center">
            <i class="bi bi-exclamation-circle-fill"></i>
        </div>
        <div class="flex-grow-1">
            <div class="d-flex justify-content-between align-items-center mb-1">
                <strong class="text-dark font-weight-bold fs-6">${titulo}</strong>
                <button type="button" class="btn-close ms-2" style="font-size: 0.75rem;" onclick="cerrarToast(this)"></button>
            </div>
            <p class="mb-0 text-secondary fs-7">${mensaje}</p>
            ${badgesHTML}
        </div>
    `;

    container.appendChild(toastDiv);

    setTimeout(() => {
        if (toastDiv && toastDiv.parentNode) {
            toastDiv.classList.add("toast-hide");
            setTimeout(() => {
                if (toastDiv && toastDiv.parentNode) toastDiv.remove();
            }, 300);
        }
    }, 4500);
}

function cerrarToast(btn) {
    const toast = btn.closest(".toast-custom");
    if (toast) {
        toast.classList.add("toast-hide");
        setTimeout(() => toast.remove(), 300);
    }
}

function cerrarAlerta(btn) {
    const alertEl = btn.closest(".alert");
    if (alertEl) {
        alertEl.style.transition = "opacity 0.35s ease, transform 0.35s ease";
        alertEl.style.opacity = "0";
        alertEl.style.transform = "translateY(-12px)";
        setTimeout(() => {
            if (alertEl && alertEl.parentNode) alertEl.remove();
        }, 350);
    }
}

function validarCamposGenerales() {
    const inputClave = document.getElementById("clave");
    const inputNombre = document.getElementById("nombre");
    const selectTipo = document.getElementById("idTipoProducto");
    const inputPrecio = document.getElementById("precio");

    let camposFaltantes = [];
    let primerElementoInvalido = null;

    [inputClave, inputNombre, selectTipo, inputPrecio].forEach(el => {
        if (el) el.classList.remove("is-invalid");
    });

    if (!inputClave || !inputClave.value.trim()) {
        camposFaltantes.push("Clave");
        if (inputClave) inputClave.classList.add("is-invalid");
        if (!primerElementoInvalido) primerElementoInvalido = inputClave;
    }

    if (!inputNombre || !inputNombre.value.trim()) {
        camposFaltantes.push("Nombre");
        if (inputNombre) inputNombre.classList.add("is-invalid");
        if (!primerElementoInvalido) primerElementoInvalido = inputNombre;
    }

    if (!selectTipo || !selectTipo.value || selectTipo.value === "0" || selectTipo.value === "") {
        camposFaltantes.push("Tipo de Producto");
        if (selectTipo) selectTipo.classList.add("is-invalid");
        if (!primerElementoInvalido) primerElementoInvalido = selectTipo;
    }

    if (!inputPrecio || !inputPrecio.value.trim() || isNaN(parseFloat(inputPrecio.value)) || parseFloat(inputPrecio.value) <= 0) {
        camposFaltantes.push("Precio de Venta");
        if (inputPrecio) inputPrecio.classList.add("is-invalid");
        if (!primerElementoInvalido) primerElementoInvalido = inputPrecio;
    }

    if (camposFaltantes.length > 0) {
        mostrarNotificacionElegante(
            "Faltan datos por llenar",
            "Favor de completarlos para poder configurar los proveedores.",
            camposFaltantes
        );

        if (primerElementoInvalido) {
            primerElementoInvalido.focus();
        }
        return false;
    }

    return true;
}

function guardarCambiosDirecto() {
    if (!validarCamposGenerales()) {
        return;
    }
    const form = document.getElementById("formProductoPrincipal");
    if (form) {
        form.submit();
    }
}

function cambiarPestana(tabName) {
    const btnGeneral = document.getElementById("tab-general-btn");
    const btnProveedores = document.getElementById("tab-proveedores-btn");
    const paneGeneral = document.getElementById("tab-general");
    const paneProveedores = document.getElementById("tab-proveedores");
    const iconoCandadoGeneral = document.getElementById("iconoCandadoGeneral");
    const iconoCandadoProveedores = document.getElementById("iconoCandadoProveedores");

    if (tabName === 'proveedores') {
        if (!validarCamposGenerales()) {
            return;
        }

        if (btnGeneral) {
            btnGeneral.classList.remove("active");
            btnGeneral.classList.add("text-muted");
            btnGeneral.style.pointerEvents = "none";
            btnGeneral.style.opacity = "0.65";
            btnGeneral.style.cursor = "not-allowed";
            btnGeneral.setAttribute("aria-selected", "false");
            btnGeneral.title = "Bloqueado: Use el botón Cancelar / Regresar";
        }
        if (iconoCandadoGeneral) {
            iconoCandadoGeneral.className = "bi bi-lock-fill me-1";
        }

        if (btnProveedores) {
            btnProveedores.classList.add("active");
            btnProveedores.classList.remove("text-muted");
            btnProveedores.style.pointerEvents = "auto";
            btnProveedores.style.opacity = "1";
            btnProveedores.style.cursor = "pointer";
            btnProveedores.setAttribute("aria-selected", "true");
            btnProveedores.title = "";
        }
        if (iconoCandadoProveedores) {
            iconoCandadoProveedores.className = "bi bi-truck me-1";
        }

        if (paneGeneral) {
            paneGeneral.classList.remove("show", "active");
        }
        if (paneProveedores) {
            paneProveedores.classList.add("show", "active");
        }
    } else {
        if (btnProveedores) {
            btnProveedores.classList.remove("active");
            btnProveedores.classList.add("text-muted");
            btnProveedores.style.pointerEvents = "none";
            btnProveedores.style.opacity = "0.65";
            btnProveedores.style.cursor = "not-allowed";
            btnProveedores.setAttribute("aria-selected", "false");
            btnProveedores.title = "Bloqueado: Use el botón Configurar Proveedores";
        }
        if (iconoCandadoProveedores) {
            iconoCandadoProveedores.className = "bi bi-lock-fill me-1";
        }

        if (btnGeneral) {
            btnGeneral.classList.add("active");
            btnGeneral.classList.remove("text-muted");
            btnGeneral.style.pointerEvents = "auto";
            btnGeneral.style.opacity = "1";
            btnGeneral.style.cursor = "pointer";
            btnGeneral.setAttribute("aria-selected", "true");
            btnGeneral.title = "";
        }
        if (iconoCandadoGeneral) {
            iconoCandadoGeneral.className = "bi bi-info-circle me-1";
        }

        if (paneProveedores) {
            paneProveedores.classList.remove("show", "active");
        }
        if (paneGeneral) {
            paneGeneral.classList.add("show", "active");
        }
    }
}

function agregarFilaProveedor() {
    const container = document.getElementById("tablaProveedoresBody");
    if (!container) return;

    const index = container.querySelectorAll("tr").length;

    let optionsHTML = '<option value="">-- Seleccionar Proveedor --</option>';
    if (typeof CATALOGO_PROVEEDORES !== "undefined" && Array.isArray(CATALOGO_PROVEEDORES) && CATALOGO_PROVEEDORES.length > 0) {
        CATALOGO_PROVEEDORES.forEach(p => {
            optionsHTML += `<option value="${p.idProveedor}">${p.nombre}</option>`;
        });
    } else {
        const primerSelect = container.querySelector(".select-proveedor-item");
        if (primerSelect) {
            optionsHTML = primerSelect.innerHTML;
        }
    }

    const tr = document.createElement("tr");
    tr.className = "fila-proveedor";
    tr.innerHTML = `
        <td>
            <select name="proveedores[${index}].idProveedor" class="form-select select-proveedor-item">
                ${optionsHTML}
            </select>
        </td>
        <td>
            <input type="text" name="proveedores[${index}].claveProveedor" class="form-control input-clave-proveedor" placeholder="Ej. UNI-JAB-500ML" />
        </td>
        <td>
            <div class="input-group">
                <span class="input-group-text">$</span>
                <input type="number" step="0.01" min="0" name="proveedores[${index}].costo" class="form-control input-costo-proveedor" placeholder="0.00" />
            </div>
        </td>
        <td class="text-center">
            <button type="button" class="btn btn-outline-danger btn-sm btn-eliminar-fila" onclick="eliminarFilaProveedor(this)">
                <i class="bi bi-trash"></i> Eliminar
            </button>
        </td>
    `;
    container.appendChild(tr);
}

function eliminarFilaProveedor(btn) {
    const container = document.getElementById("tablaProveedoresBody");
    if (!container) return;

    const tr = btn.closest("tr");
    const totalFilas = container.querySelectorAll("tr").length;

    if (totalFilas > 1) {
        tr.remove();
        reindexarFilasProveedores();
    } else {
        const select = tr.querySelector("select");
        const inputs = tr.querySelectorAll("input");
        if (select) select.value = "";
        inputs.forEach(input => input.value = "");
    }
}

function reindexarFilasProveedores() {
    const container = document.getElementById("tablaProveedoresBody");
    if (!container) return;

    const filas = container.querySelectorAll("tr");
    filas.forEach((tr, idx) => {
        const select = tr.querySelector("select");
        const inputClave = tr.querySelector(".input-clave-proveedor");
        const inputCosto = tr.querySelector(".input-costo-proveedor");

        if (select) select.name = `proveedores[${idx}].idProveedor`;
        if (inputClave) inputClave.name = `proveedores[${idx}].claveProveedor`;
        if (inputCosto) inputCosto.name = `proveedores[${idx}].costo`;
    });
}

document.addEventListener("DOMContentLoaded", function() {
    const alertasFlash = document.querySelectorAll(".alert-dismissible");
    alertasFlash.forEach(alerta => {
        setTimeout(() => {
            if (alerta && alerta.parentNode) {
                alerta.style.transition = "opacity 0.4s ease, transform 0.4s ease";
                alerta.style.opacity = "0";
                alerta.style.transform = "translateY(-12px)";
                setTimeout(() => {
                    if (alerta && alerta.parentNode) alerta.remove();
                }, 400);
            }
        }, 4000);
    });

    const inputs = ["clave", "nombre", "idTipoProducto", "precio"];
    inputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener("input", function() {
                if (el.value.trim()) {
                    el.classList.remove("is-invalid");
                }
            });
            el.addEventListener("change", function() {
                if (el.value.trim()) {
                    el.classList.remove("is-invalid");
                }
            });
        }
    });

    const modalEliminar = document.getElementById("modalEliminarConfirm");
    if (modalEliminar) {
        modalEliminar.addEventListener("click", function(e) {
            if (e.target === modalEliminar) {
                cerrarModalEliminar();
            }
        });
    }
});

function mostrarModalEliminar(urlEliminar, nombreProducto, claveProducto) {
    const modal = document.getElementById("modalEliminarConfirm");
    const spanNombre = document.getElementById("modalEliminarNombre");
    const spanClave = document.getElementById("modalEliminarClave");
    const btnAceptar = document.getElementById("btnConfirmarEliminarAccion");

    if (!modal) {
        if (confirm("¿Está seguro de eliminar este producto? Se eliminarán también sus asociaciones con proveedores sobre este producto.")) {
            window.location.href = urlEliminar;
        }
        return;
    }

    if (spanNombre) spanNombre.textContent = nombreProducto || "Producto";
    if (spanClave) spanClave.textContent = claveProducto || "";
    if (btnAceptar) btnAceptar.href = urlEliminar;

    modal.classList.add("show");
    document.body.style.overflow = "hidden";
}

function cerrarModalEliminar() {
    const modal = document.getElementById("modalEliminarConfirm");
    if (modal) {
        modal.classList.remove("show");
        document.body.style.overflow = "";
    }
}

document.addEventListener("keydown", function(e) {
    if (e.key === "Escape") {
        cerrarModalEliminar();
    }
});
