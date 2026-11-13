import java.io.IOException;          // Excepción para errores de lectura/escritura
import java.io.RandomAccessFile;     // Permite leer y escribir en cualquier posición del fichero
import java.util.Scanner;            // Permite pedir datos al usuario por teclado

/*
 * PRÁCTICA Nº2 - ACCESO A DATOS
 * Mantenimiento de un fichero binario de acceso directo.
 *
 * Fichero utilizado: videojuegos.dat
 *
 * Cada registro representa un videojuego y tiene un tamaño fijo:
 *
 * - Código        -> int      -> 4 bytes
 * - Título        -> 35 char  -> 70 bytes
 * - Plataforma    -> 15 char  -> 30 bytes
 * - Año           -> short    -> 2 bytes
 * - Precio        -> float    -> 4 bytes
 * - Multijugador  -> boolean  -> 1 byte
 * - Valoración    -> float    -> 4 bytes
 *
 * TOTAL:
 * 4 + 70 + 30 + 2 + 4 + 1 + 4 = 115 bytes por registro.
 *
 * Todos los registros deben ocupar exactamente lo mismo.
 * Gracias a esto podemos acceder directamente a cualquier registro:
 *
 * posicion = (codigo - 1) * TAMANO_REGISTRO
 *
 * Para eliminar un videojuego utilizamos borrado lógico:
 * no se elimina físicamente el registro del fichero, sino que
 * se cambia su código por -1.
 */

public class Practica2Videojuegos {

    // Nombre del fichero binario donde se guardarán los videojuegos.
    private static final String NOMBRE_FICHERO = "videojuegos.dat";

    /*
     * Las cadenas tienen una longitud fija porque los registros
     * también deben tener siempre un tamaño fijo.
     */
    private static final int LONGITUD_TITULO = 35;
    private static final int LONGITUD_PLATAFORMA = 15;

    // Tamaño total, en bytes, que ocupa cada videojuego.
    private static final int TAMANO_REGISTRO = 115;

    /*
     * Desplazamiento del campo precio dentro de cada registro.
     *
     * Antes del precio tenemos:
     *
     * Código      = 4 bytes
     * Título      = 70 bytes
     * Plataforma  = 30 bytes
     * Año         = 2 bytes
     *
     * 4 + 70 + 30 + 2 = 106 bytes
     *
     * Por tanto:
     * posicionPrecio = posicionRegistro + 106
     */
    private static final int DESPLAZAMIENTO_PRECIO = 106;

    public static void main(String[] args) {

        // Scanner se utiliza para leer las opciones y datos introducidos por teclado.
        Scanner scanner = new Scanner(System.in);

        // Inicializamos la opción para poder controlar posteriormente el bucle.
        int opcion = 0;

        /*
         * El menú se repite mientras el usuario no seleccione
         * la opción 8 (Salir).
         */
        do {

            mostrarMenu();

            try {

                /*
                 * nextLine() recoge lo introducido como String.
                 * trim() elimina espacios al principio y al final.
                 * parseInt() convierte el String a int.
                 */
                opcion = Integer.parseInt(scanner.nextLine().trim());

                // Ejecutamos una operación distinta según la opción elegida.
                switch (opcion) {

                    case 1:
                        anadir15Videojuegos();
                        break;

                    case 2:
                        anadirVideojuego(scanner);
                        break;

                    case 3:
                        visualizarTodos();
                        break;

                    case 4:
                        visualizarPorCodigo(scanner);
                        break;

                    case 5:
                        visualizarPorPlataforma(scanner);
                        break;

                    case 6:
                        modificarPrecio(scanner);
                        break;

                    case 7:
                        eliminarVideojuego(scanner);
                        break;

                    case 8:
                        System.out.println("Programa finalizado.");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {

                /*
                 * Se produce si el usuario escribe algo que
                 * no puede convertirse en número.
                 */
                System.out.println(
                        "Error: debes introducir un número válido."
                );
            }

            System.out.println();

        } while (opcion != 8);

        // Cerramos el Scanner cuando ya no se va a utilizar.
        scanner.close();
    }

    /*
     * Muestra el menú principal.
     *
     * Se separa en un método para no llenar el main()
     * de instrucciones de impresión.
     */
    private static void mostrarMenu() {

        System.out.println("==============================================");
        System.out.println("       GESTIÓN DE VIDEOJUEGOS");
        System.out.println("==============================================");
        System.out.println("1.- Añadir 15 videojuegos al fichero.");
        System.out.println("2.- Añadir un videojuego al final.");
        System.out.println("3.- Visualizar todos los videojuegos.");
        System.out.println("4.- Visualizar un videojuego por código.");
        System.out.println("5.- Visualizar videojuegos por plataforma.");
        System.out.println("6.- Modificar el precio de un videojuego.");
        System.out.println("7.- Eliminar un videojuego.");
        System.out.println("8.- Salir.");
        System.out.print("Elige una opción: ");
    }

    /*
     * ============================================================
     * OPCIÓN 1: AÑADIR 15 VIDEOJUEGOS
     * ============================================================
     *
     * El enunciado pide utilizar arrays con datos ya generados.
     * Cada posición de los arrays representa el mismo videojuego.
     *
     * Por ejemplo:
     *
     * titulos[0]       -> Minecraft
     * plataformas[0]   -> PC
     * anios[0]         -> 2011
     * precios[0]       -> 29.99
     *
     * Todos esos datos pertenecen al videojuego número 0 del array.
     */
    private static void anadir15Videojuegos() {

        String[] titulos = {
                "Minecraft",
                "The Witcher 3",
                "Grand Theft Auto V",
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
                "PC",
                "PC",
                "PS5",
                "PS5",
                "PC",
                "PS5",
                "PC",
                "PC",
                "PC",
                "PC",
                "Xbox",
                "PS5",
                "PC",
                "PS5",
                "PC"
        };

        short[] anios = {
                2011, 2015, 2013, 2018, 2020,
                2018, 2022, 2020, 2017, 2020,
                2021, 2023, 2023, 2018, 2016
        };

        /*
         * La f detrás del número indica que el valor
         * debe almacenarse como float.
         */
        float[] precios = {
                29.99f, 39.99f, 19.99f, 59.99f, 49.99f,
                39.99f, 59.99f, 24.99f, 14.99f, 39.99f,
                59.99f, 49.99f, 59.99f, 49.99f, 39.99f
        };

        boolean[] multijugador = {
                true, false, true, true, false,
                false, true, false, false, true,
                true, false, true, false, true
        };

        float[] valoraciones = {
                9.5f, 9.7f, 9.4f, 9.8f, 8.8f,
                9.3f, 9.6f, 9.1f, 9.2f, 8.9f,
                9.0f, 9.4f, 9.7f, 9.2f, 9.3f
        };

        /*
         * Abrimos el fichero en modo "rw":
         *
         * r = read  -> lectura
         * w = write -> escritura
         *
         * Si videojuegos.dat no existe, RandomAccessFile lo crea.
         *
         * try-with-resources cerrará automáticamente el fichero
         * cuando terminemos de utilizarlo.
         */
        try (RandomAccessFile raf =
                     new RandomAccessFile(NOMBRE_FICHERO, "rw")) {

            /*
             * length() devuelve el tamaño del fichero en bytes.
             *
             * seek() mueve el puntero del fichero.
             *
             * Como queremos AÑADIR datos y no sobrescribirlos,
             * movemos el puntero exactamente al final.
             */
            raf.seek(raf.length());

            /*
             * Calculamos el código que debe recibir el siguiente registro.
             *
             * Ejemplo:
             *
             * 1725 bytes / 115 bytes = 15 registros
             * 15 + 1 = código 16
             */
            int siguienteCodigo =
                    (int) (raf.length() / TAMANO_REGISTRO) + 1;

            /*
             * Recorremos los 15 elementos de los arrays.
             */
            for (int i = 0; i < titulos.length; i++) {

                // Cada nuevo registro recibe un código consecutivo.
                int codigo = siguienteCodigo + i;

                /*
                 * Escribimos todos los campos del videojuego.
                 * Se utiliza un método para evitar repetir el mismo
                 * código de escritura.
                 */
                escribirRegistro(
                        raf,
                        codigo,
                        titulos[i],
                        plataformas[i],
                        anios[i],
                        precios[i],
                        multijugador[i],
                        valoraciones[i]
                );
            }

            System.out.println();
            System.out.println("15 videojuegos añadidos correctamente.");

            // Mostramos el tamaño físico final del fichero.
            System.out.println(
                    "Tamaño actual del fichero: "
                            + raf.length()
                            + " bytes."
            );

        } catch (IOException e) {

            // Captura posibles errores al trabajar con el fichero.
            System.out.println(
                    "Error al escribir el fichero: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * OPCIÓN 2: AÑADIR UN VIDEOJUEGO
     * ============================================================
     *
     * Pide los datos por teclado y añade un registro al final.
     */
    private static void anadirVideojuego(Scanner scanner) {

        try {

            System.out.print("Introduce el título: ");
            String titulo = scanner.nextLine();

            System.out.print("Introduce la plataforma: ");
            String plataforma = scanner.nextLine();

            System.out.print("Introduce el año: ");

            // Convertimos el texto introducido a short.
            short anio =
                    Short.parseShort(scanner.nextLine().trim());

            System.out.print("Introduce el precio: ");

            /*
             * replace(',', '.') permite introducir:
             *
             * 29,99
             *
             * o:
             *
             * 29.99
             */
            float precio =
                    Float.parseFloat(
                            scanner.nextLine()
                                    .trim()
                                    .replace(',', '.')
                    );

            System.out.print(
                    "¿Tiene multijugador? (S/N): "
            );

            String respuestaMultijugador =
                    scanner.nextLine().trim();

            /*
             * equalsIgnoreCase ignora mayúsculas/minúsculas.
             *
             * "S", "s" -> true
             * cualquier otra respuesta -> false
             */
            boolean multijugador =
                    respuestaMultijugador.equalsIgnoreCase("S");

            System.out.print(
                    "Introduce la valoración (0-10): "
            );

            float valoracion =
                    Float.parseFloat(
                            scanner.nextLine()
                                    .trim()
                                    .replace(',', '.')
                    );

            try (RandomAccessFile raf =
                         new RandomAccessFile(
                                 NOMBRE_FICHERO,
                                 "rw")) {

                /*
                 * Calculamos el código a partir del número
                 * de registros físicos existentes.
                 */
                int codigo =
                        (int) (
                                raf.length()
                                        / TAMANO_REGISTRO
                        ) + 1;

                // Movemos el puntero al final del fichero.
                raf.seek(raf.length());

                // Escribimos el nuevo registro completo.
                escribirRegistro(
                        raf,
                        codigo,
                        titulo,
                        plataforma,
                        anio,
                        precio,
                        multijugador,
                        valoracion
                );

                System.out.println();
                System.out.println(
                        "Videojuego añadido correctamente."
                );

                System.out.println(
                        "Código asignado: " + codigo
                );
            }

        } catch (NumberFormatException e) {

            /*
             * Este error se produce si, por ejemplo,
             * introducimos texto donde se esperaba un precio o un año.
             */
            System.out.println(
                    "Error: alguno de los datos numéricos no es válido."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al añadir el videojuego: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * OPCIÓN 3: VISUALIZAR TODOS
     * ============================================================
     *
     * Aquí hacemos una lectura SECUENCIAL.
     *
     * Empezamos al principio y recorremos uno por uno
     * todos los registros hasta llegar al final.
     */
    private static void visualizarTodos() {

        /*
         * "r" significa que abrimos el fichero únicamente
         * para lectura.
         */
        try (RandomAccessFile raf =
                     new RandomAccessFile(
                             NOMBRE_FICHERO,
                             "r")) {

            // Un fichero con longitud 0 no contiene registros.
            if (raf.length() == 0) {

                System.out.println(
                        "El fichero está vacío."
                );

                return;
            }

            int encontrados = 0;

            /*
             * getFilePointer() devuelve la posición actual
             * del puntero dentro del fichero.
             *
             * Seguimos leyendo mientras no lleguemos
             * al final del fichero.
             */
            while (
                    raf.getFilePointer()
                            < raf.length()
            ) {

                /*
                 * Los campos deben leerse EXACTAMENTE
                 * en el mismo orden en que fueron escritos.
                 */

                int codigo = raf.readInt();

                String titulo =
                        leerCadenaFija(
                                raf,
                                LONGITUD_TITULO
                        );

                String plataforma =
                        leerCadenaFija(
                                raf,
                                LONGITUD_PLATAFORMA
                        );

                short anio = raf.readShort();

                float precio = raf.readFloat();

                boolean multijugador =
                        raf.readBoolean();

                float valoracion =
                        raf.readFloat();

                /*
                 * Un código -1 significa que ese registro
                 * fue eliminado lógicamente.
                 *
                 * Por tanto, no lo mostramos.
                 */
                if (codigo != -1) {

                    mostrarVideojuego(
                            codigo,
                            titulo,
                            plataforma,
                            anio,
                            precio,
                            multijugador,
                            valoracion
                    );

                    encontrados++;
                }
            }

            if (encontrados == 0) {

                System.out.println(
                        "No hay videojuegos activos en el fichero."
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error al leer el fichero: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * OPCIÓN 4: BUSCAR POR CÓDIGO
     * ============================================================
     *
     * Aquí aprovechamos realmente el ACCESO DIRECTO.
     *
     * No recorremos todos los registros.
     * Calculamos directamente la posición que corresponde al código.
     */
    private static void visualizarPorCodigo(
            Scanner scanner) {

        try {

            System.out.print(
                    "Introduce el código del videojuego: "
            );

            int codigoBuscado =
                    Integer.parseInt(
                            scanner.nextLine().trim()
                    );

            // Los códigos válidos empiezan en 1.
            if (codigoBuscado < 1) {

                System.out.println(
                        "Código no válido."
                );

                return;
            }

            try (RandomAccessFile raf =
                         new RandomAccessFile(
                                 NOMBRE_FICHERO,
                                 "r")) {

                /*
                 * Fórmula fundamental del acceso directo:
                 *
                 * posición = (código - 1) * 115
                 *
                 * Ejemplo para código 5:
                 *
                 * (5 - 1) * 115 = 460
                 */
                long posicion =
                        (long) (codigoBuscado - 1)
                                * TAMANO_REGISTRO;

                /*
                 * Si esa posición está fuera del fichero,
                 * el registro no puede existir.
                 */
                if (posicion >= raf.length()) {

                    System.out.println(
                            "Error: el código no existe."
                    );

                    return;
                }

                // Saltamos directamente hasta el registro.
                raf.seek(posicion);

                int codigo = raf.readInt();

                // Comprobamos el borrado lógico.
                if (codigo == -1) {

                    System.out.println(
                            "El videojuego con código "
                                    + codigoBuscado
                                    + " está eliminado."
                    );

                    return;
                }

                /*
                 * Una vez encontrados los primeros 4 bytes
                 * del código, leemos el resto del registro.
                 */
                String titulo =
                        leerCadenaFija(
                                raf,
                                LONGITUD_TITULO
                        );

                String plataforma =
                        leerCadenaFija(
                                raf,
                                LONGITUD_PLATAFORMA
                        );

                short anio = raf.readShort();
                float precio = raf.readFloat();

                boolean multijugador =
                        raf.readBoolean();

                float valoracion =
                        raf.readFloat();

                mostrarVideojuego(
                        codigo,
                        titulo,
                        plataforma,
                        anio,
                        precio,
                        multijugador,
                        valoracion
                );
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Error: debes introducir un código numérico."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al consultar el fichero: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * OPCIÓN 5: BUSCAR POR PLATAFORMA
     * ============================================================
     *
     * Aquí no conocemos directamente la posición de los registros
     * que tienen una determinada plataforma.
     *
     * Por eso tenemos que recorrer todo el fichero secuencialmente.
     */
    private static void visualizarPorPlataforma(
            Scanner scanner) {

        System.out.print(
                "Introduce la plataforma a buscar: "
        );

        String plataformaBuscada =
                scanner.nextLine().trim();

        try (RandomAccessFile raf =
                     new RandomAccessFile(
                             NOMBRE_FICHERO,
                             "r")) {

            int encontrados = 0;

            while (
                    raf.getFilePointer()
                            < raf.length()
            ) {

                int codigo = raf.readInt();

                String titulo =
                        leerCadenaFija(
                                raf,
                                LONGITUD_TITULO
                        );

                String plataforma =
                        leerCadenaFija(
                                raf,
                                LONGITUD_PLATAFORMA
                        );

                short anio = raf.readShort();
                float precio = raf.readFloat();

                boolean multijugador =
                        raf.readBoolean();

                float valoracion =
                        raf.readFloat();

                /*
                 * El videojuego se muestra únicamente si:
                 *
                 * 1. No está borrado.
                 * 2. Su plataforma coincide con la buscada.
                 */
                if (
                        codigo != -1
                                &&
                        plataforma.equalsIgnoreCase(
                                plataformaBuscada
                        )
                ) {

                    mostrarVideojuego(
                            codigo,
                            titulo,
                            plataforma,
                            anio,
                            precio,
                            multijugador,
                            valoracion
                    );

                    encontrados++;
                }
            }

            if (encontrados == 0) {

                System.out.println(
                        "No se han encontrado videojuegos "
                                + "para la plataforma "
                                + plataformaBuscada
                                + "."
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Error al consultar el fichero: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * OPCIÓN 6: MODIFICAR PRECIO
     * ============================================================
     *
     * Modificamos únicamente los 4 bytes correspondientes
     * al precio.
     *
     * No es necesario volver a escribir todo el registro.
     */
    private static void modificarPrecio(
            Scanner scanner) {

        try {

            System.out.print(
                    "Introduce el código del videojuego: "
            );

            int codigoBuscado =
                    Integer.parseInt(
                            scanner.nextLine().trim()
                    );

            if (codigoBuscado < 1) {

                System.out.println(
                        "Código no válido."
                );

                return;
            }

            try (RandomAccessFile raf =
                         new RandomAccessFile(
                                 NOMBRE_FICHERO,
                                 "rw")) {

                /*
                 * Primero calculamos dónde empieza
                 * el registro completo.
                 */
                long posicionRegistro =
                        (long) (codigoBuscado - 1)
                                * TAMANO_REGISTRO;

                if (
                        posicionRegistro
                                >= raf.length()
                ) {

                    System.out.println(
                            "Error: el código no existe."
                    );

                    return;
                }

                // Saltamos al comienzo del registro.
                raf.seek(posicionRegistro);

                int codigo = raf.readInt();

                if (codigo == -1) {

                    System.out.println(
                            "El videojuego está eliminado."
                    );

                    return;
                }

                /*
                 * Leemos los datos actuales para mostrarlos
                 * antes de realizar la modificación.
                 */
                String titulo =
                        leerCadenaFija(
                                raf,
                                LONGITUD_TITULO
                        );

                String plataforma =
                        leerCadenaFija(
                                raf,
                                LONGITUD_PLATAFORMA
                        );

                short anio = raf.readShort();

                float precioActual =
                        raf.readFloat();

                boolean multijugador =
                        raf.readBoolean();

                float valoracion =
                        raf.readFloat();

                mostrarVideojuego(
                        codigo,
                        titulo,
                        plataforma,
                        anio,
                        precioActual,
                        multijugador,
                        valoracion
                );

                System.out.print(
                        "Introduce el nuevo precio: "
                );

                float nuevoPrecio =
                        Float.parseFloat(
                                scanner.nextLine()
                                        .trim()
                                        .replace(',', '.')
                        );

                /*
                 * Posición exacta del precio:
                 *
                 * inicio del registro + 106 bytes
                 *
                 * De esta forma sobrescribimos únicamente
                 * los 4 bytes del float precio.
                 */
                raf.seek(
                        posicionRegistro
                                + DESPLAZAMIENTO_PRECIO
                );

                raf.writeFloat(nuevoPrecio);

                System.out.println(
                        "Precio modificado correctamente."
                );
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Error: dato numérico no válido."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al modificar el fichero: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * OPCIÓN 7: ELIMINAR VIDEOJUEGO
     * ============================================================
     *
     * Realizamos un BORRADO LÓGICO.
     *
     * No eliminamos físicamente los 115 bytes.
     * Solamente sustituimos el código por -1.
     */
    private static void eliminarVideojuego(
            Scanner scanner) {

        try {

            System.out.print(
                    "Introduce el código del videojuego "
                            + "que quieres eliminar: "
            );

            int codigoBuscado =
                    Integer.parseInt(
                            scanner.nextLine().trim()
                    );

            if (codigoBuscado < 1) {

                System.out.println(
                        "Código no válido."
                );

                return;
            }

            try (RandomAccessFile raf =
                         new RandomAccessFile(
                                 NOMBRE_FICHERO,
                                 "rw")) {

                // Calculamos directamente dónde comienza.
                long posicionRegistro =
                        (long) (codigoBuscado - 1)
                                * TAMANO_REGISTRO;

                if (
                        posicionRegistro
                                >= raf.length()
                ) {

                    System.out.println(
                            "Error: el código no existe."
                    );

                    return;
                }

                raf.seek(posicionRegistro);

                int codigo = raf.readInt();

                // Evitamos eliminar dos veces el mismo registro.
                if (codigo == -1) {

                    System.out.println(
                            "El videojuego ya estaba eliminado."
                    );

                    return;
                }

                /*
                 * Leemos el resto del registro para mostrárselo
                 * al usuario antes de confirmar el borrado.
                 */
                String titulo =
                        leerCadenaFija(
                                raf,
                                LONGITUD_TITULO
                        );

                String plataforma =
                        leerCadenaFija(
                                raf,
                                LONGITUD_PLATAFORMA
                        );

                short anio = raf.readShort();
                float precio = raf.readFloat();

                boolean multijugador =
                        raf.readBoolean();

                float valoracion =
                        raf.readFloat();

                mostrarVideojuego(
                        codigo,
                        titulo,
                        plataforma,
                        anio,
                        precio,
                        multijugador,
                        valoracion
                );

                System.out.print(
                        "¿Seguro que deseas eliminarlo? (S/N): "
                );

                String respuesta =
                        scanner.nextLine().trim();

                if (
                        respuesta.equalsIgnoreCase("S")
                ) {

                    /*
                     * Después de haber leído el registro,
                     * el puntero está al final.
                     *
                     * Volvemos al comienzo del registro.
                     */
                    raf.seek(posicionRegistro);

                    /*
                     * Sobrescribimos únicamente el código.
                     *
                     * Como int ocupa 4 bytes, solo cambiamos
                     * los primeros 4 bytes del registro.
                     */
                    raf.writeInt(-1);

                    System.out.println(
                            "Videojuego eliminado correctamente."
                    );

                } else {

                    System.out.println(
                            "Operación cancelada."
                    );
                }
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Error: debes introducir un código numérico."
            );

        } catch (IOException e) {

            System.out.println(
                    "Error al eliminar el videojuego: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ============================================================
     * MÉTODO AUXILIAR: ESCRIBIR UN REGISTRO
     * ============================================================
     *
     * Centraliza la escritura de un videojuego completo.
     *
     * MUY IMPORTANTE:
     *
     * El orden de escritura debe ser siempre el mismo
     * que utilizaremos después para leer.
     */
    private static void escribirRegistro(
            RandomAccessFile raf,
            int codigo,
            String titulo,
            String plataforma,
            short anio,
            float precio,
            boolean multijugador,
            float valoracion) throws IOException {

        // int ocupa 4 bytes.
        raf.writeInt(codigo);

        // 35 caracteres x 2 bytes = 70 bytes.
        escribirCadenaFija(
                raf,
                titulo,
                LONGITUD_TITULO
        );

        // 15 caracteres x 2 bytes = 30 bytes.
        escribirCadenaFija(
                raf,
                plataforma,
                LONGITUD_PLATAFORMA
        );

        // short ocupa 2 bytes.
        raf.writeShort(anio);

        // float ocupa 4 bytes.
        raf.writeFloat(precio);

        // boolean ocupa 1 byte.
        raf.writeBoolean(multijugador);

        // float ocupa otros 4 bytes.
        raf.writeFloat(valoracion);
    }

    /*
     * ============================================================
     * MÉTODO AUXILIAR: ESCRIBIR CADENA FIJA
     * ============================================================
     *
     * RandomAccessFile no sabe que queremos reservar siempre
     * 35 caracteres para el título o 15 para la plataforma.
     *
     * Este método fuerza esa longitud.
     */
    private static void escribirCadenaFija(
            RandomAccessFile raf,
            String texto,
            int longitud) throws IOException {

        /*
         * El operador ternario:
         *
         * texto != null ? texto : ""
         *
         * significa:
         *
         * si texto no es null -> utiliza texto
         * si texto es null    -> utiliza cadena vacía
         */
        StringBuffer buffer =
                new StringBuffer(
                        texto != null ? texto : ""
                );

        /*
         * setLength fuerza la longitud.
         *
         * Si el texto es más largo, lo recorta.
         * Si es más corto, lo completa.
         */
        buffer.setLength(longitud);

        /*
         * writeChars escribe cada carácter utilizando 2 bytes.
         */
        raf.writeChars(
                buffer.toString()
        );
    }

    /*
     * ============================================================
     * MÉTODO AUXILIAR: LEER CADENA FIJA
     * ============================================================
     *
     * Hace la operación contraria a escribirCadenaFija().
     */
    private static String leerCadenaFija(
            RandomAccessFile raf,
            int longitud) throws IOException {

        // Creamos espacio para el número exacto de caracteres.
        char[] caracteres =
                new char[longitud];

        /*
         * Leemos exactamente la misma cantidad de caracteres
         * que escribimos anteriormente.
         */
        for (
                int i = 0;
                i < longitud;
                i++
        ) {

            caracteres[i] =
                    raf.readChar();
        }

        /*
         * Convertimos el array de char en String.
         *
         * trim() elimina el relleno sobrante de la cadena.
         */
        return new String(caracteres).trim();
    }

    /*
     * ============================================================
     * MÉTODO AUXILIAR: MOSTRAR VIDEOJUEGO
     * ============================================================
     *
     * Evita repetir el mismo código de impresión
     * en todas las consultas.
     */
    private static void mostrarVideojuego(
            int codigo,
            String titulo,
            String plataforma,
            short anio,
            float precio,
            boolean multijugador,
            float valoracion) {

        System.out.println();
        System.out.println(
                "----------------------------------------------"
        );

        System.out.println(
                "Código       : " + codigo
        );

        System.out.println(
                "Título       : " + titulo
        );

        System.out.println(
                "Plataforma   : " + plataforma
        );

        System.out.println(
                "Año          : " + anio
        );

        // %.2f muestra el float con dos decimales.
        System.out.printf(
                "Precio       : %.2f EUR%n",
                precio
        );

        /*
         * Operador ternario:
         *
         * condición ? valorSiTrue : valorSiFalse
         */
        System.out.println(
                "Multijugador : "
                        + (multijugador ? "Sí" : "No")
        );

        // %.1f muestra la valoración con un decimal.
        System.out.printf(
                "Valoración   : %.1f / 10%n",
                valoracion
        );

        System.out.println(
                "----------------------------------------------"
        );
    }
}