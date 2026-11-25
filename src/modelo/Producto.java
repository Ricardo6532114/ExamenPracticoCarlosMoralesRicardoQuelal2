package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class Producto {
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;

    protected Producto(String codigo, String nombre, BigDecimal precio) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del producto no puede estar vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        Objects.requireNonNull(precio, "El precio no puede ser nulo.");
        if (precio.signum() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public abstract BigDecimal calcularIva(BigDecimal subtotal);
}
