import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Papeleria extends Producto {
    private final String marca;
    private int existencias;

    public Papeleria(
            String codigo,
            String nombre,
            BigDecimal precio,
            String marca,
            int existencias) {
        super(codigo, nombre, precio);

        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("La marca no puede estar vacía.");
        }
        if (existencias < 0) {
            throw new IllegalArgumentException(
                    "Las existencias no pueden ser negativas.");
        }

        this.marca = marca.trim();
        this.existencias = existencias;
    }

    public String getMarca() {
        return marca;
    }

    public int getExistencias() {
        return existencias;
    }

    public void vender(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a vender debe ser mayor que cero.");
        }
        if (cantidad > existencias) {
            throw new IllegalArgumentException(
                    "No hay suficientes unidades disponibles.");
        }

        existencias -= cantidad;
    }

    public void reponer(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad a reponer debe ser mayor que cero.");
        }

        existencias += cantidad;
    }

    @Override
    public BigDecimal calcularIva(BigDecimal subtotal) {
        Objects.requireNonNull(subtotal, "El subtotal no puede ser nulo.");
        if (subtotal.signum() < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no puede ser negativo.");
        }

        return subtotal.multiply(new BigDecimal("0.15"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}