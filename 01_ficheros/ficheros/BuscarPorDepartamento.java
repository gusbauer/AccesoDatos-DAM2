package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class BuscarPorDepartamento {

    // TamaÃ±o fijo de cada registro (171 bytes)[cite: 1]
    private static final int TAMANO_REGISTRO = 171;
    private static final int LONGITUD_CADENA_MAX = 40;

    // Desplazamiento en bytes hasta el campo departamento (169 bytes)[cite: 1]
    private static final int DESPLAZAMIENTO_DEPARTAMENTO = 169;

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

        // 1. Pedir el cÃ³digo del departamento
        System.out.print("Introduce el cÃ³digo del departamento a buscar (10, 20, 30, 40, 50): ");
        short deptoBuscado;

        try {
            deptoBuscado = Short.parseShort(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: Debes introducir un nÃºmero vÃ¡lido para el departamento.");
            return;
        }

        // 2. Abrir el fichero en modo solo lectura ("r")[cite: 1]
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {

            long tamanoFichero = raf.length();
            long totalRegistros = tamanoFichero / TAMANO_REGISTRO;

            if (totalRegistros == 0) {
                System.out.println("El fichero estÃ¡ vacÃ­o.");
                return;
            }

            System.out.println("\n==========================================================================================");
            System.out.println("                     EMPLEADOS PERTENECIENTES AL DEPARTAMENTO " + deptoBuscado);
            System.out.println("==========================================================================================");

            int contadorCoincidencias = 0;

            // Recorrer los registros comprobando el departamento[cite: 1]
            for (int i = 0; i < totalRegistros; i++) {
                long inicioRegistro = (long) i * TAMANO_REGISTRO;

                // Salto directo al campo departamento del registro 'i'[cite: 1]
                raf.seek(inicioRegistro + DESPLAZAMIENTO_DEPARTAMENTO);
                short deptoActual = raf.readShort();

                // Si coincide el departamento, comprobamos si no estÃ¡ borrado lÃ³gicamente[cite: 1]
                if (deptoActual == deptoBuscado) {
                    raf.seek(inicioRegistro); // Rebobinar al inicio del registro[cite: 1]

                    int codigo = raf.readInt();

                    // Si el cÃ³digo es -1, ignoramos este empleado borrado[cite: 1]
                    if (codigo == -1) {
                        continue;
                    }

                    // Leer el resto de campos del empleado
                    String nombre = leerCadenaFija(raf, LONGITUD_CADENA_MAX);
                    String correo = leerCadenaFija(raf, LONGITUD_CADENA_MAX);
                    float salario = raf.readFloat();
                    boolean carnetConducir = raf.readBoolean();
                    @SuppressWarnings("unused")
                    short departamento = raf.readShort(); // Consumir el campo departamento

                    contadorCoincidencias++;

                    // Mostrar los datos del empleado en formato de tabla
                    System.out.printf("Reg #%-2d | CÃ³d: %-4d | Nombre: %-18s | Correo: %-22s | Salario: %7.2f â‚¬ | Carnet: %-2s%n",
                            (i + 1),
                            codigo,
                            nombre,
                            correo,
                            salario,
                            carnetConducir ? "SÃ­" : "No");
                }
            }

            System.out.println("==========================================================================================");
            System.out.println("Total de empleados activos en el departamento " + deptoBuscado + ": " + contadorCoincidencias);

        } catch (IOException e) {
            System.err.println("Error de lectura en el fichero: " + e.getMessage());
        }
    }

    /**
     * Auxiliar para leer una cadena de longitud fija de caracteres Unicode (2 bytes cada uno)[cite: 1].
     */
    private static String leerCadenaFija(RandomAccessFile raf, int longitud) throws IOException {
        char[] caracteres = new char[longitud];
        for (int i = 0; i < longitud; i++) {
            caracteres[i] = raf.readChar();
        }
        return new String(caracteres).trim();
    }
}