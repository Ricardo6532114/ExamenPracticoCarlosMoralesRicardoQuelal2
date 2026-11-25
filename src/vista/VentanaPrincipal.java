package vista;

import modelo.Cliente;
import modelo.GestorLibreria;
import modelo.LibroDigital;
import modelo.LibroFisico;
import modelo.Papeleria;
import modelo.Producto;
import modelo.Venta;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class VentanaPrincipal extends JFrame {
    private static final String LIBRO_FISICO = "Libro impreso";
    private static final String LIBRO_DIGITAL = "Libro digital";
    private static final String PAPELERIA = "Papelería";

    private final GestorLibreria gestor = new GestorLibreria();
    private final NumberFormat formatoDinero =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-EC"));

    private final DefaultTableModel modeloProductos = new DefaultTableModel(
            new Object[]{"Código", "Tipo", "Nombre", "Precio", "Información", "Existencias"},
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tablaProductos = new JTable(modeloProductos);
    private final TableRowSorter<DefaultTableModel> ordenadorProductos =
            new TableRowSorter<>(modeloProductos);

    private final JComboBox<String> tipoProducto =
            new JComboBox<>(new String[]{LIBRO_FISICO, LIBRO_DIGITAL, PAPELERIA});
    private final JTextField codigoProducto = new JTextField(18);
    private final JTextField nombreProducto = new JTextField(18);
    private final JTextField precioProducto = new JTextField(18);
    private final JTextField autorFisico = new JTextField(18);
    private final JTextField autorDigital = new JTextField(18);
    private final JComboBox<String> formatoDigital =
            new JComboBox<>(new String[]{"PDF", "EPUB"});
    private final JTextField marcaPapeleria = new JTextField(18);
    private final JSpinner existenciasFisico =
            new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
    private final JSpinner existenciasPapeleria =
            new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
    private final JPanel panelDatosEspecificos = new JPanel(new CardLayout());

    private final JComboBox<Cliente> selectorCliente = new JComboBox<>();
    private final JComboBox<Producto> selectorProducto = new JComboBox<>();
    private final JSpinner cantidadVenta =
            new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final DefaultTableModel modeloDetalle = new DefaultTableModel(
            new Object[]{"Código", "Producto", "Cantidad", "Precio", "Subtotal"},
            0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel etiquetaSubtotal = new JLabel();
    private final JLabel etiquetaDescuento = new JLabel();
    private final JLabel etiquetaIva = new JLabel();
    private final JLabel etiquetaTotal = new JLabel();

    private Venta ventaActual;

    public VentanaPrincipal() {
        setTitle("Páginas Andinas - Librería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Productos", crearPanelProductos());
        pestanas.addTab("Ventas", crearPanelVentas());
        add(pestanas);

        cargarProductos();
        cargarClientes();
        actualizarResumen();
    }

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Productos de la librería");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(titulo, BorderLayout.NORTH);

        tablaProductos.setRowSorter(ordenadorProductos);
        tablaProductos.setFillsViewportHeight(true);
        JPanel lista = new JPanel(new BorderLayout(8, 8));
        JTextField busqueda = new JTextField();
        busqueda.setToolTipText("Escriba una parte del nombre para buscar");
        busqueda.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    private void filtrar() {
                        String texto = busqueda.getText().trim();
                        if (texto.isEmpty()) {
                            ordenadorProductos.setRowFilter(null);
                        } else {
                            ordenadorProductos.setRowFilter(
                                    javax.swing.RowFilter.regexFilter(
                                            "(?i)" + java.util.regex.Pattern.quote(texto), 2));
                        }
                    }

                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        filtrar();
                    }
                });
        JPanel barraBusqueda = new JPanel(new BorderLayout(8, 0));
        barraBusqueda.add(new JLabel("Buscar por nombre:"), BorderLayout.WEST);
        barraBusqueda.add(busqueda, BorderLayout.CENTER);
        lista.add(barraBusqueda, BorderLayout.NORTH);
        lista.add(new javax.swing.JScrollPane(tablaProductos), BorderLayout.CENTER);

        JPanel formulario = crearFormularioProducto();
        formulario.setPreferredSize(new Dimension(300, 400));
        panel.add(lista, BorderLayout.CENTER);
        panel.add(formulario, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearFormularioProducto() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Registrar producto"));

        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 6));
        campos.add(campo("Tipo de producto", tipoProducto));
        campos.add(campo("Código", codigoProducto));
        campos.add(campo("Nombre", nombreProducto));
        campos.add(campo("Precio", precioProducto));

        JPanel fisico = new JPanel(new GridLayout(0, 1, 6, 6));
        fisico.add(campo("Autor", autorFisico));
        fisico.add(campo("Existencias", existenciasFisico));

        JPanel digital = new JPanel(new GridLayout(0, 1, 6, 6));
        digital.add(campo("Autor", autorDigital));
        digital.add(campo("Formato", formatoDigital));

        JPanel papeleria = new JPanel(new GridLayout(0, 1, 6, 6));
        papeleria.add(campo("Marca", marcaPapeleria));
        papeleria.add(campo("Existencias", existenciasPapeleria));

        panelDatosEspecificos.add(fisico, LIBRO_FISICO);
        panelDatosEspecificos.add(digital, LIBRO_DIGITAL);
        panelDatosEspecificos.add(papeleria, PAPELERIA);
        tipoProducto.addActionListener(evento -> mostrarCamposEspecificos());

        JButton botonRegistrar = new JButton("Registrar producto");
        botonRegistrar.addActionListener(evento -> registrarProducto());

        campos.add(panelDatosEspecificos);
        panel.add(campos, BorderLayout.CENTER);
        panel.add(botonRegistrar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Registrar una venta");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        panel.add(titulo, BorderLayout.NORTH);

        selectorProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto) {
                    Producto producto = (Producto) value;
                    setText(producto.getCodigo() + " - " + producto.getNombre()
                            + " (" + formatoDinero.format(producto.getPrecio()) + ")");
                }
                return this;
            }
        });
        selectorCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    Cliente cliente = (Cliente) value;
                    setText(cliente.getNombre()
                            + (cliente.esSocio() ? " (socio)" : ""));
                }
                return this;
            }
        });

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controles.add(new JLabel("Cliente:"));
        controles.add(selectorCliente);
        controles.add(new JLabel("Producto:"));
        controles.add(selectorProducto);
        controles.add(new JLabel("Cantidad:"));
        controles.add(cantidadVenta);

        JButton botonAgregar = new JButton("Agregar a la venta");
        botonAgregar.addActionListener(evento -> agregarDetalle());
        controles.add(botonAgregar);
        panel.add(controles, BorderLayout.NORTH);

        JTable tablaDetalles = new JTable(modeloDetalle);
        tablaDetalles.setFillsViewportHeight(true);
        panel.add(new javax.swing.JScrollPane(tablaDetalles), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(8, 8));
        JPanel resumen = new JPanel(new GridLayout(0, 2, 8, 4));
        resumen.add(new JLabel("Subtotal:"));
        resumen.add(etiquetaSubtotal);
        resumen.add(new JLabel("Descuento de socio:"));
        resumen.add(etiquetaDescuento);
        resumen.add(new JLabel("IVA:"));
        resumen.add(etiquetaIva);
        resumen.add(new JLabel("Total:"));
        etiquetaTotal.setFont(etiquetaTotal.getFont().deriveFont(Font.BOLD, 16f));
        resumen.add(etiquetaTotal);
        pie.add(resumen, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botonFinalizar = new JButton("Finalizar venta");
        botonFinalizar.addActionListener(evento -> finalizarVenta());
        JButton botonNueva = new JButton("Nueva venta");
        botonNueva.addActionListener(evento -> nuevaVenta());
        botones.add(botonNueva);
        botones.add(botonFinalizar);
        pie.add(botones, BorderLayout.SOUTH);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel campo(String etiqueta, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(4, 3));
        panel.add(new JLabel(etiqueta), BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    private void mostrarCamposEspecificos() {
        CardLayout tarjetas = (CardLayout) panelDatosEspecificos.getLayout();
        tarjetas.show(panelDatosEspecificos, (String) tipoProducto.getSelectedItem());
    }

    private void registrarProducto() {
        try {
            String tipo = (String) tipoProducto.getSelectedItem();
            String codigo = codigoProducto.getText().trim();
            String nombre = nombreProducto.getText().trim();
            BigDecimal precio = new BigDecimal(precioProducto.getText().trim());
            Producto producto;

            if (LIBRO_FISICO.equals(tipo)) {
                producto = new LibroFisico(
                        codigo, nombre, precio, autorFisico.getText(),
                        (Integer) existenciasFisico.getValue());
            } else if (LIBRO_DIGITAL.equals(tipo)) {
                producto = new LibroDigital(
                        codigo, nombre, precio, autorDigital.getText(),
                        (String) formatoDigital.getSelectedItem());
            } else {
                producto = new Papeleria(
                        codigo, nombre, precio, marcaPapeleria.getText(),
                        (Integer) existenciasPapeleria.getValue());
            }

            gestor.registrarProducto(producto);
            cargarProductos();
            limpiarFormularioProducto();
            JOptionPane.showMessageDialog(
                    this, "Producto registrado correctamente.",
                    "Registro", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    private void limpiarFormularioProducto() {
        codigoProducto.setText("");
        nombreProducto.setText("");
        precioProducto.setText("");
        autorFisico.setText("");
        autorDigital.setText("");
        marcaPapeleria.setText("");
        existenciasFisico.setValue(0);
        existenciasPapeleria.setValue(0);
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        selectorProducto.removeAllItems();

        for (Producto producto : gestor.getProductos()) {
            String tipo;
            String informacion;
            String existencias = "No se agota";

            if (producto instanceof LibroFisico) {
                LibroFisico libro = (LibroFisico) producto;
                tipo = LIBRO_FISICO;
                informacion = libro.getAutor();
                existencias = String.valueOf(libro.getExistencias());
            } else if (producto instanceof LibroDigital) {
                LibroDigital libro = (LibroDigital) producto;
                tipo = LIBRO_DIGITAL;
                informacion = libro.getAutor() + " - " + libro.getFormato();
            } else {
                Papeleria articulo = (Papeleria) producto;
                tipo = PAPELERIA;
                informacion = articulo.getMarca();
                existencias = String.valueOf(articulo.getExistencias());
            }

            modeloProductos.addRow(new Object[]{
                    producto.getCodigo(), tipo, producto.getNombre(),
                    formatoDinero.format(producto.getPrecio()), informacion, existencias
            });
            selectorProducto.addItem(producto);
        }
    }

    private void cargarClientes() {
        selectorCliente.removeAllItems();
        for (Cliente cliente : gestor.getClientes()) {
            selectorCliente.addItem(cliente);
        }
    }

    private void agregarDetalle() {
        Cliente cliente = (Cliente) selectorCliente.getSelectedItem();
        Producto producto = (Producto) selectorProducto.getSelectedItem();
        if (cliente == null || producto == null) {
            mostrarError("Seleccione un cliente y un producto.");
            return;
        }

        try {
            if (ventaActual == null) {
                ventaActual = new Venta(cliente);
            } else if (ventaActual.getCliente() != cliente) {
                mostrarError("Use «Nueva venta» para cambiar de cliente.");
                return;
            }

            ventaActual.agregarProducto(
                    producto, (Integer) cantidadVenta.getValue());
            Venta.DetalleVenta detalle =
                    ventaActual.getDetalles().get(ventaActual.getDetalles().size() - 1);
            modeloDetalle.addRow(new Object[]{
                    producto.getCodigo(), producto.getNombre(),
                    detalle.getCantidad(), formatoDinero.format(producto.getPrecio()),
                    formatoDinero.format(detalle.getSubtotal())
            });
            actualizarResumen();
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    private void finalizarVenta() {
        if (ventaActual == null) {
            mostrarError("Agregue al menos un producto antes de finalizar.");
            return;
        }

        try {
            ventaActual.finalizar();
            actualizarResumen();
            cargarProductos();

            String mensaje = "Subtotal: " + formatoDinero.format(ventaActual.getSubtotal())
                    + "\nDescuento: " + formatoDinero.format(ventaActual.getDescuento())
                    + "\nIVA: " + formatoDinero.format(ventaActual.getIva())
                    + "\nTotal a pagar: " + formatoDinero.format(ventaActual.getTotal());
            JOptionPane.showMessageDialog(
                    this, mensaje, "Venta finalizada",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    private void nuevaVenta() {
        ventaActual = null;
        modeloDetalle.setRowCount(0);
        cantidadVenta.setValue(1);
        actualizarResumen();
    }

    private void actualizarResumen() {
        BigDecimal subtotal = ventaActual == null
                ? BigDecimal.ZERO : ventaActual.getSubtotal();
        BigDecimal descuento = ventaActual == null
                ? BigDecimal.ZERO : ventaActual.getDescuento();
        BigDecimal iva = ventaActual == null
                ? BigDecimal.ZERO : ventaActual.getIva();
        BigDecimal total = ventaActual == null
                ? BigDecimal.ZERO : ventaActual.getTotal();

        etiquetaSubtotal.setText(formatoDinero.format(subtotal));
        etiquetaDescuento.setText(formatoDinero.format(descuento));
        etiquetaIva.setText(formatoDinero.format(iva));
        etiquetaTotal.setText(formatoDinero.format(total));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje, "No se pudo completar",
                JOptionPane.ERROR_MESSAGE);
    }
}
