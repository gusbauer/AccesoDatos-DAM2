package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class BuscarEmpleadoPorCodigo {

    // TamaÃ±o fijo de cada registro definido (171 bytes)
    private static final int TAMANO_REGISTRO = 171;
    private static final int LONGITUD_CADENA_MAX = 40;

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String rutaFichero = "empleados.dat";

        File fichero = new File(rutaFichero);

        // Validar si el fichero existe
        if (!fichero.exists() || !fichero.isFile()) {
            System.err.println("Error: El fichero '" + rutaFichero + "' no existe.");
            return;
        }

        // Pedir al usuario el cÃ³digo de empleado
        System.out.print("Introduce el cÃ³digo de empleado a buscar (ej. 1, 2, 3...): ");
        int codigoBuscado;

        try {
            codigoBuscado = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: Debes introducir un nÃºmero entero vÃ¡lido.");
            return;
        }

        // Un cÃ³digo menor o igual a 0 no es vÃ¡lido en esta secuencia (empieza en 1)
        if (codigoBuscado < 1) {
            System.out.println("El cÃ³digo introducido no es vÃ¡lido. Los cÃ³digos empiezan desde 1.");
            return;
        }

        // Abrir el fichero en modo solo lectura ("r")
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {

            long tamanoFichero = raf.length();
            
            // CÃLCULO DE LA POSICIÃ“N DIRECTA EN BYTES
            long posicion = (long) (codigoBuscado - 1) * TAMANO_REGISTRO;

            // Si la posiciÃ³n calculada es mayor o igual al tamaÃ±o total del fichero, el empleado no existe
            if (posicion >= tamanoFichero) {
                System.out.println("\nNo se ha encontrado ningÃºn empleado con el cÃ³digo " + codigoBuscado + ".");
                System.out.println("El rango de cÃ³digos registrados va del 1 al " + (tamanoFichero / TAMANO_REGISTRO) + ".");
                return;
            }

            // SALTO DIRECTO A LA POSICIÃ“N DEL REGISTRO
            raf.seek(posicion);

            // 1. Leer CÃ³digo (int - 4 bytes)
            int codigo = raf.readInt();

            // 2. Leer Nombre (40 chars - 80 bytes)
            String nombre = leerCadenaFija(raf, LONGITUD_CADENA_MAX);

            // 3. Leer Correo (40 chars - 80 bytes)
            String correo = leerCadenaFija(raf, LONGITUD_CADENA_MAX);

            // 4. Leer Salario (float - 4 bytes)
            float salario = raf.readFloat();

            // 5. Leer Carnet de conducir (boolean - 1 byte)
            boolean carnetConducir = raf.readBoolean();

            // 6. Leer Departamento (short - 2 bytes)
            short departamento = raf.readShort();

            // Mostrar la informaciÃ³n del empleado
            System.out.println("\n==========================================================================================");
            System.out.println("                               EMPLEADO ENCONTRADO                                        ");
            System.out.println("==========================================================================================");
            System.out.println("  â€¢ CÃ³digo           : " + codigo);
            System.out.println("  â€¢ Nombre           : " + nombre);
            System.out.println("  â€¢ Correo           : " + correo);
            System.out.println("  â€¢ Salario          : " + String.format("%.2f â‚¬", salario));
            System.out.println("  â€¢ Carnet Conducir  : " + (carnetConducir ? "SÃ­" : "No"));
            System.out.println("  â€¢ Departamento     : " + departamento);
            System.out.println("==========================================================================================");

        } catch (IOException e) {
            System.err.println("Error de lectura en el fichero: " + e.getMessage());
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