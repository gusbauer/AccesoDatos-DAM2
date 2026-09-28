package ficheros;

import java.io.*;
import java.util.Scanner;

public class EliminarLineaFichero {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce la ruta del fichero: ");
        String ruta = scanner.nextLine();

        File original = new File(ruta);

        if (!original.exists() || !original.isFile()) {
            System.out.println("El fichero no existe.");
            scanner.close();
            return;
        }

        System.out.print("Introduce el número de línea a eliminar: ");
        int lineaEliminar = scanner.nextInt();

        if (lineaEliminar <= 0) {
            System.out.println("El número de línea debe ser mayor que 0.");
            scanner.close();
            return;
        }

        File temporal =
                new File(original.getParent(),
                        "temporal_" + original.getName());

        int numeroLinea = 1;
        boolean encontrada = false;

        try (
                BufferedReader br =
                        new BufferedReader(new FileReader(original));

                BufferedWriter bw =
                        new BufferedWriter(new FileWriter(temporal))
        ) {

            String linea;

            while ((linea = br.readLine()) != null) {

                if (numeroLinea != lineaEliminar) {

                    bw.write(linea);
                    bw.newLine();

                } else {

                    encontrada = true;
                }

                numeroLinea++;
            }

        } catch (IOException e) {

            System.out.println(
                    "Error procesando el fichero: " + e.getMessage()
            );

            scanner.close();
            return;
        }

        if (!encontrada) {

            System.out.println(
                    "El fichero no tiene la línea "
                    + lineaEliminar + "."
            );

            temporal.delete();
            scanner.close();
            return;
        }

        if (original.delete()) {

            if (temporal.renameTo(original)) {

                System.out.println(
                        "Línea " + lineaEliminar
                        + " eliminada correctamente."
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