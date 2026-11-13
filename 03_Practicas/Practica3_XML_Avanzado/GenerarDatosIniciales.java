import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * Programa auxiliar para crear el fichero binario
 * videojuegos.dat utilizado como punto de partida
 * de la Práctica Nº3.
 *
 * Cada registro ocupa exactamente 173 bytes.
 */
public class GenerarDatosIniciales {

    private static final String FICHERO = "videojuegos.dat";

    private static final int LONGITUD_TITULO = 40;
    private static final int LONGITUD_PLATAFORMA = 15;
    private static final int LONGITUD_GENERO = 20;

    private static final int TAMANO_REGISTRO = 173;

    public static void main(String[] args) {

        String[] titulos = {
                "Minecraft",
                "The Witcher 3",
                "Grand Theft Auto V",
                "Red Dead Redemption 2",
                "Cyberpunk 2077",
                "God of War",
                "Elden Ring",
                "Hades",
                "Hollow Knight",
                "DOOM Eternal",
                "Forza Horizon 5",
                "Resident Evil 4",
                "Baldurs Gate 3",
                "Spider-Man",
                "Dark Souls 3"
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

        /*
         * Códigos de las desarrolladoras:
         *
         * 1 = Mojang
         * 2 = CD Projekt
         * 3 = Rockstar Games
         * 4 = Sony Studios
         * 5 = FromSoftware
         * 6 = Otras
         */
        short[] desarrolladoras = {
                1, 2, 3, 3, 2,
                4, 5, 6, 6, 6,
                6, 6, 6, 4, 5
        };

        String[] generos = {
                "Sandbox",
                "RPG",
                "Accion",
                "Aventura",
                "RPG",
                "Aventura",
                "RPG",
                "Roguelike",
                "Metroidvania",
                "Shooter",
                "Carreras",
                "Terror",
                "RPG",
                "Accion",
                "RPG"
        };

        short[] pegi = {
                7, 18, 18, 18, 18,
                18, 16, 12, 7, 18,
                3, 18, 18, 16, 16
        };

        int[] ventas = {
                300000000,
                50000000,
                200000000,
                60000000,
                30000000,
                25000000,
                25000000,
                7000000,
                5000000,
                5000000,
                35000000,
                8000000,
                15000000,
                33000000,
                10000000
        };

        try (RandomAccessFile raf =
                     new RandomAccessFile(FICHERO, "rw")) {

            /*
             * Ponemos el fichero a longitud 0.
             * Así empezamos siempre desde un fichero limpio.
             */
            raf.setLength(0);

            for (int i = 0; i < titulos.length; i++) {

                int codigo = i + 1;

                raf.writeInt(codigo);

                escribirCadenaFija(
                        raf,
                        titulos[i],
                        LONGITUD_TITULO
                );

                escribirCadenaFija(
                        raf,
                        plataformas[i],
                        LONGITUD_PLATAFORMA
                );

                raf.writeShort(anios[i]);

                raf.writeFloat(precios[i]);

                raf.writeBoolean(multijugador[i]);

                raf.writeFloat(valoraciones[i]);

                raf.writeShort(desarrolladoras[i]);

                escribirCadenaFija(
                        raf,
                        generos[i],
                        LONGITUD_GENERO
                );

                raf.writeShort(pegi[i]);

                raf.writeInt(ventas[i]);
            }

            System.out.println(
                    "Fichero videojuegos.dat creado correctamente."
            );

            System.out.println(
                    "Registros creados: "
                            + titulos.length
            );

            System.out.println(
                    "Tamaño del fichero: "
                            + raf.length()
                            + " bytes"
            );

            System.out.println(
                    "Tamaño esperado: "
                            + (titulos.length * TAMANO_REGISTRO)
                            + " bytes"
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al crear el fichero: "
                            + e.getMessage()
            );
        }
    }

    /*
     * Escribe una cadena con una longitud fija.
     * writeChars utiliza 2 bytes por carácter.
     */
    private static void escribirCadenaFija(
            RandomAccessFile raf,
            String texto,
            int longitud) throws IOException {

        StringBuffer buffer =
                new StringBuffer(
                        texto != null ? texto : ""
                );

        buffer.setLength(longitud);

        raf.writeChars(buffer.toString());
    }
}