package ficheros;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class GestionFicheroIndiceArbolB {

    private static final String FICHERO_DATOS = "empleados_ind_arbolB.dat";
    private static final String FICHERO_INDICE = "emp_indice_arbolB.ind";
    private static final int TAMANO_NODO_INDICE = 28;
    @SuppressWarnings("unused")
    private static final int TAMANO_REG_DATOS = 171;
    private static final long NULL_POINTER = -1L;

    public static void main(String[] args) {
        try {
            // ✅ Nombre calificado para forzar java.io.File
            new java.io.File(FICHERO_DATOS).delete();
            new java.io.File(FICHERO_INDICE).delete();

            System.out.println("=== 1. GENERANDO Y GUARDANDO 10 EMPLEADOS EN ESTRUCTURA ÁRBOL-B ===");
            generar10EmpleadosAleatorios();

            System.out.println("\n=== 2. ESTRUCTURA Y CONTENIDO DEL FICHERO DE ÍNDICES (emp_indice_arbolB.ind) ===");
            mostrarFicheroIndice();

            System.out.println("\n=== 3. PRUEBAS DE BÚSQUEDA B-TREE (RECORRIDO O(log N)) ===");

            try (RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {
                if (rafIndice.length() > 0) {
                    rafIndice.seek(0);
                    int codigoRaiz = rafIndice.readInt();
                    buscarPorCodigo(codigoRaiz);
                }
            }

            buscarPorCodigo(-1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @SuppressWarnings("unused")
    public static void insertarEmpleado(int codigo, String nombre, String correo, float salario, boolean carnet, short depto) throws IOException {
        try (RandomAccessFile rafDatos = new RandomAccessFile(FICHERO_DATOS, "rw");
             RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "rw")) {

            long offsetDatos = rafDatos.length();
            rafDatos.seek(offsetDatos);

            rafDatos.writeInt(codigo);
            escribirCadenaFija(rafDatos, nombre, 40);
            escribirCadenaFija(rafDatos, correo, 40);
            rafDatos.writeFloat(salario);
            rafDatos.writeBoolean(carnet);
            rafDatos.writeShort(depto);

            long nuevoOffsetIndice = rafIndice.length();

            if (nuevoOffsetIndice == 0) {
                rafIndice.seek(0);
                rafIndice.writeInt(codigo);
                rafIndice.writeLong(offsetDatos);
                rafIndice.writeLong(NULL_POINTER);
                rafIndice.writeLong(NULL_POINTER);
                System.out.printf("Empleado Cód: %4d guardado como RAÍZ del Árbol-B (Offset Datos: %4d B)%n", codigo, offsetDatos);
                return;
            }

            long offsetActual = 0;

            while (true) {
                rafIndice.seek(offsetActual);
                int codigoActual = rafIndice.readInt();
                long posDatosActual = rafIndice.readLong();
                long menorInd = rafIndice.readLong();
                long mayorInd = rafIndice.readLong();

                if (codigo == codigoActual) {
                    System.out.println("Aviso: El código " + codigo + " ya existe. Se omite la duplicación.");
                    return;
                }

                if (codigo < codigoActual) {
                    if (menorInd == NULL_POINTER) {
                        rafIndice.seek(offsetActual + 4 + 8);
                        rafIndice.writeLong(nuevoOffsetIndice);
                        break;
                    } else {
                        offsetActual = menorInd;
                    }
                } else {
                    if (mayorInd == NULL_POINTER) {
                        rafIndice.seek(offsetActual + 4 + 8 + 8);
                        rafIndice.writeLong(nuevoOffsetIndice);
                        break;
                    } else {
                        offsetActual = mayorInd;
                    }
                }
            }

            rafIndice.seek(nuevoOffsetIndice);
            rafIndice.writeInt(codigo);
            rafIndice.writeLong(offsetDatos);
            rafIndice.writeLong(NULL_POINTER);
            rafIndice.writeLong(NULL_POINTER);

            System.out.printf("Empleado Cód: %4d insertado en nodo [Offset Índice: %3d B] -> Offset Datos: %4d B%n",
                    codigo, nuevoOffsetIndice, offsetDatos);
        }
    }

    public static void buscarPorCodigo(int codigoBuscado) throws IOException {
        java.io.File fIndice = new java.io.File(FICHERO_INDICE);   // ✅ calificado
        if (!fIndice.exists() || fIndice.length() == 0) {
            System.out.println("El fichero de índices no existe o está vacío.");
            return;
        }

        try (RandomAccessFile rafDatos = new RandomAccessFile(FICHERO_DATOS, "r");
             RandomAccessFile rafIndice = new RandomAccessFile(FICHERO_INDICE, "r")) {

            long offsetActual = 0;
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
                    offsetActual = menorInd;
                } else {
                    offsetActual = mayorInd;
                }
            }

            if (offsetDatosEncontrado == -1) {
                System.out.println("\nEmpleado con código " + codigoBuscado + " NO encontrado (Nodos de índice consultados: " + nodosConsultados + ").");
                return;
            }

            rafDatos.seek(offsetDatosEncontrado);

            int codigo = rafDatos.readInt();
            String nombre = leerCadenaFija(rafDatos, 40);
            String correo = leerCadenaFija(rafDatos, 40);
            float salario = rafDatos.readFloat();
            boolean carnet = rafDatos.readBoolean();
            short depto = rafDatos.readShort();

            System.out.println("\n--- ¡EMPLEADO ENCONTRADO! (Nodos consultados en Árbol-B: " + nodosConsultados + ") ---");
            System.out.printf("Código: %d%nNombre: %s%nCorreo: %s%nSalario: %.2f€%nCarnet: %s%nDepartamento: %d%n",
                    codigo, nombre, correo, salario, (carnet ? "Sí" : "No"), depto);
        }
    }

    public static void mostrarFicheroIndice() throws IOException {
        java.io.File fIndice = new java.io.File(FICHERO_INDICE);   // ✅ calificado
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

                System.out.printf("Nodo #%d [Offset: %3d B] -> Cód: %4d | Posición Datos: %4d B | Menor_Ind: %-6s | Mayor_Ind: %-6s%n",
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