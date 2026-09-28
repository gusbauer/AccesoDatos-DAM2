package ficheros;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ComprobadorFicherosDirectorios {

    private static final SimpleDateFormat FORMATO_FECHA =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

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

                imprimirDetalles(archivo);

            // Si es un directorio
            } else if (archivo.isDirectory()) {

                System.out.println("Tipo: Es un directorio.");
                System.out.println("Contenido:");

                File[] contenido = archivo.listFiles();

                if (contenido != null && contenido.length > 0) {

                    for (File elemento : contenido) {

                        System.out.println("\n--------------------");
                        imprimirDetalles(elemento);
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

    private static void imprimirDetalles(File archivo) {

        String tipo =
                archivo.isDirectory() ? "Directorio" : "Fichero";

        String padre =
                archivo.getParent() == null
                        ? "(sin padre)"
                        : archivo.getParent();

        String fecha =
                FORMATO_FECHA.format(
                        new Date(archivo.lastModified())
                );

        System.out.println("Nombre: " + archivo.getName());
        System.out.println("Tipo: " + tipo);
        System.out.println("Directorio padre: " + padre);
        System.out.println("Ruta: " + archivo.getPath());
        System.out.println("Ruta absoluta: " + archivo.getAbsolutePath());
        System.out.println("Fecha de modificación: " + fecha);
        System.out.println("Tamaño: " + archivo.length() + " bytes");
        System.out.println(
                "Oculto: " + (archivo.isHidden() ? "Sí" : "No")
        );

        System.out.println(
                "Permisos: "
                        + (archivo.canRead() ? "lectura " : "")
                        + (archivo.canWrite() ? "escritura " : "")
                        + (archivo.canExecute() ? "ejecución" : "")
        );
    }
}
