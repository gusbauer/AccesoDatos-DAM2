package ficheros;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ModificarCaracterFichero {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce la ruta del fichero: ");
        String ruta = scanner.nextLine();

        File ficheroOriginal = new File(ruta);

        if (!ficheroOriginal.exists() || !ficheroOriginal.isFile()) {
            System.out.println("El fichero indicado no existe.");
            scanner.close();
            return;
        }

        System.out.print("Introduce una letra minúscula a convertir en mayúscula: ");
        String entrada = scanner.nextLine();

        if (entrada.length() != 1) {
            System.out.println("Debes introducir solamente un carácter.");
            scanner.close();
            return;
        }

        char caracterBuscar = entrada.charAt(0);

        if (!Character.isLowerCase(caracterBuscar)) {
            System.out.println("Debes introducir una letra minúscula.");
            scanner.close();
            return;
        }

        char caracterMayuscula =
                Character.toUpperCase(caracterBuscar);

        File ficheroTemporal =
                new File(ficheroOriginal.getParent(),
                        "temporal_" + ficheroOriginal.getName());

        try (
                FileReader fr = new FileReader(ficheroOriginal);
                FileWriter fw = new FileWriter(ficheroTemporal)
        ) {

            int caracter;

            while ((caracter = fr.read()) != -1) {

                char actual = (char) caracter;

                if (actual == caracterBuscar) {
                    actual = caracterMayuscula;
                }

                fw.write(actual);
            }

        } catch (IOException e) {

            System.out.println(
                    "Error procesando el fichero: " + e.getMessage()
            );

            scanner.close();
            return;
        }

        if (ficheroOriginal.delete()) {

            if (ficheroTemporal.renameTo(ficheroOriginal)) {

                System.out.println(
                        "Fichero modificado correctamente."
                );

            } else {

                System.out.println(
                        "No se pudo renombrar el fichero temporal."
                );
            }

        } else {

            System.out.println(
                    "No se pudo eliminar el fichero original."
            );
        }

        scanner.close();
    }
}