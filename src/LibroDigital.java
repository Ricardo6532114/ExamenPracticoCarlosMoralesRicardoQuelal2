import java.math.BigDecimal;
import java.util.Objects;

public class LibroDigital extends Producto {
    private final String autor;
    private final String formato;

    public LibroDigital(
            String codigo,
            String nombre,
            BigDecimal precio,
            String autor,
            String formato) {
        super(codigo, nombre, precio);

        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        if (formato == null || formato.isBlank()) {
            throw new IllegalArgumentException("El formato no puede estar vacío.");
        }

        String formatoValidado = formato.trim().toUpperCase();
        if (!formatoValidado.equals("PDF") && !formatoValidado.equals("EPUB")) {
            throw new IllegalArgumentException("El formato debe ser PDF o EPUB.");
        }

        this.autor = autor.trim();
        this.formato = formatoValidado;
    }

    public String getAutor() {
        return autor;
    }

    public String getFormato() {
        return formato;
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