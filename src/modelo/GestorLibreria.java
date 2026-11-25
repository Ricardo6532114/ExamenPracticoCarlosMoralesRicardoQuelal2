package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorLibreria {
    private final List<Producto> productos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();

    public GestorLibreria() {
        precargarDatos();
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(new ArrayList<>(productos));
    }

    public List<Cliente> getClientes() {
        return Collections.unmodifiableList(new ArrayList<>(clientes));
    }

    public void registrarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        for (Producto existente : productos) {
            if (existente.getCodigo().equalsIgnoreCase(producto.getCodigo())) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con el código " + producto.getCodigo() + ".");
            }
        }
        productos.add(producto);
    }

    public List<Producto> buscarPorNombre(String texto) {
        String consulta = texto == null ? "" : texto.trim().toLowerCase();
        List<Producto> encontrados = new ArrayList<>();

        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase().contains(consulta)) {
                encontrados.add(producto);
            }
        }
        return Collections.unmodifiableList(encontrados);
    }

    private void precargarDatos() {
        productos.add(new LibroFisico(
                "LF001", "Cien años de soledad", new BigDecimal("18.50"),
                "Gabriel García Márquez", 12));
        productos.add(new LibroFisico(
                "LF002", "La casa de los espíritus", new BigDecimal("16.00"),
                "Isabel Allende", 8));
        productos.add(new LibroDigital(
                "LD001", "El principito", new BigDecimal("7.50"),
                "Antoine de Saint-Exupéry", "EPUB"));
        productos.add(new LibroDigital(
                "LD002", "Don Quijote de la Mancha", new BigDecimal("9.00"),
                "Miguel de Cervantes", "PDF"));
        productos.add(new Papeleria(
                "PA001", "Cuaderno universitario", new BigDecimal("2.50"),
                "Norma", 30));
        productos.add(new Papeleria(
                "PA002", "Esfero azul", new BigDecimal("0.75"),
                "Bic", 50));

        clientes.add(new Cliente("0100000001", "Ana Socia", true));
        clientes.add(new Cliente("0100000002", "Luis Cliente", false));
    }
}
