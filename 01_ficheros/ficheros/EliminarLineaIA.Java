/*
 
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author antonio
 */
package ficheros;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EliminarLineaIA {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Pedir el nombre/ruta del fichero
        System.out.print("Introduce la ruta o nombre del fichero de texto: ");
        String rutaFichero = scanner.nextLine();

        File ficheroOriginal = new File(rutaFichero);

        // ComprobaciÃ³n de existencia del fichero
        if (!ficheroOriginal.exists() || !ficheroOriginal.isFile()) {
            System.out.println("Fichero no encontrado");
            return;
        }

        // 2. Pedir el nÃºmero de lÃ­nea a borrar
        System.out.print("Introduce el nÃºmero de lÃ­nea que quieres borrar: ");
        int lineaABorrar;
        try {
            lineaABorrar = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("El nÃºmero de lÃ­nea debe ser un valor entero vÃ¡lido.");
            return;
        }

        if (lineaABorrar <= 0) {
            System.out.println("El nÃºmero de lÃ­nea debe ser mayor que 0.");
            return;
        }

        // Fichero temporal para almacenar el resultado sin la lÃ­nea borrada
        File ficheroTemporal = new File(ficheroOriginal.getAbsolutePath() + ".tmp");

        int lineaActual = 0;
        boolean lineaEncontrada = false;

        // 3. Lectura y escritura optimizada con BufferedReader y BufferedWriter
        try (BufferedReader br = new BufferedReader(new FileReader(ficheroOriginal));
             BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroTemporal))) {

            String linea;
            boolean primeraLineaEscrita = false;

            while ((linea = br.readLine()) != null) {
                lineaActual++;

                // Si es la lÃ­nea a borrar, la ignoramos (no la escribimos)
                if (lineaActual == lineaABorrar) {
                    lineaEncontrada = true;
                    continue;
                }

                // AÃ±adimos salto de lÃ­nea antes de escribir la lÃ­nea (excepto en la primera)
                if (primeraLineaEscrita) {
                    bw.newLine();
                }
                bw.write(linea);
                primeraLineaEscrita = true;
            }

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura: " + e.getMessage());
            // Si ocurre un error, aseguramos borrar el fichero temporal creado
            if (ficheroTemporal.exists()) {
                ficheroTemporal.delete();
            }
            return;
        }

        // 4. Salida segÃºn si la lÃ­nea existÃ­a o superaba el total de lÃ­neas
        if (!lineaEncontrada) {
            System.out.println("El fichero no tiene tantas lÃ­neas");
            ficheroTemporal.delete(); // Eliminamos el fichero temporal innecesario
        } else {
            // Reemplazamos el fichero original por el fichero modificado
            if (ficheroOriginal.delete() && ficheroTemporal.renameTo(ficheroOriginal)) {
                System.out.println("eliminada la linea del fichero");
            } else {
                System.err.println("Error al actualizar el fichero original.");
            }
        }
    }
}