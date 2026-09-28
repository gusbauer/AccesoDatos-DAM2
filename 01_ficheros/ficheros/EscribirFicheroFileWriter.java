package ficheros;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EscribirFicheroFileWriter {

    public static void main(String[] args) {

        File ficheroSalida = new File("Prueba.txt");

        String cadena = "Esto es una prueba del FileWriter";

        System.out.println(cadena);

        try {

            // Crear flujo de escritura
            FileWriter escritor = new FileWriter(ficheroSalida);

            // Convertir String a array de caracteres
            char[] caracteres = cadena.toCharArray();

            // Escribir carácter a carácter
            for (int i = 0; i < caracteres.length; i++) {
                escritor.write(caracteres[i]);
            }

            // Añadir un carácter al final
            escritor.append('*');

            // Escribir nuevamente todo el array
            escritor.write(caracteres);

            // Cerrar el flujo
            escritor.close();

            System.out.println("Fichero escrito correctamente.");

        } catch (IOException e) {

            System.out.println(
                "Error al escribir el fichero: " + e.getMessage()
            );
        }
    }
}
