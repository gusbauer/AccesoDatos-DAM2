import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/*
 * ============================================================
 * PRÁCTICA Nº2 - ACCESO A DATOS
 * FICHERO BINARIO DE ACCESO DIRECTO
 * ============================================================
 *
 * Fichero utilizado: videojuegos.dat
 *
 * COMPILAR:
 * javac Practica2Videojuegos.java
 *
 * EJECUTAR:
 * java Practica2Videojuegos
 *
 * ============================================================
 * CLASES UTILIZADAS
 * ============================================================
 *
 * RandomAccessFile:
 * Clase principal de esta práctica.
 * Permite leer, escribir y mover el puntero a una posición
 * concreta del fichero mediante seek().
 *
 * Scanner:
 * Se utiliza para leer datos introducidos por teclado.
 *
 * IOException:
 * Excepción utilizada para controlar errores de entrada/salida.
 *
 * IMPORTANTE:
 *
 * RandomAccessFile -> es la CLASE.
 * raf              -> es una VARIABLE que referencia al objeto.
 *
 * Ejemplo:
 *
 * RandomAccessFile raf = new RandomAccessFile(FICHERO, "rw");
 *
 * ============================================================
 * TIPO DE FICHERO
 * ============================================================
 *
 * videojuegos.dat es un fichero BINARIO.
 *
 * Los datos se escriben mediante:
 * writeInt(), writeChar(), writeFloat()
 *
 * y se leen mediante:
 * readInt(), readChar(), readFloat()
 *
 * Es de ACCESO DIRECTO porque todos los registros ocupan
 * exactamente el mismo número de bytes y podemos calcular
 * directamente la posición de un registro.
 *
 * También hay operaciones de ACCESO SECUENCIAL, como:
 * - mostrar todos los videojuegos;
 * - buscar todos los videojuegos de una plataforma.
 *
 * ============================================================
 * CREACIÓN DEL FICHERO
 * ============================================================
 *
 * Se abre así:
 *
 * new RandomAccessFile(FICHERO, "rw")
 *
 * "r"  -> solo lectura.
 * "rw" -> lectura y escritura.
 *
 * Si el fichero no existe y se abre con "rw",
 * RandomAccessFile lo crea.
 *
 * Después los registros se escriben utilizando los métodos
 * writeInt(), writeChar() y writeFloat().
 *
 * ============================================================
 * ESTRUCTURA DE UN REGISTRO
 * ============================================================
 *
 * Código      -> int      -> 4 bytes
 * Título      -> 30 char  -> 60 bytes
 * Plataforma  -> 15 char  -> 30 bytes
 * Año         -> int      -> 4 bytes
 * Precio      -> float    -> 4 bytes
 *
 * TOTAL:
 *
 * 4 + 60 + 30 + 4 + 4 = 102 bytes.
 *
 * Cada char ocupa 2 bytes.
 *
 * Todos los registros tienen tamaño fijo para poder realizar
 * acceso directo.
 *
 * ============================================================
 * ACCESO DIRECTO
 * ============================================================
 *
 * Para buscar un videojuego por código:
 *
 * posicion = (codigo - 1) * TAM_REGISTRO
 *
 * Ejemplo:
 *
 * código 5:
 * (5 - 1) * 102 = 408
 *
 * Después:
 *
 * raf.seek(408);
 *
 * seek() mueve el puntero directamente a esa posición.
 *
 * ============================================================
 * ACCESO SECUENCIAL
 * ============================================================
 *
 * Para recorrer todos los registros:
 *
 * while (raf.getFilePointer() < raf.length())
 *
 * getFilePointer() -> posición actual del puntero.
 * length()         -> tamaño total del fichero.
 *
 * ============================================================
 * OPERACIONES BÁSICAS
 * ============================================================
 *
 * 1. CREAR / AÑADIR
 *
 * raf.seek(raf.length());
 *
 * Nos colocamos al final y escribimos el nuevo registro.
 *
 *
 * 2. LEER
 *
 * raf.readInt();
 * raf.readChar();
 * raf.readFloat();
 *
 *
 * 3. ACTUALIZAR
 *
 * Calculamos la posición del registro con el código,
 * hacemos seek() y sobrescribimos solamente el dato.
 *
 *
 * 4. ELIMINAR
 *
 * Se utiliza BORRADO LÓGICO.
 *
 * raf.seek(posicion);
 * raf.writeInt(-1);
 *
 * No eliminamos físicamente los 102 bytes.
 * Marcamos el registro con código -1.
 *
 * ============================================================
 * MODIFICACIÓN DEL PRECIO
 * ============================================================
 *
 * Antes del precio tenemos:
 *
 * Código      -> 4 bytes
 * Título      -> 60 bytes
 * Plataforma  -> 30 bytes
 * Año         -> 4 bytes
 *
 * TOTAL = 98 bytes.
 *
 * Por eso:
 *
 * raf.seek(posicion + 98);
 * raf.writeFloat(nuevoPrecio);
 *
 * Así modificamos solamente los 4 bytes del precio.
 *
 * ============================================================
 * DE DÓNDE SALEN LOS DATOS
 * ============================================================
 *
 * Opción 1:
 * Los 15 videojuegos iniciales salen de arrays definidos
 * dentro del programa.
 *
 * Opción 2:
 * Los datos los introduce el usuario mediante Scanner.
 *
 * ============================================================
 */

public class Practica2Videojuegos {

    /*  Son atributos constantes de la clase. private significa que solo se utilizan dentro de esta clase, static que pertenecen a la clase y no a un objeto concreto, y final que su valor no cambia.

    Y qué representa cada uno:
    FICHERO            nombre del fichero
    LONG_TITULO        longitud fija del título
    LONG_PLATAFORMA    longitud fija de plataforma
    TAM_REGISTRO       102 bytes por videojuego
    POS_PRECIO         desplazamiento de 98 bytes hasta el precio */
    // Nombre del fichero binario.
    private static final String FICHERO = "videojuegos.dat";

    // Longitud fija de las cadenas para que todos los registros
    // tengan exactamente el mismo tamaño.
    private static final int LONG_TITULO = 30;
    private static final int LONG_PLATAFORMA = 15;

    // Cada videojuego ocupa exactamente 102 bytes.
    private static final int TAM_REGISTRO = 102;

    // El precio comienza 98 bytes después del inicio del registro.
    private static final int POS_PRECIO = 98;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        do {
            mostrarMenu();

            try {
                opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1:
                        anadir15();
                        break;
                    case 2:
                        anadirUno(sc);
                        break;
                    case 3:
                        mostrarTodos();
                        break;
                    case 4:
                        buscarPorCodigo(sc);
                        break;
                    case 5:
                        buscarPorPlataforma(sc);
                        break;
                    case 6:
                        modificarPrecio(sc);
                        break;
                    case 7:
                        eliminar(sc);
                        break;
                    case 8:
                        System.out.println("Programa finalizado.");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un número.");
            }

            System.out.println();

        } while (opcion != 8);

        sc.close();
    }

    private static void mostrarMenu() {
        System.out.println("===== GESTIÓN DE VIDEOJUEGOS =====");
        System.out.println("1. Añadir 15 videojuegos");
        System.out.println("2. Añadir un videojuego");
        System.out.println("3. Mostrar todos");
        System.out.println("4. Buscar por código");
        System.out.println("5. Buscar por plataforma");
        System.out.println("6. Modificar precio");
        System.out.println("7. Eliminar videojuego");
        System.out.println("8. Salir");
        System.out.print("Opción: ");
    }

    private static void anadir15() {

        String[] titulos = {
                "Minecraft",
                "The Witcher 3",
                "GTA V",
                "Red Dead Redemption 2",
                "Cyberpunk 2077",
                "God of War",
                "Elden Ring",
                "Hades",
                "Hollow Knight",
                "DOOM Eternal",
                "Forza Horizon 5",
                "Resident Evil 4",
                "Baldur's Gate 3",
                "Spider-Man",
                "Dark Souls 3"
        };

        String[] plataformas = {
                "PC", "PC", "PS5", "PS5", "PC",
                "PS5", "PC", "PC", "PC", "PC",
                "Xbox", "PS5", "PC", "PS5", "PC"
        };

        int[] anios = {
                2011, 2015, 2013, 2018, 2020,
                2018, 2022, 2020, 2017, 2020,
                2021, 2023, 2023, 2018, 2016
        };

        float[] precios = {
                29.99f, 39.99f, 19.99f, 59.99f, 49.99f,
                39.99f, 59.99f, 24.99f, 14.99f, 39.99f,
                59.99f, 49.99f, 59.99f, 49.99f, 39.99f
        };

        // RandomAccessFile permite leer, escribir y movernos por el fichero.
        // "rw" significa lectura y escritura.
        // Si el fichero no existe, se crea. 
        try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "rw")) {

            int siguienteCodigo = (int) (raf.length() / TAM_REGISTRO) + 1;
            // Nos movemos al final del fichero para añadir sin sobrescribir.
            raf.seek(raf.length());

            for (int i = 0; i < titulos.length; i++) {
                escribirRegistro(
                        raf,
                        siguienteCodigo + i,
                        titulos[i],
                        plataformas[i],
                        anios[i],
                        precios[i]
                );
            }

            System.out.println("15 videojuegos añadidos.");

        } catch (IOException e) {
            System.out.println("Error al escribir el fichero.");
        }
    }

    private static void anadirUno(Scanner sc) {

        try {
            System.out.print("Título: ");
            String titulo = sc.nextLine();

            System.out.print("Plataforma: ");
            String plataforma = sc.nextLine();

            System.out.print("Año: ");
            int anio = Integer.parseInt(sc.nextLine());

            System.out.print("Precio: ");
            float precio = Float.parseFloat(sc.nextLine().replace(',', '.'));

            // RandomAccessFile es la clase.
            // raf es la variable que referencia al objeto.
            // "rw" permite leer y escribir y crea el fichero si no existe.
            try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "rw")) {

                int codigo = (int) (raf.length() / TAM_REGISTRO) + 1;
                // length() devuelve el tamaño del fichero.
                // seek() mueve el puntero.
                // Al moverlo a length() nos colocamos al final para añadir.
                raf.seek(raf.length());

                escribirRegistro(
                        raf,
                        codigo,
                        titulo,
                        plataforma,
                        anio,
                        precio
                );

                System.out.println("Videojuego añadido con código " + codigo + ".");
            }

        } catch (NumberFormatException e) {
            System.out.println("Año o precio no válido.");
        } catch (IOException e) {
            System.out.println("Error al escribir el fichero.");
        }
    }

    private static void mostrarTodos() {

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "r")) {

            // ACCESO SECUENCIAL:
            // recorremos el fichero registro a registro hasta llegar al final.
            while (raf.getFilePointer() < raf.length()) {

                int codigo = raf.readInt();
                String titulo = leerCadena(raf, LONG_TITULO);
                String plataforma = leerCadena(raf, LONG_PLATAFORMA);
                int anio = raf.readInt();
                float precio = raf.readFloat();

                if (codigo != -1) {
                    mostrarRegistro(codigo, titulo, plataforma, anio, precio);
                }
            }

        } catch (IOException e) {
            System.out.println("No se ha podido leer el fichero.");
        }
    }

    private static void buscarPorCodigo(Scanner sc) {

        try {
            System.out.print("Código: ");
            int codigoBuscado = Integer.parseInt(sc.nextLine());

            if (codigoBuscado < 1) {
                System.out.println("Código no válido.");
                return;
            }

            try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "r")) {

                // ACCESO DIRECTO:
                // como cada registro ocupa 102 bytes, calculamos directamente
                // dónde empieza el videojuego solicitado.
                long posicion = (long) (codigoBuscado - 1) * TAM_REGISTRO;

                if (posicion >= raf.length()) {
                    System.out.println("El código no existe.");
                    return;
                }

                raf.seek(posicion);

                int codigo = raf.readInt();

                if (codigo == -1) {
                    System.out.println("El videojuego está eliminado.");
                    return;
                }

                String titulo = leerCadena(raf, LONG_TITULO);
                String plataforma = leerCadena(raf, LONG_PLATAFORMA);
                int anio = raf.readInt();
                float precio = raf.readFloat();

                mostrarRegistro(codigo, titulo, plataforma, anio, precio);
            }

        } catch (NumberFormatException e) {
            System.out.println("Código no válido.");
        } catch (IOException e) {
            System.out.println("Error al leer el fichero.");
        }
    }

    private static void buscarPorPlataforma(Scanner sc) {

        System.out.print("Plataforma: ");
        String plataformaBuscada = sc.nextLine();

        boolean encontrado = false;

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "r")) {


            // ACCESO SECUENCIAL:
            // recorremos todos los registros desde el principio hasta el final.
            while (raf.getFilePointer() < raf.length()) {

                int codigo = raf.readInt();
                String titulo = leerCadena(raf, LONG_TITULO);
                String plataforma = leerCadena(raf, LONG_PLATAFORMA);
                int anio = raf.readInt();
                float precio = raf.readFloat();

                if (codigo != -1
                        && plataforma.equalsIgnoreCase(plataformaBuscada)) {

                    mostrarRegistro(codigo, titulo, plataforma, anio, precio);
                    encontrado = true;
                }
            }

            if (!encontrado) {
                System.out.println("No hay videojuegos de esa plataforma.");
            }

        } catch (IOException e) {
            System.out.println("Error al leer el fichero.");
        }
    }

    private static void modificarPrecio(Scanner sc) {

        try {
            System.out.print("Código: ");
            int codigoBuscado = Integer.parseInt(sc.nextLine());

            if (codigoBuscado < 1) {
                System.out.println("Código no válido.");
                return;
            }

            try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "rw")) {

                long posicion = (long) (codigoBuscado - 1) * TAM_REGISTRO;

                if (posicion >= raf.length()) {
                    System.out.println("El código no existe.");
                    return;
                }

                raf.seek(posicion);

                int codigo = raf.readInt();

                if (codigo == -1) {
                    System.out.println("El videojuego está eliminado.");
                    return;
                }

                String titulo = leerCadena(raf, LONG_TITULO);
                String plataforma = leerCadena(raf, LONG_PLATAFORMA);
                int anio = raf.readInt();
                float precioActual = raf.readFloat();

                mostrarRegistro(
                        codigo,
                        titulo,
                        plataforma,
                        anio,
                        precioActual
                );

                System.out.print("Nuevo precio: ");
                float nuevoPrecio =
                        Float.parseFloat(sc.nextLine().replace(',', '.'));

                // nos situamos directamente sobre el campo precio
                // y sobrescribimos únicamente esos 4 bytes.
                raf.seek(posicion + POS_PRECIO);
                raf.writeFloat(nuevoPrecio);

                System.out.println("Precio modificado.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Dato no válido.");
        } catch (IOException e) {
            System.out.println("Error al modificar el fichero.");
        }
    }

    private static void eliminar(Scanner sc) {

        try {
            System.out.print("Código: ");
            int codigoBuscado = Integer.parseInt(sc.nextLine());

            if (codigoBuscado < 1) {
                System.out.println("Código no válido.");
                return;
            }

            try (RandomAccessFile raf = new RandomAccessFile(FICHERO, "rw")) {

                long posicion = (long) (codigoBuscado - 1) * TAM_REGISTRO;

                if (posicion >= raf.length()) {
                    System.out.println("El código no existe.");
                    return;
                }

                // BORRADO LÓGICO:
                // no borramos físicamente el registro.
                // Marcamos su código como -1.
                raf.seek(posicion);

                int codigo = raf.readInt();

                if (codigo == -1) {
                    System.out.println("El videojuego ya está eliminado.");
                    return;
                }

                String titulo = leerCadena(raf, LONG_TITULO);
                String plataforma = leerCadena(raf, LONG_PLATAFORMA);
                int anio = raf.readInt();
                float precio = raf.readFloat();

                mostrarRegistro(codigo, titulo, plataforma, anio, precio);

                System.out.print("¿Quieres eliminarlo? (S/N): ");
                String respuesta = sc.nextLine();

                if (respuesta.equalsIgnoreCase("S")) {

                    // BORRADO LÓGICO:
                    // el registro no desaparece físicamente.
                    // Su código pasa a -1 y después se ignora al leerlo.
                    raf.seek(posicion);
                    raf.writeInt(-1);
                    System.out.println("Videojuego eliminado.");
                } else {
                    System.out.println("Operación cancelada.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Código no válido.");
        } catch (IOException e) {
            System.out.println("Error al eliminar el videojuego.");
        }
    }

    private static void escribirRegistro(
            RandomAccessFile raf,
            int codigo,
            String titulo,
            String plataforma,
            int anio,
            float precio) throws IOException {

        raf.writeInt(codigo);
        escribirCadena(raf, titulo, LONG_TITULO);
        escribirCadena(raf, plataforma, LONG_PLATAFORMA);
        raf.writeInt(anio);
        raf.writeFloat(precio);
    }

    private static void escribirCadena(
            RandomAccessFile raf,
            String texto,
            int longitud) throws IOException {

        for (int i = 0; i < longitud; i++) {

            if (i < texto.length()) {
                raf.writeChar(texto.charAt(i));
            } else {
                raf.writeChar(' ');
            }
        }
    }

    private static String leerCadena(
            RandomAccessFile raf,
            int longitud) throws IOException {

        String texto = "";

        for (int i = 0; i < longitud; i++) {
            texto += raf.readChar();
        }

        return texto.trim();
    }

    private static void mostrarRegistro(
            int codigo,
            String titulo,
            String plataforma,
            int anio,
            float precio) {

        System.out.println("----------------------------");
        System.out.println("Código: " + codigo);
        System.out.println("Título: " + titulo);
        System.out.println("Plataforma: " + plataforma);
        System.out.println("Año: " + anio);
        System.out.printf("Precio: %.2f €%n", precio);
    }
}
