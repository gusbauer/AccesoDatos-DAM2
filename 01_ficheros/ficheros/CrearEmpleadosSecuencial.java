package ficheros;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Programa para crear un fichero de acceso directo con cÃ³digos secuenciales (1, 2, 3...).
 *
 * ESTRUCTURA DEL REGISTRO (171 BYTES):
 * -------------------------------------------------------------------------------------
 * 1. CÃ³digo (int)                 : 4 bytes  (1, 2, 3, ... N)
 * 2. Nombre (String, max 40 chars): 80 bytes (40 chars * 2 bytes/char)
 * 3. Correo (String, max 40 chars): 80 bytes (40 chars * 2 bytes/char)
 * 4. Salario (float)              : 4 bytes 
 * 5. Carnet de conducir (boolean) : 1 byte  
 * 6. Departamento (short)         : 2 bytes  (10, 20, 30, 40, 50)
 * -------------------------------------------------------------------------------------
 * TAMAÃ‘O TOTAL DEL REGISTRO      : 171 bytes
 */
@SuppressWarnings("unused")
public class CrearEmpleadosSecuencial {

    private static final int TAMANO_REGISTRO = 171;
    private static final int LONGITUD_CADENA_MAX = 40;

    public static void main(String[] args) {
        String rutaFichero = "empleados.dat";

        // Datos de entrada de los 10 empleados
        String[] nombres = {
            "Ana Gomez", "Carlos Perez", "Beatriz Ruiz", "David Lopez", "Elena Fernandez",
            "Fernando Torres", "Gema Martin", "Hugo Sanchez", "Isabel Diaz", "Javier Garcia"
        };
        
        String[] correos = {
            "agomez@empresa.com", "cperez@empresa.com", "bruiz@empresa.com", "dlopez@empresa.com", "efernandez@empresa.com",
            "ftorres@empresa.com", "gmartin@empresa.com", "hsanchez@empresa.com", "idiaz@empresa.com", "jgarcia@empresa.com"
        };
        
        float[] salarios = {
            2500.50f, 1800.00f, 3100.75f, 2100.20f, 2800.00f,
            1950.40f, 3300.00f, 2250.60f, 2900.80f, 2400.00f
        };
        
        boolean[] carnetsConducir = {true, false, true, true, false, true, false, true, true, false};
        
        short[] departamentos = {10, 20, 30, 10, 40, 50, 20, 30, 40, 50};

        System.out.println("Creando fichero con cÃ³digos secuenciales empezando en 1...");

        try (RandomAccessFile raf = new RandomAccessFile(rutaFichero, "rw")) {

            for (int i = 0; i < 10; i++) {
                // Generar cÃ³digo secuencial: 1, 2, 3, 4, ...
                int codigoSecuencial = i + 1;

                // CÃ¡lculo de la posiciÃ³n directa: (codigo - 1) * 171
                long posicion = (long) (codigoSecuencial - 1) * TAMANO_REGISTRO;
                raf.seek(posicion);

                // 1. Escribir CÃ³digo (int -> 4 bytes)
                raf.writeInt(codigoSecuencial);

                // 2. Escribir Nombre (String ajustado a 40 chars -> 80 bytes)
                escribirCadenaFija(raf, nombres[i], LONGITUD_CADENA_MAX);

                // 3. Escribir Correo (String ajustado a 40 chars -> 80 bytes)
                escribirCadenaFija(raf, correos[i], LONGITUD_CADENA_MAX);

                // 4. Escribir Salario (float -> 4 bytes)
                raf.writeFloat(salarios[i]);

                // 5. Escribir Carnet de conducir (boolean -> 1 byte)
                raf.writeBoolean(carnetsConducir[i]);

                // 6. Escribir Departamento (short -> 2 bytes)
                raf.writeShort(departamentos[i]);
            }

            System.out.println("Â¡Fichero creado con Ã©xito!");
            System.out.println("NÃºmero de registros creados: 10 (CÃ³digos del 1 al 10).");
            System.out.println("TamaÃ±o total del fichero: " + raf.length() + " bytes.");

        } catch (IOException e) {
            System.err.println("Error al escribir el fichero: " + e.getMessage());
        }
    }

    /**
     * Escribe una cadena fijando exactamente la longitud requerida (40 caracteres).
     */
    private static void escribirCadenaFija(RandomAccessFile raf, String texto, int longitudMaxima) throws IOException {
        StringBuffer buffer = new StringBuffer(texto != null ? texto : "");
        buffer.setLength(longitudMaxima);
        raf.writeChars(buffer.toString());
    }
}