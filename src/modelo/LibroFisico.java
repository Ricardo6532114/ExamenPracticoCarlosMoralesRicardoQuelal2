package modelo;

import java.math.BigDecimal;
import java.util.Objects;

public class LibroFisico extends Producto {
    private final String autor;
    private int existencias;

    public LibroFisico(
            String codigo,
            String nombre,
            BigDecimal precio,
            String autor,
            int existencias) {
        super(codigo, nombre, precio);

        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        if (existencias < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        }

        this.autor = autor.trim();
        this.existencias = existencias;
    }

    public String getAutor() {
        return autor;
    }

    public int getExistencias() {
        return existencias;
    }

    public void vender(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a vender debe ser mayor que cero.");
        }
        if (cantidad > existencias) {
            throw new IllegalArgumentException("No hay suficientes ejemplares disponibles.");
        }
        existencias -= cantidad;
    }

    public void reponer(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reponer debe ser mayor que cero.");
        }
        existencias += cantidad;
    }

    @Override
    public BigDecimal calcularIva(BigDecimal subtotal) {
        Objects.requireNonNull(subtotal, "El subtotal no puede ser nulo.");
        if (subtotal.signum() < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }
        return BigDecimal.ZERO.setScale(2);
    }
}
