package ficheros;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EscribirCochesBinario {

    public static void main(String[] args) {

        String rutaFichero = "coches.dat";

        String[] modelos = {
            "Toyota Corolla",
            "Seat Leon",
            "Tesla Model 3",
            "Ford Focus",
            "Hyundai Tucson",
            "BMW Serie 3",
            "Audi A4",
            "Peugeot 3008",
            "Renault Clio",
            "Volkswagen Golf"
        };

        int[] cilindradas = {
            1800, 1500, 0, 1000, 1600,
            2000, 2000, 1200, 1000, 1500
        };

        long[] precios = {
            28500L, 24000L, 41000L, 22500L, 31000L,
            45000L, 43500L, 29500L, 21000L, 27000L
        };

        boolean[] hibridos = {
            true, false, false, false, true,
            true, false, true, false, false
        };

        float[] altos = {
            1.435f, 1.456f, 1.443f, 1.452f, 1.650f,
            1.442f, 1.428f, 1.624f, 1.440f, 1.491f
        };

        float[] largos = {
            4.370f, 4.368f, 4.694f, 4.378f, 4.500f,
            4.709f, 4.762f, 4.447f, 4.050f, 4.284f
        };

        char[] colores = {
            'R', 'A', 'Z', 'V', 'R',
            'Z', 'V', 'A', 'R', 'Z'
        };

        try (DataOutputStream salida =
                     new DataOutputStream(
                             new FileOutputStream(rutaFichero))) {

            System.out.println(
                "Escribiendo los datos de los 10 coches..."
            );

            for (int i = 0; i < modelos.length; i++) {

                salida.writeUTF(modelos[i]);
                salida.writeInt(cilindradas[i]);
                salida.writeLong(precios[i]);
                salida.writeBoolean(hibridos[i]);
                salida.writeFloat(altos[i]);
                salida.writeFloat(largos[i]);
                salida.writeChar(colores[i]);
            }

            System.out.println(
                "Fichero binario creado correctamente."
            );

        } catch (IOException e) {

            System.out.println(
                "Error al escribir el fichero: " + e.getMessage()
            );
        }
    }
}