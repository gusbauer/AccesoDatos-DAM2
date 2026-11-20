/*
 * Modelo usado por XStream.
 * toXML() convierte el objeto a XML.
 * fromXML() reconstruye el objeto desde XML.
 */
public class Videojuego {

    private int codigo;
    private String titulo;
    private String plataforma;
    private short anio;
    private float precio;
    private boolean multijugador;
    private float valoracion;
    private short codigoDesarrolladora;
    private String genero;
    private short pegi;
    private int ventas;

    public Videojuego() {
    }

    public Videojuego(
            int codigo, String titulo, String plataforma, short anio,
            float precio, boolean multijugador, float valoracion,
            short codigoDesarrolladora, String genero, short pegi, int ventas) {

        this.codigo = codigo;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.anio = anio;
        this.precio = precio;
        this.multijugador = multijugador;
        this.valoracion = valoracion;
        this.codigoDesarrolladora = codigoDesarrolladora;
        this.genero = genero;
        this.pegi = pegi;
        this.ventas = ventas;
    }

    @Override
    public String toString() {
        return codigo + " - " + titulo
                + " | " + plataforma
                + " | " + precio + " €"
                + " | valoración " + valoracion;
    }
}
