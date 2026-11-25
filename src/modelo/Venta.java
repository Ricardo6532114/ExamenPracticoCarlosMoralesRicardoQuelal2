package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Venta {
    private static final BigDecimal DESCUENTO_SOCIO = new BigDecimal("0.10");

    private final Cliente cliente;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private boolean finalizada;

    public Venta(Cliente cliente) {
        this.cliente = Objects.requireNonNull(cliente, "El cliente no puede ser nulo.");
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void agregarProducto(Producto producto, int cantidad) {
        if (finalizada) {
            throw new IllegalStateException("No se pueden agregar productos a una venta finalizada.");
        }
        Objects.requireNonNull(producto, "El producto no puede ser nulo.");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        detalles.add(new DetalleVenta(producto, cantidad));
    }

    public void finalizar() {
        if (finalizada) {
            throw new IllegalStateException("La venta ya fue finalizada.");
        }
        if (detalles.isEmpty()) {
            throw new IllegalStateException("Agregue al menos un producto antes de finalizar la venta.");
        }

        validarExistencias();
        for (DetalleVenta detalle : detalles) {
            Producto producto = detalle.getProducto();
            if (producto instanceof LibroFisico) {
                ((LibroFisico) producto).vender(detalle.getCantidad());
            } else if (producto instanceof Papeleria) {
                ((Papeleria) producto).vender(detalle.getCantidad());
            }
        }
        finalizada = true;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        return dinero(subtotal);
    }

    public BigDecimal getDescuento() {
        if (!cliente.esSocio()) {
            return dinero(BigDecimal.ZERO);
        }

        BigDecimal descuento = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            if (!(detalle.getProducto() instanceof Papeleria)) {
                descuento = descuento.add(
                        detalle.getSubtotal().multiply(DESCUENTO_SOCIO));
            }
        }
        return dinero(descuento);
    }

    public BigDecimal getIva() {
        BigDecimal iva = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            iva = iva.add(detalle.getProducto().calcularIva(detalle.getSubtotal()));
        }
        return dinero(iva);
    }

    public BigDecimal getTotal() {
        return dinero(getSubtotal().subtract(getDescuento()).add(getIva()));
    }

    private void validarExistencias() {
        for (int i = 0; i < detalles.size(); i++) {
            DetalleVenta detalle = detalles.get(i);
            Producto producto = detalle.getProducto();
            int cantidadTotal = detalle.getCantidad();

            for (int j = i + 1; j < detalles.size(); j++) {
                DetalleVenta otro = detalles.get(j);
                if (otro.getProducto() == producto) {
                    cantidadTotal += otro.getCantidad();
                }
            }

            if (producto instanceof LibroFisico) {
                if (cantidadTotal > ((LibroFisico) producto).getExistencias()) {
                    throw new IllegalArgumentException(
                            "No hay suficientes ejemplares de " + producto.getNombre() + ".");
                }
            } else if (producto instanceof Papeleria) {
                if (cantidadTotal > ((Papeleria) producto).getExistencias()) {
                    throw new IllegalArgumentException(
                            "No hay suficientes unidades de " + producto.getNombre() + ".");
                }
            }
        }
    }

    private static BigDecimal dinero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static class DetalleVenta {
        private final Producto producto;
        private final int cantidad;

        private DetalleVenta(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public BigDecimal getSubtotal() {
            return dinero(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        }
    }
}
