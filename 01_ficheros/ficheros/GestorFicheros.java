/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ficheros;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GestorFicheros {

    public static void main(String[] args) {
        String rutaLectura = "c:\\clase\\damtexto1.txt";
        String rutaEscritura = "c:\\clase\\damtexto1.txt";

        // 1. Lectura carÃ¡cter a carÃ¡cter y visualizaciÃ³n por salida estÃ¡ndar
        System.out.println("--- Contenido del fichero leido caracter a caracter ---");
        try (FileReader fr = new FileReader(rutaLectura)) {
            int caracter;
            // fr.read() devuelve el valor Unicode del carÃ¡cter como int, o -1 al llegar al final (EOF)
            while ((caracter = fr.read()) != -1) {
                System.out.print((char) caracter);
            }
            System.out.println(); // Salto de lÃ­nea final
        } catch (IOException e) {
            System.err.println("Error al leer el fichero: " + e.getMessage());
        }

        // 2. CreaciÃ³n del fichero y escritura de la cadena de caracteres
        String cadena = "Prueba para escribir una cadena de caracteres en un fichero";
        try (FileWriter fw = new FileWriter(rutaEscritura,true)) {
            fw.write(cadena);
            System.out.println("\nFichero '" + rutaEscritura + "' creado con Ã©xito.");
        } catch (IOException e) {
            System.err.println("Error al escribir el fichero: " + e.getMessage());
        }
    }
}