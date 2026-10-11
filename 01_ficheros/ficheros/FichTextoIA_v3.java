/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ficheros;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FichTextoIA_v3 {

    public static void main(String[] args) {
        String rutaLectura = "c:\\clase\\damtexto1.txt";
        String rutaTemporal = "c:\\clase\\damtexto1_temp.txt";

        File ficheroOriginal = new File(rutaLectura);
        File ficheroTemporal = new File(rutaTemporal);

        // 1. Pedimos al usuario el carÃ¡cter a buscar
        char caracterABuscar = ' ';
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Introduce la letra minÃºscula a cambiar (a-z): ");
            String entrada = scanner.nextLine();

            if (!entrada.isEmpty()) {
                caracterABuscar = entrada.charAt(0);
            }
        }

        // Validamos que el carÃ¡cter estÃ© entre 'a' y 'z'
        if (caracterABuscar < 'a' || caracterABuscar > 'z') {
            System.err.println("Error: Debes introducir una letra minÃºscula entre la 'a' y la 'z'.");
            return;
        }

        char caracterMayuscula = Character.toUpperCase(caracterABuscar);

        System.out.println("--- Cambiando '" + caracterABuscar + "' por '" + caracterMayuscula + "' ---");

        // 2. Leemos del original y escribimos en el temporal
        try (FileReader fr = new FileReader(ficheroOriginal);
             FileWriter fw = new FileWriter(ficheroTemporal)) {

            int caracter;

            while ((caracter = fr.read()) != -1) {
                char c = (char) caracter;

                if (c != caracterABuscar) {
                    fw.write(c);
                }

                
            }

        } catch (IOException e) {
            System.err.println("Error durante el procesamiento: " + e.getMessage());
            return;
        }

        // 3. Reemplazamos el fichero original por el temporal
        if (ficheroOriginal.delete()) {
            if (ficheroTemporal.renameTo(ficheroOriginal)) {
                System.out.println("Fichero original '" + rutaLectura + "' eliminada la letra con Ã©xito.");
            } else {
                System.err.println("Error al renombrar el fichero temporal.");
            }
        } else {
            System.err.println("Error al borrar el fichero original.");
        }
    }
}