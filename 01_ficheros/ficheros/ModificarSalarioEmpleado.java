package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class ModificarSalarioEmpleado {

    // TamaÃ±o fijo de cada registro definido (171 bytes)
    private static final int TAMANO_REGISTRO = 171;
    private static final int LONGITUD_CADENA_MAX = 40;

    // Desplazamiento en bytes hasta el campo Salario:
    // CÃ³digo (4 bytes) + Nombre (80 bytes) + Correo (80 bytes) = 164 bytes
    private static final int DESPLAZAMIENTO_SALARIO = 164;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String rutaFichero = "empleados.dat";

        File fichero = new File(rutaFichero);

        // Validar si el fichero existe
        if (!fichero.exists() || !fichero.isFile()) {
            System.err.println("Error: El fichero '" + rutaFichero + "' no existe.");
            return;
        }

        // 1. Pedir el cÃ³digo de empleado
        System.out.print("Introduce el cÃ³digo del empleado a modificar (ej. 1, 2, 3...): ");
        int codigoBuscado;

        try {
            codigoBuscado = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: Debes introducir un nÃºmero entero vÃ¡lido.");
            return;
        }

        if (codigoBuscado < 1) {
            System.out.println("El cÃ³digo introducido no es vÃ¡lido. Los cÃ³digos empiezan desde 1.");
            return;
        }

        // 2. Abrir el fichero en modo lectura y escritura ("rw")
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {

            long tamanoFichero = raf.length();
            
            // CÃLCULO DIRECTO DE LA POSICIÃ“N DEL REGISTRO
            long posicionRegistro = (long) (codigoBuscado - 1) * TAMANO_REGISTRO;

            // Verificar si el cÃ³digo solicitado estÃ¡ dentro del rango del fichero
            if (posicionRegistro >= tamanoFichero) {
                System.out.println("\nNo se ha encontrado ningÃºn empleado con el cÃ³digo " + codigoBuscado + ".");
                System.out.println("El rango de cÃ³digos registrados va del 1 al " + (tamanoFichero / TAMANO_REGISTRO) + ".");
                return;
            }

            // SALTO DIRECTO AL REGISTRO DEL EMPLEADO
            raf.seek(posicionRegistro);

            // Leer los datos actuales para mostrarlos
            int codigo = raf.readInt();
            String nombre = leerCadenaFija(raf, LONGITUD_CADENA_MAX);
            String correo = leerCadenaFija(raf, LONGITUD_CADENA_MAX);
            float salarioActual = raf.readFloat();
            boolean carnetConducir = raf.readBoolean();
            short departamento = raf.readShort();

            // Mostrar los datos en pantalla
            System.out.println("\n==========================================================================================");
            System.out.println("                              DATOS ACTUALES DEL EMPLEADO                                 ");
            System.out.println("==========================================================================================");
            System.out.println("  â€¢ CÃ³digo           : " + codigo);
            System.out.println("  â€¢ Nombre           : " + nombre);
            System.out.println("  â€¢ Correo           : " + correo);
            System.out.println("  â€¢ Salario Actual   : " + String.format("%.2f â‚¬", salarioActual));
            System.out.println("  â€¢ Carnet Conducir  : " + (carnetConducir ? "SÃ­" : "No"));
            System.out.println("  â€¢ Departamento     : " + departamento);
            System.out.println("==========================================================================================");

            // 3. Pedir el nuevo salario
            System.out.print("\nIntroduce el nuevo salario para el empleado: ");
            float nuevoSalario;

            try {
                nuevoSalario = Float.parseFloat(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.err.println("Error: Salario no vÃ¡lido. OperaciÃ³n cancelada.");
                return;
            }

            // 4. Preguntar confirmaciÃ³n de modificaciÃ³n
            System.out.print("Â¿Deseas confirmar la modificaciÃ³n del salario de " 
                    + String.format("%.2f â‚¬", salarioActual) + " a " 
                    + String.format("%.2f â‚¬", nuevoSalario) + "? (S/N): ");
            String respuesta = scanner.nextLine().trim();

            if (respuesta.equalsIgnoreCase("S")) {
                // SALTO DIRECTO AL CAMPO SALARIO (inicioRegistro + 164 bytes)
                raf.seek(posicionRegistro + DESPLAZAMIENTO_SALARIO);

                // Sobreescribir Ãºnicamente los 4 bytes del salario (float)
                raf.writeFloat(nuevoSalario);

                System.out.println("\nÂ¡Ã‰xito! El salario ha sido modificado correctamente en el fichero.");
            } else {
                System.out.println("\nOperaciÃ³n cancelada. No se han realizado cambios.");
            }

        } catch (IOException e) {
            System.err.println("Error de lectura/escritura en el fichero: " + e.getMessage());
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