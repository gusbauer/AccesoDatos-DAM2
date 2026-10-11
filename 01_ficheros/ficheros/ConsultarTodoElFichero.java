package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ConsultarTodoElFichero {

    // TamaÃ±o fijo de cada registro definido (171 bytes)[cite: 1]
    private static final int TAMANO_REGISTRO = 171;
    @SuppressWarnings("unused")
    private static final int LONGITUD_CADENA_MAX = 40;

    public static void main(String[] args) {
        String rutaFichero = "empleados.dat";
        File fichero = new File(rutaFichero);

        // Validar si el fichero existe
        if (!fichero.exists() || !fichero.isFile()) {
            System.err.println("Error: El fichero '" + rutaFichero + "' no existe.");
            return;
        }

        System.out.println("\n==========================================================================================");
        System.out.println("                         LISTADO COMPLETO DE EMPLEADOS                                    ");
        System.out.println("==========================================================================================");

        // Abrir el fichero en modo solo lectura ("r")[cite: 1]
        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {

            long tamanoTotalBytes = raf.length();
            long totalRegistrosFichero = tamanoTotalBytes / TAMANO_REGISTRO;

            if (totalRegistrosFichero == 0) {
                System.out.println("El fichero estÃ¡ vacÃ­o.");
                return;
            }

            int contadorActivos = 0;
            int contadorBorrados = 0;
            int posicionOrdinal = 0;

            // Recorrer el fichero secuencialmente desde el principio hasta el final[cite: 1, 2]
            while (raf.getFilePointer() < tamanoTotalBytes) {

                // 1. Leer CÃ³digo (int - 4 bytes)[cite: 1]
                int codigo = raf.readInt();

                // 2. Leer Nombre (40 chars - 80 bytes)[cite: 1]
                String nombre = leerCadenaFija(raf, 40);

                // 3. Leer Correo (40 chars - 80 bytes)[cite: 1]
                String correo = leerCadenaFija(raf, 40);

                // 4. Leer Salario (float - 4 bytes)[cite: 1]
                float salario = raf.readFloat();

                // 5. Leer Carnet de conducir (boolean - 1 byte)[cite: 1]
                boolean carnetConducir = raf.readBoolean();

                // 6. Leer Departamento (short - 2 bytes)[cite: 1]
                short departamento = raf.readShort();

                posicionOrdinal++;

                // CONTROL DE BORRADO LÃ“GICO (-1)[cite: 1]
                if (codigo == -1) {
                    contadorBorrados++;
                    continue; // Saltar la impresiÃ³n de este registro
                }

                // Imprimir el registro activo formateado
                System.out.printf("Reg #%-2d | CÃ³d: %-4d | Nombre: %-18s | Correo: %-22s | Salario: %7.2f â‚¬ | Carnet: %-2s | Depto: %d%n",
                        posicionOrdinal,
                        codigo,
                        nombre,
                        correo,
                        salario,
                        carnetConducir ? "SÃ­" : "No",
                        departamento);

                contadorActivos++;
            }

            System.out.println("==========================================================================================");
            System.out.println("Resumen de lectura:");
            System.out.println("  â€¢ Empleados activos  : " + contadorActivos);
            System.out.println("  â€¢ Registros borrados : " + contadorBorrados);
            System.out.println("  â€¢ Total en fichero   : " + totalRegistrosFichero + " registros (" + tamanoTotalBytes + " bytes).");

        } catch (IOException e) {
            System.err.println("Error de lectura en el fichero: " + e.getMessage());
        }
    }

    /**
     * Auxiliar para leer una cadena de longitud fija de caracteres Unicode (2 bytes cada uno).[cite: 1]
     */
    private static String leerCadenaFija(RandomAccessFile raf, int longitud) throws IOException {
        char[] caracteres = new char[longitud];
        for (int i = 0; i < longitud; i++) {
            caracteres[i] = raf.readChar();
        }
        return new String(caracteres).trim();
    }
}