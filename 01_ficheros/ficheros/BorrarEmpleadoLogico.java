package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class BorrarEmpleadoLogico {

    // TamaÃ±o fijo de cada registro definido (171 bytes)
    private static final int TAMANO_REGISTRO = 171;
    private static final int LONGITUD_CADENA_MAX = 40;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String rutaFichero = "empleados.dat";

        File fichero = new File(rutaFichero);

        // Validar existencia del fichero
        if (!fichero.exists() || !fichero.isFile()) {
            System.err.println("Error: El fichero '" + rutaFichero + "' no existe.");
            return;
        }

        // 1. Pedir el cÃ³digo del empleado a borrar
        System.out.print("Introduce el cÃ³digo del empleado a borrar (ej. 1, 2, 3...): ");
        int codigoBuscado;

        try {
            codigoBuscado = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: Debes introducir un nÃºmero entero vÃ¡lido.");
            return;
        }

        if (codigoBuscado < 1) {
            System.out.println("El cÃ³digo introducido no es vÃ¡lido. Los cÃ³digos deben ser mayores o iguales a 1.");
            return;
        }

        // 2. Abrir el fichero en modo lectura y escritura ("rw")
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {

            long tamanoFichero = raf.length();
            
            // CÃLCULO DIRECTO DE LA POSICIÃ“N DEL REGISTRO
            long posicionRegistro = (long) (codigoBuscado - 1) * TAMANO_REGISTRO;

            // Comprobar si la posiciÃ³n estÃ¡ dentro del lÃ­mite del fichero
            if (posicionRegistro >= tamanoFichero) {
                System.out.println("\nNo se ha encontrado ningÃºn empleado con el cÃ³digo " + codigoBuscado + ".");
                System.out.println("El rango de cÃ³digos registrados va del 1 al " + (tamanoFichero / TAMANO_REGISTRO) + ".");
                return;
            }

            // SALTO DIRECTO AL INICIO DEL REGISTRO
            raf.seek(posicionRegistro);

            // Leer el cÃ³digo actual
            int codigoActual = raf.readInt();

            // Comprobar si ya estaba borrado previamente
            if (codigoActual == -1) {
                System.out.println("\nEl empleado con cÃ³digo " + codigoBuscado + " ya fue eliminado anteriormente.");
                return;
            }

            // Leer los campos restantes para mostrÃ¡rselos al usuario
            String nombre = leerCadenaFija(raf, LONGITUD_CADENA_MAX);
            String correo = leerCadenaFija(raf, LONGITUD_CADENA_MAX);
            float salario = raf.readFloat();
            boolean carnetConducir = raf.readBoolean();
            short departamento = raf.readShort();

            // Mostrar los datos en pantalla
            System.out.println("\n==========================================================================================");
            System.out.println("                              DATOS DEL EMPLEADO A ELIMINAR");
            System.out.println("==========================================================================================");
            System.out.println("  â€¢ CÃ³digo           : " + codigoActual);
            System.out.println("  â€¢ Nombre           : " + nombre);
            System.out.println("  â€¢ Correo           : " + correo);
            System.out.println("  â€¢ Salario          : " + String.format("%.2f â‚¬", salario));
            System.out.println("  â€¢ Carnet Conducir  : " + (carnetConducir ? "SÃ­" : "No"));
            System.out.println("  â€¢ Departamento     : " + departamento);
            System.out.println("==========================================================================================");

            // 3. ConfirmaciÃ³n del usuario
            System.out.print("\nÂ¿EstÃ¡s seguro de que deseas eliminar este empleado? (S/N): ");
            String respuesta = scanner.nextLine().trim();

            if (respuesta.equalsIgnoreCase("S")) {
                // VOLVER AL INICIO DEL REGISTRO PARA SOBREESCRIBIR EL CÃ“DIGO
                raf.seek(posicionRegistro);

                // Escribir -1 en los primeros 4 bytes (marcado de borrado lÃ³gico)
                raf.writeInt(-1);

                System.out.println("\nÂ¡Empleado eliminado con Ã©xito! (CÃ³digo actualizado a -1).");
            } else {
                System.out.println("\nOperaciÃ³n cancelada. No se han realizado cambios.");
            }

        } catch (IOException e) {
            System.err.println("Error durante la operaciÃ³n en el fichero: " + e.getMessage());
        }
    }

    /**
     * Auxiliar para leer una cadena de longitud fija de caracteres Unicode (2 bytes cada uno).
     */
    private static String leerCadenaFija(RandomAccessFile raf, int longitud) throws IOException {
        char[] caracteres = new char[longitud];
        for (int i = 0; i < longitud; i++) {
            caracteres[i] = raf.readChar();
        }
        return new String(caracteres).trim();
    }
}