import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

/*
 * ============================================================
 * PRÁCTICA Nº3 - ACCESO A DATOS
 * XML, DOM, XStream, XPath y XSLT
 * ============================================================
 *
 * COMPILAR EN WINDOWS:
 *
 * javac -cp ".;lib/*" Videojuego.java GenerarDatosIniciales.java Practica3Videojuegos.java
 *
 * GENERAR EL FICHERO BINARIO INICIAL:
 *
 * java -cp ".;lib/*" GenerarDatosIniciales
 *
 * EJECUTAR:
 *
 * java -cp ".;lib/*" Practica3Videojuegos
 *
 * ============================================================
 * ¿DE DÓNDE SALEN LOS DATOS?
 * ============================================================
 *
 * GenerarDatosIniciales.java crea:
 *
 * videojuegos.dat
 *
 * El fichero contiene 15 videojuegos.
 * Cada registro ocupa exactamente 173 bytes.
 *
 * Después Practica3Videojuegos utiliza videojuegos.dat
 * como fichero de partida.
 *
 * ============================================================
 * TIPO DE FICHERO Y TIPO DE ACCESO
 * ============================================================
 *
 * videojuegos.dat:
 *
 * - Es un fichero BINARIO.
 * - Tiene registros de tamaño fijo.
 * - Se utiliza RandomAccessFile.
 *
 * RandomAccessFile permite ACCESO DIRECTO porque puede mover
 * el puntero a una posición concreta mediante seek().
 *
 * IMPORTANTE:
 * En la opción 1 NO utilizamos seek().
 * Recorremos el fichero desde el principio hasta el final.
 *
 * Por tanto:
 *
 * El fichero permite acceso directo, pero en la opción 1
 * hacemos una LECTURA SECUENCIAL del fichero binario.
 *
 * ============================================================
 * FLUJO PRINCIPAL
 * ============================================================
 *
 * videojuegos.dat
 *      |
 *      | RandomAccessFile
 *      v
 *     DOM
 *      |
 *      v
 * videojuegos.xml
 *
 *
 * Objeto Videojuego
 *      |
 *      | XStream.toXML()
 *      v
 *     XML
 *      |
 *      | XStream.fromXML()
 *      v
 * Objeto Videojuego
 *
 *
 * videojuegos_masivo.xml
 *      |
 *      v
 *    XPath
 *
 *
 * videojuegos_masivo.xml + videojuegos.xsl
 *      |
 *      | XSLT
 *      v
 * videojuegos.html
 *
 * ============================================================
 * CLASES IMPORTANTES
 * ============================================================
 *
 * File:
 * Representa un fichero o una ruta.
 * NO lee ni escribe el contenido por sí solo.
 *
 *
 * RandomAccessFile:
 * Permite trabajar con ficheros binarios.
 * Puede leer, escribir y mover el puntero.
 *
 * RandomAccessFile = CLASE
 * raf              = VARIABLE
 *
 *
 * Document:
 * Representa TODO el documento XML como un árbol DOM
 * almacenado en memoria.
 *
 * Document = CLASE
 * doc      = VARIABLE
 *
 *
 * Element:
 * Representa una etiqueta o elemento XML.
 *
 * Ejemplo:
 *
 * <videojuego codigo="1">
 *     <titulo>Minecraft</titulo>
 * </videojuego>
 *
 * codigo = atributo
 * titulo = elemento hijo
 *
 *
 * Node:
 * Representa un nodo genérico del árbol DOM.
 *
 *
 * NodeList:
 * Representa una lista o colección de nodos.
 *
 *
 * DocumentBuilder:
 *
 * newDocument() -> crea un XML NUEVO en memoria.
 * parse()       -> carga un XML EXISTENTE en memoria.
 *
 *
 * XStream:
 * Librería externa para convertir objetos Java a XML
 * y XML de nuevo a objetos.
 *
 * toXML()   -> SERIALIZAR: objeto Java -> XML.
 * fromXML() -> DESERIALIZAR: XML -> objeto Java.
 *
 *
 * XPath:
 * Permite consultar y filtrar información de un XML.
 *
 *
 * Transformer:
 *
 * - Guarda un árbol DOM en un fichero XML.
 * - También puede aplicar XSLT para transformar XML en HTML.
 *
 * ============================================================
 * ATRIBUTOS XML
 * ============================================================
 *
 * Ejemplo:
 *
 * <videojuego codigo="1" desarrolladora="3">
 *
 * codigo y desarrolladora son ATRIBUTOS.
 *
 * Para añadir:
 *
 * elemento.setAttribute("codigo", "1");
 *
 * Para leer:
 *
 * elemento.getAttribute("codigo");
 *
 * ============================================================
 * REGISTRO BINARIO: 173 BYTES
 * ============================================================
 *
 * Código                  int       4 bytes
 * Título                  40 char  80 bytes
 * Plataforma              15 char  30 bytes
 * Año                     short     2 bytes
 * Precio                  float     4 bytes
 * Multijugador            boolean   1 byte
 * Valoración              float     4 bytes
 * Código desarrolladora   short     2 bytes
 * Género                  20 char  40 bytes
 * PEGI                    short     2 bytes
 * Ventas                  int       4 bytes
 *
 * TOTAL = 173 bytes
 *
 * ============================================================
 */

public class Practica3Videojuegos {

    private static final String BINARIO = "videojuegos.dat";
    private static final String XML = "videojuegos.xml";
    private static final String XML_MASIVO = "videojuegos_masivo.xml";
    private static final String XML_XSTREAM = "videojuego_xstream.xml";
    private static final String XSL = "videojuegos.xsl";
    private static final String HTML = "videojuegos.html";

    private static final int LONG_TITULO = 40;
    private static final int LONG_PLATAFORMA = 15;
    private static final int LONG_GENERO = 20;
    private static final int TAM_REGISTRO = 173;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        // El menú se repite hasta elegir la opción 8.
        do {

            mostrarMenu();

            try {

                opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1 -> generarXmlDesdeBinario();
                    case 2 -> modificarPrecioXml(sc);
                    case 3 -> eliminarVideojuegoXml(sc);
                    case 4 -> serializarConXStream();
                    case 5 -> generarXmlMasivo();
                    case 6 -> consultarConXPath(sc);
                    case 7 -> generarHtmlConXslt();
                    case 8 -> System.out.println("Programa finalizado.");
                    default -> System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un número.");
            }

            System.out.println();

        } while (opcion != 8);

        sc.close();
    }

    private static void mostrarMenu() {

        System.out.println("===== GESTIÓN AVANZADA DE VIDEOJUEGOS =====");
        System.out.println("1. Generar XML desde videojuegos.dat");
        System.out.println("2. Modificar precio en el XML");
        System.out.println("3. Eliminar videojuego del XML");
        System.out.println("4. Serializar/deserializar con XStream");
        System.out.println("5. Generar XML masivo (6 desarrolladoras / 100 juegos)");
        System.out.println("6. Consultar con XPath");
        System.out.println("7. Generar HTML mediante XSLT");
        System.out.println("8. Salir");
        System.out.print("Opción: ");
    }

    // ============================================================
    // OPCIÓN 1
    // BINARIO -> DOM -> XML
    // ============================================================

    private static void generarXmlDesdeBinario() {

        // File representa el fichero o su ruta.
        // NO lee el contenido.
        File fichero = new File(BINARIO);

        if (!fichero.exists()) {
            System.out.println(
                    "No existe " + BINARIO
                    + ". Ejecuta antes GenerarDatosIniciales."
            );
            return;
        }

        try {

            // Document representa TODO el XML en memoria.
            // Document es la clase y doc es la variable.
            // Document es la clase que representa todo el documento XML como un árbol DOM en memoria. doc es la variable que referencia ese documento.
            // Document → clase 
            // doc → variable
            Document doc = nuevoDocumento();

            // createElement() crea una etiqueta XML.
            Element raiz = doc.createElement("videojuegos");

            // appendChild() añade el elemento al árbol DOM. -- DOM representa el XML como un árbol de nodos en memoria, permitiendo crear, buscar, modificar y eliminar elementos.
            doc.appendChild(raiz);

            // RandomAccessFile es la clase.
            // raf es la variable.
            // "r" significa solo lectura.
            try (RandomAccessFile raf =
                    new RandomAccessFile(fichero, "r")) {

                // Comprobamos que el fichero contenga
                // registros completos de 173 bytes.
                if (raf.length() % TAM_REGISTRO != 0) {
                    System.out.println(
                            "El fichero no contiene registros completos de 173 bytes."
                    );
                    return;
                }

                /*
                 * LECTURA SECUENCIAL:
                 *
                 * Aunque RandomAccessFile permite acceso directo,
                 * aquí recorremos el fichero desde el principio
                 * hasta el final.
                 *
                 * getFilePointer() -> posición actual del puntero.
                 * length()         -> tamaño total del fichero.
                 */
                while (raf.getFilePointer() < raf.length()) {

                    /*
                     * Los datos deben leerse en el MISMO ORDEN
                     * y con el MISMO TIPO con el que se escribieron.
                     */
                    int codigo = raf.readInt();

                    String titulo =
                            leerCadena(raf, LONG_TITULO);

                    String plataforma =
                            leerCadena(raf, LONG_PLATAFORMA);

                    short anio = raf.readShort();
                    float precio = raf.readFloat();
                    boolean multijugador = raf.readBoolean();
                    float valoracion = raf.readFloat();
                    short desarrolladora = raf.readShort();

                    String genero =
                            leerCadena(raf, LONG_GENERO);

                    short pegi = raf.readShort();
                    int ventas = raf.readInt();

                    /*
                     * BORRADO LÓGICO:
                     *
                     * Si codigo == -1, el registro sigue existiendo
                     * físicamente, pero se considera eliminado.
                     */
                    if (codigo == -1) {
                        continue;
                    }

                    // Creamos <videojuego>.
                    Element juego =
                            doc.createElement("videojuego");

                    /*
                     * Añadimos ATRIBUTOS.
                     *
                     * Ejemplo:
                     *
                     * <videojuego codigo="1" desarrolladora="3">
                     */
                    juego.setAttribute(
                            "codigo",
                            String.valueOf(codigo)
                    );

                    juego.setAttribute(
                            "desarrolladora",
                            String.valueOf(desarrolladora)
                    );

                    // Añadimos elementos hijos.
                    agregarElemento(
                            doc, juego, "titulo", titulo
                    );

                    agregarElemento(
                            doc, juego, "plataforma", plataforma
                    );

                    agregarElemento(
                            doc, juego, "anio",
                            String.valueOf(anio)
                    );

                    agregarElemento(
                            doc, juego, "precio",
                            String.valueOf(precio)
                    );

                    agregarElemento(
                            doc, juego, "multijugador",
                            multijugador ? "S" : "N"
                    );

                    agregarElemento(
                            doc, juego, "valoracion",
                            String.valueOf(valoracion)
                    );

                    agregarElemento(
                            doc, juego, "genero", genero
                    );

                    agregarElemento(
                            doc, juego, "pegi",
                            String.valueOf(pegi)
                    );

                    agregarElemento(
                            doc, juego, "ventas",
                            String.valueOf(ventas)
                    );

                    // Añadimos <videojuego> a <videojuegos>.
                    raiz.appendChild(juego);
                }
            }

            // Transformer guarda el DOM en un XML físico.
            guardarXml(doc, new File(XML));

            System.out.println(
                    XML + " generado correctamente."
            );

        } catch (Exception e) {
            System.out.println(
                    "Error al generar XML: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // OPCIÓN 2
    // MODIFICAR PRECIO EN XML
    // ============================================================

    private static void modificarPrecioXml(Scanner sc) {

        if (!new File(XML).exists()) {
            System.out.println(
                    "Primero genera " + XML
                    + " con la opción 1."
            );
            return;
        }

        System.out.print(
                "Código del videojuego: "
        );

        String codigo = sc.nextLine();

        try {

            /*
             * cargarXml() utiliza parse().
             *
             * parse() carga videojuegos.xml
             * como árbol DOM en memoria.
             */
            Document doc = cargarXml(XML);

            Element juego =
                    buscarJuegoPorCodigo(doc, codigo);

            if (juego == null) {
                System.out.println(
                        "Código no encontrado."
                );
                return;
            }

            /*
             * Buscamos el nodo:
             *
             * <precio>...</precio>
             */
            Node precio =
                    juego.getElementsByTagName("precio")
                            .item(0);

            System.out.println(
                    "Título: "
                    + texto(juego, "titulo")
            );

            System.out.println(
                    "Precio actual: "
                    + precio.getTextContent()
            );

            System.out.print(
                    "Nuevo precio: "
            );

            float nuevoPrecio =
                    Float.parseFloat(
                            sc.nextLine().replace(',', '.')
                    );

            /*
             * ACTUALIZACIÓN:
             *
             * setTextContent() cambia el contenido
             * del nodo <precio> en memoria.
             */
            precio.setTextContent(
                    String.valueOf(nuevoPrecio)
            );

            // Guardamos nuevamente el XML en disco.
            guardarXml(
                    doc,
                    new File(XML)
            );

            System.out.println(
                    "Precio modificado."
            );

        } catch (Exception e) {
            System.out.println(
                    "Error al modificar XML: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // OPCIÓN 3
    // ELIMINAR VIDEOJUEGO DEL XML
    // ============================================================

    private static void eliminarVideojuegoXml(Scanner sc) {

        if (!new File(XML).exists()) {
            System.out.println(
                    "Primero genera " + XML
                    + " con la opción 1."
            );
            return;
        }

        System.out.print(
                "Código a eliminar: "
        );

        String codigo = sc.nextLine();

        try {

            Document doc =
                    cargarXml(XML);

            Element juego =
                    buscarJuegoPorCodigo(doc, codigo);

            if (juego == null) {
                System.out.println(
                        "Código no encontrado."
                );
                return;
            }

            System.out.println(
                    "Título: "
                    + texto(juego, "titulo")
            );

            System.out.print(
                    "¿Eliminar? (S/N): "
            );

            if (sc.nextLine().equalsIgnoreCase("S")) {

                /*
                 * BORRADO XML:
                 *
                 * getParentNode() -> obtiene el nodo padre.
                 *
                 * removeChild() -> elimina completamente
                 * el nodo <videojuego>.
                 *
                 * DIFERENCIA:
                 *
                 * Práctica 2 -> borrado lógico: codigo = -1.
                 * Práctica 3 -> borrado físico del nodo XML.
                 */
                juego.getParentNode()
                        .removeChild(juego);

                guardarXml(
                        doc,
                        new File(XML)
                );

                System.out.println(
                        "Videojuego eliminado del XML."
                );

            } else {
                System.out.println(
                        "Operación cancelada."
                );
            }

        } catch (Exception e) {
            System.out.println(
                    "Error al eliminar: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // OPCIÓN 4
    // XSTREAM: SERIALIZAR / DESERIALIZAR
    // ============================================================

    private static void serializarConXStream() {

        try {

            // Creamos un objeto Java en memoria.
            Videojuego original =
                    new Videojuego(
                            1001,
                            "The Last of Us",
                            "PS5",
                            (short) 2022,
                            49.99f,
                            false,
                            9.6f,
                            (short) 4,
                            "Aventura",
                            (short) 18,
                            20000000
                    );

            /*
             * XStream convierte objetos Java
             * a XML y XML de nuevo a objetos.
             *
             * KXml2Driver es el driver XML pedido
             * para trabajar con XStream.
             */
            XStream xstream =
                    new XStream(
                            new KXml2Driver()
                    );

            // Permitimos trabajar con Videojuego.
            xstream.allowTypes(
                    new Class[]{Videojuego.class}
            );

            /*
             * Indicamos que la etiqueta principal
             * del XML se llamará <videojuego>.
             */
            xstream.alias(
                    "videojuego",
                    Videojuego.class
            );

            /*
             * SERIALIZACIÓN:
             *
             * objeto Java -> XML
             */
            String xml =
                    xstream.toXML(original);

            // Guardamos el XML en disco.
            try (FileWriter writer =
                    new FileWriter(XML_XSTREAM)) {

                writer.write(xml);
            }

            /*
             * DESERIALIZACIÓN:
             *
             * XML -> objeto Java
             */
            Videojuego recuperado =
                    (Videojuego)
                    xstream.fromXML(xml);

            /*
             * Si pregunta:
             *
             * SERIALIZAR:
             * Convertir un objeto Java a un formato
             * que se pueda almacenar o transportar.
             *
             * DESERIALIZAR:
             * Reconstruir el objeto Java desde ese formato.
             */

            System.out.println(
                    "Objeto original:   "
                    + original
            );

            System.out.println(
                    "Objeto recuperado: "
                    + recuperado
            );

            System.out.println(
                    XML_XSTREAM
                    + " generado."
            );

        } catch (Exception e) {
            System.out.println(
                    "Error con XStream: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // OPCIÓN 5
    // GENERAR XML MASIVO
    // ============================================================

    private static void generarXmlMasivo() {

        String[] desarrolladoras = {
                "Mojang",
                "CD Projekt",
                "Rockstar Games",
                "Sony Studios",
                "FromSoftware",
                "Bethesda"
        };

        String[] paises = {
                "Suecia",
                "Polonia",
                "Estados Unidos",
                "Japon",
                "Japon",
                "Estados Unidos"
        };

        String[] plataformas = {
                "PC",
                "PS5",
                "Xbox",
                "Nintendo Switch"
        };

        String[] generos = {
                "RPG",
                "Accion",
                "Aventura",
                "Shooter",
                "Sandbox",
                "Carreras"
        };

        try {

            // Creamos un XML nuevo desde cero.
            Document doc =
                    nuevoDocumento();

            Element raiz =
                    doc.createElement(
                            "desarrolladoras"
                    );

            doc.appendChild(raiz);

            int codigoJuego = 1;

            // Creamos las 6 desarrolladoras.
            for (int i = 0;
                    i < desarrolladoras.length;
                    i++) {

                Element desarrolladora =
                        doc.createElement(
                                "desarrolladora"
                        );

                desarrolladora.setAttribute(
                        "codigo",
                        String.valueOf(i + 1)
                );

                desarrolladora.setAttribute(
                        "nombre",
                        desarrolladoras[i]
                );

                agregarElemento(
                        doc,
                        desarrolladora,
                        "pais",
                        paises[i]
                );

                Element juegos =
                        doc.createElement(
                                "videojuegos"
                        );

                desarrolladora.appendChild(
                        juegos
                );

                /*
                 * Repartimos exactamente 100:
                 *
                 * 17 + 17 + 17 + 17 + 16 + 16 = 100
                 *
                 * Operador ternario:
                 *
                 * condición ? valorSiTrue : valorSiFalse
                 *
                 * Si i < 4 -> 17
                 * Si no    -> 16
                 */
                int cantidad =
                        i < 4 ? 17 : 16;

                for (int j = 0;
                        j < cantidad;
                        j++) {

                    Element juego =
                            doc.createElement(
                                    "videojuego"
                            );

                    juego.setAttribute(
                            "codigo",
                            String.valueOf(
                                    codigoJuego
                            )
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "titulo",
                            "Videojuego "
                                    + codigoJuego
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "plataforma",
                            plataformas[
                                    (codigoJuego - 1)
                                    % plataformas.length
                            ]
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "anio",
                            String.valueOf(
                                    2015
                                    + codigoJuego % 12
                            )
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "precio",
                            String.valueOf(
                                    19.99f
                                    + (codigoJuego % 5)
                                    * 10
                            )
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "multijugador",
                            codigoJuego % 2 == 0
                                    ? "S"
                                    : "N"
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "valoracion",
                            String.valueOf(
                                    7.0f
                                    + (codigoJuego % 30)
                                    / 10.0f
                            )
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "genero",
                            generos[
                                    (codigoJuego - 1)
                                    % generos.length
                            ]
                    );

                    int[] edadesPegi = {
                            7, 12, 16, 18
                    };

                    agregarElemento(
                            doc,
                            juego,
                            "pegi",
                            String.valueOf(
                                    edadesPegi[
                                            codigoJuego
                                            % edadesPegi.length
                                    ]
                            )
                    );

                    agregarElemento(
                            doc,
                            juego,
                            "ventas",
                            String.valueOf(
                                    codigoJuego
                                    * 100000
                            )
                    );

                    juegos.appendChild(
                            juego
                    );

                    codigoJuego++;
                }

                raiz.appendChild(
                        desarrolladora
                );
            }

            guardarXml(
                    doc,
                    new File(XML_MASIVO)
            );

            System.out.println(
                    XML_MASIVO
                    + " generado: "
                    + "6 desarrolladoras y "
                    + "100 videojuegos."
            );

        } catch (Exception e) {
            System.out.println(
                    "Error al generar XML masivo: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // OPCIÓN 6
    // CONSULTAS XPATH
    // ============================================================

    private static void consultarConXPath(
            Scanner sc) {

        if (!new File(XML_MASIVO).exists()) {

            System.out.println(
                    "Primero genera "
                    + XML_MASIVO
                    + " con la opción 5."
            );

            return;
        }

        try {

            // Cargamos el XML existente mediante DOM.
            Document doc =
                    cargarXml(XML_MASIVO);

            /*
             * XPath permite realizar consultas y
             * filtros dentro de un XML.
             */
            XPath xpath =
                    XPathFactory
                            .newInstance()
                            .newXPath();

            System.out.println(
                    "1. Precio superior a una cantidad"
            );

            System.out.println(
                    "2. Videojuegos multijugador"
            );

            System.out.println(
                    "3. Videojuegos de una desarrolladora"
            );

            System.out.println(
                    "4. Expresión XPath manual"
            );

            System.out.print(
                    "Consulta: "
            );

            int opcion =
                    Integer.parseInt(
                            sc.nextLine()
                    );

            String expresion;

            switch (opcion) {

                case 1 -> {

                    System.out.print(
                            "Precio mínimo: "
                    );

                    float precio =
                            Float.parseFloat(
                                    sc.nextLine()
                                            .replace(',', '.')
                            );

                    /*
                     * //
                     * Busca elementos descendientes.
                     *
                     * []
                     * Aplica una condición.
                     */
                    expresion =
                            "//videojuego[precio > "
                            + precio
                            + "]";
                }

                case 2 -> {

                    expresion =
                            "//videojuego"
                            + "[multijugador='S']";
                }

                case 3 -> {

                    System.out.print(
                            "Código de desarrolladora (1-6): "
                    );

                    String codigo =
                            sc.nextLine();

                    /*
                     * @ significa ATRIBUTO.
                     *
                     * Busca una desarrolladora cuyo
                     * atributo codigo tenga ese valor.
                     */
                    expresion =
                            "//desarrolladora"
                            + "[@codigo='"
                            + codigo
                            + "']"
                            + "//videojuego";
                }

                case 4 -> {

                    System.out.print(
                            "XPath: "
                    );

                    expresion =
                            sc.nextLine();
                }

                default -> {

                    System.out.println(
                            "Consulta no válida."
                    );

                    return;
                }
            }

            /*
             * XPathConstants.NODESET indica que esperamos
             * como resultado una colección de nodos.
             */
            NodeList resultados =
                    (NodeList)
                    xpath.evaluate(
                            expresion,
                            doc,
                            XPathConstants.NODESET
                    );

            System.out.println(
                    "Resultados: "
                    + resultados.getLength()
            );

            for (int i = 0;
                    i < resultados.getLength();
                    i++) {

                Node nodo =
                        resultados.item(i);

                if (
                        nodo.getNodeType()
                                == Node.ELEMENT_NODE
                        &&
                        nodo.getNodeName()
                                .equals("videojuego")
                ) {

                    Element juego =
                            (Element) nodo;

                    System.out.println(
                            juego.getAttribute(
                                    "codigo"
                            )
                            + " - "
                            + texto(
                                    juego,
                                    "titulo"
                            )
                            + " - "
                            + texto(
                                    juego,
                                    "precio"
                            )
                            + " €"
                    );

                } else {

                    System.out.println(
                            nodo.getTextContent()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error XPath: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // OPCIÓN 7
    // XML + XSLT -> HTML
    // ============================================================

    private static void generarHtmlConXslt() {

        if (!new File(XML_MASIVO).exists()) {

            System.out.println(
                    "Primero genera "
                    + XML_MASIVO
                    + " con la opción 5."
            );

            return;
        }

        if (!new File(XSL).exists()) {

            System.out.println(
                    "No existe "
                    + XSL
                    + "."
            );

            return;
        }

        try {

            /*
             * Cargamos videojuegos.xsl.
             *
             * La hoja XSLT contiene las reglas
             * para transformar el XML en HTML.
             */
            Transformer transformer =
                    TransformerFactory
                            .newInstance()
                            .newTransformer(
                                    new StreamSource(
                                            XSL
                                    )
                            );

            /*
             * Entrada:
             * videojuegos_masivo.xml
             *
             * Reglas:
             * videojuegos.xsl
             *
             * Salida:
             * videojuegos.html
             */
            transformer.transform(
                    new StreamSource(
                            XML_MASIVO
                    ),
                    new StreamResult(
                            HTML
                    )
            );

            System.out.println(
                    HTML
                    + " generado mediante XSLT."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error XSLT: "
                    + e.getMessage()
            );
        }
    }

    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /*
     * Crea un Document XML NUEVO y vacío.
     *
     * newDocument() = crear XML nuevo.
     */
    private static Document nuevoDocumento()
            throws Exception {

        DocumentBuilder builder =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder();

        return builder.newDocument();
    }

    /*
     * Carga un XML EXISTENTE mediante parse().
     *
     * parse() convierte el fichero XML
     * en un árbol DOM en memoria.
     */
    private static Document cargarXml(
            String nombre)
            throws Exception {

        DocumentBuilder builder =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder();

        Document doc =
                builder.parse(
                        new File(nombre)
                );

        doc.getDocumentElement()
                .normalize();

        return doc;
    }

    /*
     * Busca un <videojuego> por el atributo codigo.
     *
     * getElementsByTagName() devuelve un NodeList.
     * getAttribute() obtiene el valor de un atributo.
     */
    private static Element buscarJuegoPorCodigo(
            Document doc,
            String codigo) {

        NodeList lista =
                doc.getElementsByTagName(
                        "videojuego"
                );

        for (int i = 0;
                i < lista.getLength();
                i++) {

            Element juego =
                    (Element)
                    lista.item(i);

            if (
                    juego.getAttribute("codigo")
                            .equals(codigo)
            ) {

                return juego;
            }
        }

        return null;
    }

    /*
     * Devuelve el texto de una etiqueta hija.
     *
     * Ejemplo:
     *
     * texto(juego, "titulo")
     *
     * devuelve el contenido de:
     *
     * <titulo>...</titulo>
     */
    private static String texto(
            Element padre,
            String etiqueta) {

        return padre
                .getElementsByTagName(
                        etiqueta
                )
                .item(0)
                .getTextContent();
    }

    /*
     * Método auxiliar para crear:
     *
     * <nombre>valor</nombre>
     *
     * y añadirlo al elemento padre.
     */
    private static void agregarElemento(
            Document doc,
            Element padre,
            String nombre,
            String valor) {

        Element elemento =
                doc.createElement(
                        nombre
                );

        elemento.setTextContent(
                valor
        );

        padre.appendChild(
                elemento
        );
    }

    /*
     * Lee una cadena de longitud fija
     * del fichero binario.
     *
     * readChar() lee un carácter.
     *
     * Como cada char de Java ocupa 2 bytes:
     *
     * 40 char = 80 bytes.
     */
    private static String leerCadena(
            RandomAccessFile raf,
            int longitud)
            throws IOException {

        String texto = "";

        for (int i = 0;
                i < longitud;
                i++) {

            texto += raf.readChar();
        }

        return texto.trim();
    }

    /*
     * Transformer convierte el árbol DOM
     * que está en memoria en un fichero XML físico.
     *
     * DOMSource:
     * origen = Document.
     *
     * StreamResult:
     * destino = fichero.
     */
    private static void guardarXml(
            Document doc,
            File archivo)
            throws Exception {

        Transformer transformer =
                TransformerFactory
                        .newInstance()
                        .newTransformer();

        // Formato del XML de salida.
        transformer.setOutputProperty(
                OutputKeys.INDENT,
                "yes"
        );

        transformer.setOutputProperty(
                OutputKeys.ENCODING,
                "UTF-8"
        );

        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount",
                "4"
        );

        transformer.transform(
                new DOMSource(doc),
                new StreamResult(archivo)
        );
    }
}