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

    /*
     * Constructor vacío.
     *
     * Es útil para trabajar posteriormente con XStream
     * y para poder crear objetos antes de asignar sus datos.
     */
    public Videojuego() {
    }

    /*
     * Constructor completo.
     */
    public Videojuego(
            int codigo,
            String titulo,
            String plataforma,
            short anio,
            float precio,
            boolean multijugador,
            float valoracion,
            short codigoDesarrolladora,
            String genero,
            short pegi,
            int ventas) {

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

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public short getAnio() {
        return anio;
    }

    public void setAnio(short anio) {
        this.anio = anio;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public boolean isMultijugador() {
        return multijugador;
    }

    public void setMultijugador(boolean multijugador) {
        this.multijugador = multijugador;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public short getCodigoDesarrolladora() {
        return codigoDesarrolladora;
    }

    public void setCodigoDesarrolladora(short codigoDesarrolladora) {
        this.codigoDesarrolladora = codigoDesarrolladora;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public short getPegi() {
        return pegi;
    }

    public void setPegi(short pegi) {
        this.pegi = pegi;
    }

    public int getVentas() {
        return ventas;
    }

    public void setVentas(int ventas) {
        this.ventas = ventas;
    }

    /*
     * toString() permite mostrar fácilmente todos los datos
     * del objeto, especialmente después de deserializarlo
     * mediante XStream.
     */
    @Override
    public String toString() {

        return "Videojuego{" +
                "codigo=" + codigo +
                ", titulo='" + titulo + '\'' +
                ", plataforma='" + plataforma + '\'' +
                ", anio=" + anio +
                ", precio=" + precio +
                ", multijugador=" + multijugador +
                ", valoracion=" + valoracion +
                ", codigoDesarrolladora=" + codigoDesarrolladora +
                ", genero='" + genero + '\'' +
                ", pegi=" + pegi +
                ", ventas=" + ventas +
                '}';
    }
}