import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DetalleVenta {
    private final Producto producto;
    private final int cantidad;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = Objects.requireNonNull(
                producto, "El producto no puede ser nulo.");

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return producto.getPrecio()
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }
}