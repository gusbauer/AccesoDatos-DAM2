package ficheros;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/*
========================================================================================
                               CÃLCULO DE TAMAÃ‘O DE REGISTROS
========================================================================================
 1. FICHERO DE DATOS ("empleados_ind_arbolB.dat"):
    - CÃ³digo (int)                 : 4 bytes
    - Nombre (String max 40 chars) : 80 bytes (40 caracteres * 2 bytes por char Unicode)
    - Correo (String max 40 chars) : 80 bytes (40 caracteres * 2 bytes por char Unicode)
    - Salario (float)              : 4 bytes
    - Carnet conducir (boolean)    : 1 byte
    - Departamento (short)         : 2 bytes
    -----------------------------------------------------------------------------------
    TAMAÃ‘O TOTAL REGISTRO DATOS    : 4 + 80 + 80 + 4 + 1 + 2 = 171 bytes

 2. FICHERO DE ÃNDICE ÃRBOL-B ("emp_indice_arbolB.ind"):
    - ind (int)                    : 4 bytes (CÃ³digo del empleado)
    - posicion (long)              : 8 bytes (Offset al registro en el fichero de datos)
    - menor_ind (long)             : 8 bytes (Offset al hijo izquierdo / valores menores)
    - mayor_ind (long)             : 8 bytes (Offset al hijo derecho / valores mayores)
    -----------------------------------------------------------------------------------
    TAMAÃ‘O TOTAL NODO ÃNDICE       : 4 + 8 + 8 + 8 = 28 bytes
========================================================================================
*/

public class GestionFicheroIndiceArbolB {

    private static final String FICHERO_DATOS = "empleados_ind_arbolB.dat";
    private static final String FICHERO_INDICE = "emp_indice_arbolB.ind";

    // TamaÃ±o fijo de cada entrada en el fichero de Ã­ndices (28 bytes)
    private static final int TAMANO_NODO_INDICE = 28;

    // TamaÃ±o fijo de cada registro en el fichero de datos (171 bytes)
    @SuppressWarnings("unused")
    private static final int TAMANO_REG_DATOS = 171;

    // Valor constante para representar un puntero nulo
    private static final long NULL_POINTER = -1L;

    public static void main(String[] args) {
        try {
            // Eliminar ficheros si ya existen para iniciar la prueba completamente limpia
            new File(FICHERO_DATOS).delete();
            new File(FICHERO_INDICE).delete();

            System.out.println("=== 1. GENERANDO Y GUARDANDO 10 EMPLEADOS EN ESTRUCTURA ÃRBOL-B ===");
            generar10EmpleadosAleatorios();

            System.out.println("\n=== 2. ESTRUCTURA Y CONTENIDO DEL FICHERO DE ÃNDICES (emp_indice_arbolB.ind) ===");
            mostrarFicheroIndice();

            System.out.println("\n=== 3. PRUEBAS DE BÃšSQUEDA B-TREE (RECORRIDO $O(\\log N)$) ===");
            
            // Tomar el cÃ³digo de la raÃ­z para realizar una bÃºsqueda de prueba
            try (RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {
                if (rafIndice.length() > 0) {
                    rafIndice.seek(0);
                    int codigoRaiz = rafIndice.readInt();
                    buscarPorCodigo(codigoRaiz);
                }
            }

            // BÃºsqueda de un cÃ³digo no existente
            buscarPorCodigo(-1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera 10 empleados aleatorios con cÃ³digo entre 1 y 9999, e inserta cada uno
     * en empleados_ind_arbolB.dat (171B) y su nodo correspondiente en emp_indice_arbolB.ind (28B).
     */
    public static void generar10EmpleadosAleatorios() throws IOException {
        Random random = new Random();
        short[] departamentos = {10, 20, 30, 40, 50};
        String[] nombresBase = {"Carlos", "Ana", "Beatriz", "David", "Elena", "Fernando", "Gloria", "Hugo", "Irene", "Javier"};

        for (int i = 0; i < 10; i++) {
            int codigo = random.nextInt(9999) + 1;
            String nombre = nombresBase[i];
            String correo = nombre.toLowerCase() + "@empresa.com";
            float salario = 1500.0f + random.nextFloat() * 2000.0f;
            boolean carnet = random.nextBoolean();
            short depto = departamentos[random.nextInt(departamentos.length)];

            insertarEmpleado(codigo, nombre, correo, salario, carnet, depto);
        }
    }

    /**
     * Inserta un nuevo registro al final del fichero de datos y vincula
     * el nodo de Ã­ndice en la posiciÃ³n jerÃ¡rquica adecuada dentro del Ãrbol-B.
     */
    @SuppressWarnings("unused")
    public static void insertarEmpleado(int codigo, String nombre, String correo, float salario, boolean carnet, short depto) throws IOException {
        try (RandomAccessFile rafDatos = new RandomAccessFile(FICHERO_DATOS, "rw");
             RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "rw")) {

            // 1. Escribir el registro al final del fichero de datos empleados_ind_arbolB.dat
            long offsetDatos = rafDatos.length();
            rafDatos.seek(offsetDatos);

            rafDatos.writeInt(codigo);                   // 4 bytes
            escribirCadenaFija(rafDatos, nombre, 40);    // 80 bytes
            escribirCadenaFija(rafDatos, correo, 40);    // 80 bytes
            rafDatos.writeFloat(salario);                // 4 bytes
            rafDatos.writeBoolean(carnet);               // 1 byte
            rafDatos.writeShort(depto);                  // 2 bytes

            // 2. Calcular la nueva posiciÃ³n de Ã­ndice al final de emp_indice_arbolB.ind
            long nuevoOffsetIndice = rafIndice.length();

            // Si el fichero de Ã­ndices estÃ¡ vacÃ­o, este registro pasa a ser la RAÃZ (offset 0 B)
            if (nuevoOffsetIndice == 0) {
                rafIndice.seek(0);
                rafIndice.writeInt(codigo);              // ind (4B)
                rafIndice.writeLong(offsetDatos);        // posicion (8B)
                rafIndice.writeLong(NULL_POINTER);       // menor_ind (8B)
                rafIndice.writeLong(NULL_POINTER);       // mayor_ind (8B)
                System.out.printf("Empleado CÃ³d: %4d guardado como RAÃZ del Ãrbol-B (Offset Datos: %4d B)%n", codigo, offsetDatos);
                return;
            }

            // 3. Recorrer el Ã¡rbol desde la raÃ­z (Offset 0) para encontrar el lugar de inserciÃ³n
            long offsetActual = 0;

            while (true) {
                rafIndice.seek(offsetActual);
                int codigoActual = rafIndice.readInt();
                long posDatosActual = rafIndice.readLong();
                long menorInd = rafIndice.readLong();
                long mayorInd = rafIndice.readLong();

                if (codigo == codigoActual) {
                    System.out.println("Aviso: El cÃ³digo " + codigo + " ya existe. Se omite la duplicaciÃ³n.");
                    return;
                }

                if (codigo < codigoActual) {
                    // Moverse al subÃ¡rbol izquierdo (menores)
                    if (menorInd == NULL_POINTER) {
                        // Actualizar el puntero menor_ind del nodo padre actual
                        rafIndice.seek(offsetActual + 4 + 8); // Saltamos ind (4B) + posicion (8B)
                        rafIndice.writeLong(nuevoOffsetIndice);
                        break;
                    } else {
                        offsetActual = menorInd;
                    }
                } else {
                    // Moverse al subÃ¡rbol derecho (mayores)
                    if (mayorInd == NULL_POINTER) {
                        // Actualizar el puntero mayor_ind del nodo padre actual
                        rafIndice.seek(offsetActual + 4 + 8 + 8); // Saltamos ind (4B) + posicion (8B) + menor_ind (8B)
                        rafIndice.writeLong(nuevoOffsetIndice);
                        break;
                    } else {
                        offsetActual = mayorInd;
                    }
                }
            }

            // 4. Escribir el nuevo nodo al final del fichero de Ã­ndices
            rafIndice.seek(nuevoOffsetIndice);
            rafIndice.writeInt(codigo);              // ind
            rafIndice.writeLong(offsetDatos);        // posicion
            rafIndice.writeLong(NULL_POINTER);       // menor_ind
            rafIndice.writeLong(NULL_POINTER);       // mayor_ind

            System.out.printf("Empleado CÃ³d: %4d insertado en nodo [Offset Ãndice: %3d B] -> Offset Datos: %4d B%n", 
                    codigo, nuevoOffsetIndice, offsetDatos);
        }
    }

    /**
     * BÃºsqueda optimizada O(log N) saltando entre nodos del fichero de Ã­ndice
     * utilizando menor_ind y mayor_ind.
     */
    public static void buscarPorCodigo(int codigoBuscado) throws IOException {
        File fIndice = new File(FICHERO_INDICE);
        if (!fIndice.exists() || fIndice.length() == 0) {
            System.out.println("El fichero de Ã­ndices no existe o estÃ¡ vacÃ­o.");
            return;
        }

        try (RandomAccessFile rafDatos = new RandomAccessFile(FICHERO_DATOS, "r");
             RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {

            long offsetActual = 0; // Se comienza en el nodo raÃ­z (Offset 0B)
            long offsetDatosEncontrado = -1;
            int nodosConsultados = 0;

            while (offsetActual != NULL_POINTER) {
                nodosConsultados++;
                rafIndice.seek(offsetActual);

                int ind = rafIndice.readInt();
                long posicion = rafIndice.readLong();
                long menorInd = rafIndice.readLong();
                long mayorInd = rafIndice.readLong();

                if (codigoBuscado == ind) {
                    offsetDatosEncontrado = posicion;
                    break;
                } else if (codigoBuscado < ind) {
                    offsetActual = menorInd; // Seguir bÃºsqueda por las claves menores
                } else {
                    offsetActual = mayorInd; // Seguir bÃºsqueda por las claves mayores
                }
            }

            if (offsetDatosEncontrado == -1) {
                System.out.println("\nEmpleado con cÃ³digo " + codigoBuscado + " NO encontrado (Nodos de Ã­ndice consultados: " + nodosConsultados + ").");
                return;
            }

            // Acceso directo al registro en el fichero de datos
            rafDatos.seek(offsetDatosEncontrado);

            int codigo = rafDatos.readInt();
            String nombre = leerCadenaFija(rafDatos, 40);
            String correo = leerCadenaFija(rafDatos, 40);
            float salario = rafDatos.readFloat();
            boolean carnet = rafDatos.readBoolean();
            short depto = rafDatos.readShort();

            System.out.println("\n--- Â¡EMPLEADO ENCONTRADO! (Nodos consultados en Ãrbol-B: " + nodosConsultados + ") ---");
            System.out.printf("CÃ³digo: %d%nNombre: %s%nCorreo: %s%nSalario: %.2fâ‚¬%nCarnet: %s%nDepartamento: %d%n",
                    codigo, nombre, correo, salario, (carnet ? "SÃ­" : "No"), depto);
        }
    }

    /**
     * Recorre linealmente emp_indice_arbolB.ind para volcar la estructura en disco de los nodos.
     */
    public static void mostrarFicheroIndice() throws IOException {
        File fIndice = new File(FICHERO_INDICE);
        if (!fIndice.exists()) return;

        try (RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {
            long totalNodos = rafIndice.length() / TAMANO_NODO_INDICE;

            for (int i = 0; i < totalNodos; i++) {
                rafIndice.seek((long) i * TAMANO_NODO_INDICE);

                int ind = rafIndice.readInt();
                long posicion = rafIndice.readLong();
                long menorInd = rafIndice.readLong();
                long mayorInd = rafIndice.readLong();

                String menorStr = (menorInd == NULL_POINTER) ? "null" : menorInd + " B";
                String mayorStr = (mayorInd == NULL_POINTER) ? "null" : mayorInd + " B";

                System.out.printf("Nodo #%d [Offset: %3d B] -> CÃ³d: %4d | PosiciÃ³n Datos: %4d B | Menor_Ind: %-6s | Mayor_Ind: %-6s%n",
                        i, (i * TAMANO_NODO_INDICE), ind, posicion, menorStr, mayorStr);
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