
public class Cliente {
    private final String cedula;
    private final String nombre;
    private final boolean socio;

    public Cliente(String cedula, String nombre, boolean socio) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        this.cedula = cedula.trim();
        this.nombre = nombre.trim();
        this.socio = socio;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isSocio() {
        return socio;
    }
}