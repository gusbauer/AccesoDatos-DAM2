package ficheros;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class LeerFicheroFileReader {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce la ruta del fichero: ");
        String ruta = scanner.nextLine();

        File fichero = new File(ruta);

        if (!fichero.exists()) {

            System.out.println("El fichero no existe.");

        } else if (!fichero.isFile()) {

            System.out.println("La ruta indicada no corresponde a un fichero.");

        } else {

            try {

                FileReader lector = new FileReader(fichero);

                int caracter;

                System.out.println("\nContenido del fichero:");
                System.out.println("----------------------");

                while ((caracter = lector.read()) != -1) {

                    System.out.print((char) caracter);
                }

                lector.close();

            } catch (IOException e) {

                System.out.println(
                    "Error al leer el fichero: " + e.getMessage()
                );
            }
        }

        scanner.close();
    }

}
