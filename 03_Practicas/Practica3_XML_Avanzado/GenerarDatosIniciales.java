import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Genera videojuegos.dat, fichero binario inicial de la práctica 3.
 *
 * COMPILAR:
 * javac GenerarDatosIniciales.java
 *
 * EJECUTAR:
 * java GenerarDatosIniciales
 *
 * Cada registro ocupa exactamente 173 bytes.
 */
public class GenerarDatosIniciales {

    private static final String FICHERO = "videojuegos.dat";
    private static final int LONG_TITULO = 40;
    private static final int LONG_PLATAFORMA = 15;
    private static final int LONG_GENERO = 20;

    public static void main(String[] args) {

        String[] titulos = {
                "Minecraft", "The Witcher 3", "Grand Theft Auto V",
                "Red Dead Redemption 2", "Cyberpunk 2077",
                "God of War", "Elden Ring", "Hades", "Hollow Knight",
                "DOOM Eternal", "Forza Horizon 5", "Resident Evil 4",
                "Baldurs Gate 3", "Spider-Man", "Dark Souls 3"
        };

        String[] plataformas = {
                "PC", "PC", "PS5", "PS5", "PC",
                "PS5", "PC", "PC", "PC", "PC",
                "Xbox", "PS5", "PC", "PS5", "PC"
        };

        short[] anios = {
                2011, 2015, 2013, 2018, 2020,
                2018, 2022, 2020, 2017, 2020,
                2021, 2023, 2023, 2018, 2016
        };

        float[] precios = {
                29.99f, 39.99f, 19.99f, 59.99f, 49.99f,
                39.99f, 59.99f, 24.99f, 14.99f, 39.99f,
                59.99f, 49.99f, 59.99f, 49.99f, 39.99f
        };

        boolean[] multijugador = {
                true, false, true, true, false,
                false, true, false, false, true,
                true, false, true, false, true
        };

        float[] valoraciones = {
                9.5f, 9.7f, 9.4f, 9.8f, 8.8f,
                9.3f, 9.6f, 9.1f, 9.2f, 8.9f,
                9.0f, 9.4f, 9.7f, 9.2f, 9.3f
        };

        short[] desarrolladoras = {
                1, 2, 3, 3, 2,
                4, 5, 6, 6, 6,
                6, 6, 6, 4, 5
        };

        String[] generos = {
                "Sandbox", "RPG", "Accion", "Aventura", "RPG",
                "Aventura", "RPG", "Roguelike", "Metroidvania",
                "Shooter", "Carreras", "Terror", "RPG", "Accion", "RPG"
        };

        short[] pegi = {
                7, 18, 18, 18, 18,
                18, 16, 12, 7, 18,
                3, 18, 18, 16, 16
        };

        int[] ventas = {
                300000000, 50000000, 200000000, 60000000, 30000000,
                25000000, 25000000, 7000000, 5000000, 5000000,
                35000000, 8000000, 15000000, 33000000, 10000000
        };

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "rw")) {
            raf.setLength(0);

            for (int i = 0; i < titulos.length; i++) {
                raf.writeInt(i + 1);
                escribirCadena(raf, titulos[i], LONG_TITULO);
                escribirCadena(raf, plataformas[i], LONG_PLATAFORMA);
                raf.writeShort(anios[i]);
                raf.writeFloat(precios[i]);
                raf.writeBoolean(multijugador[i]);
                raf.writeFloat(valoraciones[i]);
                raf.writeShort(desarrolladoras[i]);
                escribirCadena(raf, generos[i], LONG_GENERO);
                raf.writeShort(pegi[i]);
                raf.writeInt(ventas[i]);
            }

            System.out.println("videojuegos.dat generado.");
            System.out.println("Registros: " + titulos.length);
            System.out.println("Tamaño: " + raf.length() + " bytes");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void escribirCadena(
            RandomAccessFile raf, String texto, int longitud) throws IOException {

        for (int i = 0; i < longitud; i++) {
            raf.writeChar(i < texto.length() ? texto.charAt(i) : ' ');
        }
    }
}
