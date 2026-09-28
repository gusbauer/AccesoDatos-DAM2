package ficheros;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FichTextoIA {

    public static void main(String[] args) {

        String rutaLectura = "C:\\clase\\damtexto1.txt";
        String rutaEscritura = "C:\\clase\\dam2_2409.txt";
        String rutaTemporal = "C:\\clase\\temporal.txt";

        // 1. Lectura carácter a carácter
        System.out.println("--- Contenido del fichero leído carácter a carácter ---");

        try (FileReader fr = new FileReader(rutaLectura)) {

            int caracter;

            // read() devuelve el carácter como int.
            // Devuelve -1 cuando llega al final del fichero (EOF).
            while ((caracter = fr.read()) != -1) {
                System.out.print((char) caracter);
            }

            System.out.println();

        } catch (IOException e) {

            System.err.println(
                    "Error al leer el fichero: " + e.getMessage()
            );
        }

        // 2. Crear/escribir en otro fichero
        String cadena =
                "Prueba para escribir una cadena de caracteres en un fichero";

        try (FileWriter fw = new FileWriter(rutaEscritura, true)) {

            fw.write(cadena);
            fw.write(System.lineSeparator());

            System.out.println(
                    "\nFichero '" + rutaEscritura
                    + "' escrito correctamente."
            );

        } catch (IOException e) {

            System.err.println(
                    "Error al escribir el fichero: " + e.getMessage()
            );
        }

        // 3. Crear un fichero temporal a partir del original
        try (
                FileReader fr = new FileReader(rutaLectura);
                FileWriter fw = new FileWriter(rutaTemporal)
        ) {

            int caracter;

            while ((caracter = fr.read()) != -1) {
                fw.write(caracter);
            }

            // Podemos añadir contenido nuevo
            fw.write(System.lineSeparator());
            fw.write("Texto añadido al fichero temporal.");

        } catch (IOException e) {

            System.err.println(
                    "Error al crear el fichero temporal: "
                    + e.getMessage()
            );
        }

        // 4. Reemplazar el fichero original por el temporal
        File archivoOriginal = new File(rutaLectura);
        File archivoTemporal = new File(rutaTemporal);

        // Primero borramos el fichero original
        if (archivoOriginal.delete()) {

            // Después renombramos el temporal con el nombre del original
            if (archivoTemporal.renameTo(archivoOriginal)) {

                System.out.println(
                        "Fichero '" + rutaLectura
                        + "' actualizado y reemplazado correctamente."
                );

            } else {

                System.err.println(
                        "No se pudo renombrar el fichero temporal."
                );
            }

        } else {

            System.err.println(
                    "No se pudo eliminar el fichero original. "
                    + "Comprueba que no esté abierto en otro programa."
            );
        }
    }
}
