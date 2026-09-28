package ficheros;

import java.io.File;
import java.util.Scanner;

public class ListadoDirectorio {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce la ruta del fichero o directorio: ");
        String ruta = teclado.nextLine();

        File fich = new File(ruta);

        if (!fich.exists()) {

            System.out.println(
                "No existe el fichero o directorio (" + ruta + ")."
            );

        } else {

            if (fich.isFile()) {

                System.out.println(ruta + " es un fichero.");
                System.out.println("Nombre: " + fich.getName());
                System.out.println("Tamaño: " + fich.length() + " bytes");

            } else if (fich.isDirectory()) {

                System.out.println(ruta + " es un directorio.");
                System.out.println("Contenido de: " + fich.getName());

                File[] ficheros = fich.listFiles();

                if (ficheros != null && ficheros.length > 0) {

                    for (File f : ficheros) {

                        String tipo;

                        if (f.isDirectory()) {
                            tipo = "[DIR]";
                        } else if (f.isFile()) {
                            tipo = "[FILE]";
                        } else {
                            tipo = "[?]";
                        }

                        System.out.println(tipo + " " + f.getName());
                    }

                } else {
                    System.out.println("El directorio está vacío.");
                }
            }
        }

        teclado.close();
    }
}