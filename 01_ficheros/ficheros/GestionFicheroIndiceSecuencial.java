package ficheros;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/*
========================================================================================
                               CÃLCULO DE TAMAÃ‘O DE REGISTROS
========================================================================================
 1. FICHERO DE DATOS ("empleados_ind_seq.dat"):
    - CÃ³digo (int)                 : 4 bytes
    - Nombre (String max 40 chars) : 80 bytes (40 caracteres * 2 bytes por char en Unicode)
    - Correo (String max 40 chars) : 80 bytes (40 caracteres * 2 bytes por char en Unicode)
    - Salario (float)              : 4 bytes
    - Carnet conducir (boolean)    : 1 byte
    - Departamento (short)         : 2 bytes
    -----------------------------------------------------------------------------------
    TAMAÃ‘O TOTAL REGISTRO DATOS    : 4 + 80 + 80 + 4 + 1 + 2 = 171 bytes

 2. FICHERO DE ÃNDICE ("emp_indice_seq.ind"):
    - CÃ³digo / Clave (int)         : 4 bytes
    - PosiciÃ³n / Offset (long)     : 8 bytes
    -----------------------------------------------------------------------------------
    TAMAÃ‘O TOTAL REGISTRO ÃNDICE   : 4 + 8 = 12 bytes
========================================================================================
*/

public class GestionFicheroIndiceSecuencial {

    private static final String FICHERO_DATOS = "empleados_ind_seq.dat";
    private static final String FICHERO_INDICE = "emp_indice_seq.ind";

    // TamaÃ±o fijo de cada entrada en el fichero de Ã­ndices (12 bytes)
    private static final int TAMANO_REG_INDICE = 12;

    // TamaÃ±o fijo de cada registro en el fichero de datos (171 bytes)
    @SuppressWarnings("unused")
    private static final int TAMANO_REG_DATOS = 171;

    public static void main(String[] args) {
        try {
            // Eliminar ficheros antiguos si existen para iniciar la prueba limpia
            new File(FICHERO_DATOS).delete();
            new File(FICHERO_INDICE).delete();

            System.out.println("=== 1. GENERANDO Y GUARDANDO 10 EMPLEADOS CON CÃ“DIGOS ALEATORIOS (1-9999) ===");
            generar10EmpleadosAleatorios();

            System.out.println("\n=== 2. CONTENIDO DEL FICHERO DE ÃNDICES (emp_indice_seq.ind) ===");
            mostrarFicheroIndice();

            System.out.println("\n=== 3. PRUEBA DE BÃšSQUEDA A TRAVÃ‰S DEL ÃNDICE ===");
            // Intentar buscar el primer cÃ³digo generado para verificar el funcionamiento
            try (RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {
                if (rafIndice.length() > 0) {
                    rafIndice.seek(0);
                    int primerCodigo = rafIndice.readInt();
                    buscarPorCodigo(primerCodigo);
                }
            }
            
            // Probar la bÃºsqueda con un cÃ³digo inexistente
            buscarPorCodigo(-1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera 10 empleados aleatorios con cÃ³digo entre 1 y 9999, guardÃ¡ndolos
     * simultÃ¡neamente en empleados_ind_seq.dat (171B) y emp_indice_seq.ind (12B).
     */
    public static void generar10EmpleadosAleatorios() throws IOException {
        Random random = new Random();
        short[] departamentos = {10, 20, 30, 40, 50};

        String[] nombresBase = {"Carlos", "Ana", "Beatriz", "David", "Elena", "Fernando", "Gloria", "Hugo", "Irene", "Javier"};

        try (RandomAccessFile rafDatos = new RandomAccessFile(FICHERO_DATOS, "rw");
             RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "rw")) {

            for (int i = 0; i < 10; i++) {
                // Generar un cÃ³digo aleatorio entre 1 y 9999
                int codigo = random.nextInt(9999) + 1;
                String nombre = nombresBase[i];
                String correo = nombre.toLowerCase() + "@empresa.com";
                float salario = 1500.0f + random.nextFloat() * 2000.0f; // Salario aleatorio entre 1500 y 3500
                boolean carnet = random.nextBoolean();
                short depto = departamentos[random.nextInt(departamentos.length)];

                // 1. Posicionar y guardar en el fichero de datos empleados_ind_seq.dat
                long offsetDatos = rafDatos.length();
                rafDatos.seek(offsetDatos);

                rafDatos.writeInt(codigo);                   // 4 bytes
                escribirCadenaFija(rafDatos, nombre, 40);    // 80 bytes
                escribirCadenaFija(rafDatos, correo, 40);    // 80 bytes
                rafDatos.writeFloat(salario);                // 4 bytes
                rafDatos.writeBoolean(carnet);               // 1 byte
                rafDatos.writeShort(depto);                  // 2 bytes

                // 2. Posicionar y guardar la entrada correspondiente en emp_indice_seq.ind
                rafIndice.seek(rafIndice.length());
                rafIndice.writeInt(codigo);                  // ID / CÃ³digo (4 bytes)
                rafIndice.writeLong(offsetDatos);            // Puntero en datos (8 bytes)

                System.out.printf("Empleado #%d guardado -> CÃ³d: %4d | Nombre: %-8s | Offset Datos: %4d B%n", 
                                  (i + 1), codigo, nombre, offsetDatos);
            }
        }
    }

    /**
     * BÃºsqueda por cÃ³digo recorriendo secuencialmente el fichero de Ã­ndices emp_indice_seq.ind
     * y saltando directamente al registro en empleados_ind_seq.dat al encontrar la coincidencia.
     */
    public static void buscarPorCodigo(int codigoBuscado) throws IOException {
        File fIndice = new File(FICHERO_INDICE);
        if (!fIndice.exists() || fIndice.length() == 0) {
            System.out.println("El fichero de Ã­ndices no existe o estÃ¡ vacÃ­o.");
            return;
        }

        try (RandomAccessFile rafDatos = new RandomAccessFile(FICHERO_DATOS, "r");
             RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {

            long totalEntradasIndice = rafIndice.length() / TAMANO_REG_INDICE;
            long offsetDatosEncontrado = -1;
            int lecturasEfectuadas = 0;

            // Recorrido secuencial de las entradas del fichero de Ã­ndice (12 bytes por entrada)
            for (int i = 0; i < totalEntradasIndice; i++) {
                lecturasEfectuadas++;
                rafIndice.seek((long) i * TAMANO_REG_INDICE);

                int codigoEntry = rafIndice.readInt();
                long offsetEntry = rafIndice.readLong();

                if (codigoEntry == codigoBuscado) {
                    offsetDatosEncontrado = offsetEntry;
                    break;
                }
            }

            if (offsetDatosEncontrado == -1) {
                System.out.println("Empleado con cÃ³digo " + codigoBuscado + " NO encontrado. (Entradas consultadas en Ã­ndice: " + lecturasEfectuadas + ")");
                return;
            }

            // Salto directo en empleados_ind_seq.dat a la posiciÃ³n obtenida del Ã­ndice
            rafDatos.seek(offsetDatosEncontrado);

            int codigo = rafDatos.readInt();
            String nombre = leerCadenaFija(rafDatos, 40);
            String correo = leerCadenaFija(rafDatos, 40);
            float salario = rafDatos.readFloat();
            boolean carnet = rafDatos.readBoolean();
            short depto = rafDatos.readShort();

            System.out.println("\n--- Â¡EMPLEADO ENCONTRADO! (Entradas leÃ­das en Ã­ndice: " + lecturasEfectuadas + ") ---");
            System.out.printf("CÃ³digo: %d%nNombre: %s%nCorreo: %s%nSalario: %.2fâ‚¬%nCarnet: %s%nDepartamento: %d%n",
                    codigo, nombre, correo, salario, (carnet ? "SÃ­" : "No"), depto);
        }
    }

    /**
     * Muestra secuencialmente todas las entradas almacenadas en emp_indice_seq.ind.
     */
    public static void mostrarFicheroIndice() throws IOException {
        File fIndice = new File(FICHERO_INDICE);
        if (!fIndice.exists()) return;

        try (RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {
            long totalEntradas = rafIndice.length() / TAMANO_REG_INDICE;
            for (int i = 0; i < totalEntradas; i++) {
                rafIndice.seek((long) i * TAMANO_REG_INDICE);
                int codigo = rafIndice.readInt();
                long offset = rafIndice.readLong();
                System.out.printf("Entrada #%d [Offset Ãndice: %3d B] -> CÃ³digo: %4d | Offset en Datos: %4d B%n",
                        i, (i * TAMANO_REG_INDICE), codigo, offset);
            }
        }
    }

    private static void escribirCadenaFija(RandomAccessFile raf, String texto, int longitud) throws IOException {
        StringBuffer buffer = new StringBuffer(texto != null ? texto : "");
        buffer.setLength(longitud);
        raf.writeChars(buffer.toString());
    }

    private static String leerCadenaFija(RandomAccessFile raf, int longitud) throws IOException {
        char[] cadena = new char[longitud];
        for (int i = 0; i < longitud; i++) {
            cadena[i] = raf.readChar();
        }
        return new String(cadena).trim();
    }
}