package ficheros;

import java.io.File;
import java.util.Scanner;

public class ComprobadorFicheroMSI {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // 1. Pedir la ruta o nombre del fichero/directorio
        System.out.print("Introduce la ruta o nombre del fichero/directorio: ");
        String ruta = scanner.nextLine();

        // 2. Crear el objeto File
        File archivo = new File(ruta);

        // 3. Comprobar si existe
        if (archivo.exists()) {

            System.out.println("\nEl elemento existe.");

            // Si es un fichero
            if (archivo.isFile()) {

                System.out.println("Tipo: Es un fichero.");
                System.out.println("Nombre: " + archivo.getName());
                System.out.println("Tamaño: " + archivo.length() + " bytes.");

            // Si es un directorio
            } else if (archivo.isDirectory()) {

                System.out.println("Tipo: Es un directorio.");
                System.out.println("Contenido:");

                // Obtener contenido del directorio
                File[] contenido = archivo.listFiles();

                if (contenido != null && contenido.length > 0) {

                    for (File elemento : contenido) {

                        // [D] = directorio
                        // [F] = fichero
                        String tipo = elemento.isDirectory() ? "[D]" : "[F]";

                        System.out.println(
                                "- " + tipo + " " + elemento.getName()
                        );
                    }

                } else {

                    System.out.println("El directorio está vacío.");
                }
            }

        } else {

            System.out.println(
                    "\nEl fichero o directorio especificado no existe."
            );
        }

        scanner.close();
    }
}