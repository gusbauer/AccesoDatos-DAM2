package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class AnadirEmpleadoFinal {

    // TamaÃ±o fijo de cada registro definido (171 bytes)[cite: 1]
    private static final int TAMANO_REGISTRO = 171;
    private static final int LONGITUD_CADENA_MAX = 40;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String rutaFichero = "empleados.dat";

        File fichero = new File(rutaFichero);

        // Validar si el fichero existe
        if (!fichero.exists() || !fichero.isFile()) {
            System.err.println("Error: El fichero '" + rutaFichero + "' no existe.");
            return;
        }

        // Abrir el fichero en modo lectura/escritura ("rw")[cite: 1]
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {

            long tamanoOriginal = raf.length();

            // Calcular el nuevo cÃ³digo secuencial autoincremental
            long totalRegistros = tamanoOriginal / TAMANO_REGISTRO;
            int nuevoCodigo = (int) totalRegistros + 1;

            System.out.println("==========================================================================================");
            System.out.println("                              AÃ‘ADIR NUEVO EMPLEADO");
            System.out.println("==========================================================================================");
            System.out.println("  â€¢ CÃ³digo asignado automÃ¡ticamente : " + nuevoCodigo);

            // 1. Lectura del Nombre (mÃ¡ximo 40 caracteres)
            System.out.print("  â€¢ Nombre (mÃ¡x 40 caracteres)       : ");
            String nombre = scanner.nextLine().trim();

            // 2. Lectura del Correo (mÃ¡ximo 40 caracteres)
            System.out.print("  â€¢ Correo (mÃ¡x 40 caracteres)       : ");
            String correo = scanner.nextLine().trim();

            // 3. Lectura y validaciÃ³n del Salario
            System.out.print("  â€¢ Salario (float)                  : ");
            float salario;
            try {
                salario = Float.parseFloat(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.err.println("Error: El salario debe ser un nÃºmero decimal vÃ¡lido.");
                return;
            }

            // 4. Lectura del Carnet de conducir
            System.out.print("  â€¢ Â¿Tiene carnet de conducir? (S/N) : ");
            String respCarnet = scanner.nextLine().trim();
            boolean tieneCarnet = respCarnet.equalsIgnoreCase("S");

            // 5. Lectura y validaciÃ³n del Departamento (10, 20, 30, 40, 50)
            System.out.print("  â€¢ Departamento (10, 20, 30, 40, 50): ");
            short departamento;
            try {
                departamento = Short.parseShort(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.err.println("Error: El departamento debe ser un nÃºmero entero corto (short).");
                return;
            }

            // SALTO DIRECTO AL FINAL DEL FICHERO[cite: 1]
            raf.seek(tamanoOriginal);

            // Escribir los campos del registro (171 bytes en total)[cite: 1]

            // 1. CÃ³digo (int -> 4 bytes)[cite: 1]
            raf.writeInt(nuevoCodigo);

            // 2. Nombre (String ajustado a 40 chars -> 80 bytes)[cite: 1]
            escribirCadenaFija(raf, nombre, LONGITUD_CADENA_MAX);

            // 3. Correo (String ajustado a 40 chars -> 80 bytes)[cite: 1]
            escribirCadenaFija(raf, correo, LONGITUD_CADENA_MAX);

            // 4. Salario (float -> 4 bytes)[cite: 1]
            raf.writeFloat(salario);

            // 5. Carnet de conducir (boolean -> 1 byte)[cite: 1]
            raf.writeBoolean(tieneCarnet);

            // 6. Departamento (short -> 2 bytes)[cite: 1]
            raf.writeShort(departamento);

            System.out.println("\nÂ¡Empleado aÃ±adido con Ã©xito al final del fichero!");
            System.out.println("Nuevo cÃ³digo generado: " + nuevoCodigo);
            System.out.println("Nuevo tamaÃ±o total del fichero: " + raf.length() + " bytes (" 
                    + (raf.length() / TAMANO_REGISTRO) + " registros en total).");

        } catch (IOException e) {
            System.err.println("Error al aÃ±adir el empleado al fichero: " + e.getMessage());
        }
    }

    /**
     * Auxiliar para escribir una cadena fijando exactamente la longitud requerida (40 caracteres).[cite: 1]
     */
    private static void escribirCadenaFija(RandomAccessFile raf, String texto, int longitudMaxima) throws IOException {
        StringBuffer buffer = new StringBuffer(texto != null ? texto : "");
        buffer.setLength(longitudMaxima);
        raf.writeChars(buffer.toString());
    }
}