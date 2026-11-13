import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.KXml2Driver;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.Scanner;

/*
 * ==============================================================
 * PRÁCTICA Nº3 - ACCESO A DATOS
 * Gestión avanzada de datos documentales XML
 * ==============================================================
 *
 * Dominio:
 * Videojuegos y desarrolladoras.
 *
 * Tecnologías utilizadas:
 *
 * - Ficheros binarios de acceso directo.
 * - DOM.
 * - XML.
 * - XStream.
 * - XPath.
 * - XSLT.
 *
 * Fichero binario:
 * videojuegos.dat
 *
 * Estructura de cada registro:
 *
 * Código                  int       -> 4 bytes
 * Título                  40 char   -> 80 bytes
 * Plataforma              15 char   -> 30 bytes
 * Año                     short     -> 2 bytes
 * Precio                  float     -> 4 bytes
 * Multijugador            boolean   -> 1 byte
 * Valoración              float     -> 4 bytes
 * Código desarrolladora   short     -> 2 bytes
 * Género                  20 char   -> 40 bytes
 * PEGI                     short     -> 2 bytes
 * Ventas                   int       -> 4 bytes
 *
 * TOTAL:
 *
 * 4 + 80 + 30 + 2 + 4 + 1 + 4 + 2 + 40 + 2 + 4
 * = 173 bytes.
 */

public class Practica3Videojuegos {

    // ============================================================
    // CONSTANTES
    // ============================================================

    private static final String FICHERO_BINARIO =
            "videojuegos.dat";

    private static final String FICHERO_XML =
            "videojuegos.xml";

    private static final String FICHERO_XSTREAM =
            "videojuego_xstream.xml";

    private static final int LONGITUD_TITULO = 40;
    private static final int LONGITUD_PLATAFORMA = 15;
    private static final int LONGITUD_GENERO = 20;

    private static final int TAMANO_REGISTRO = 173;


    // ============================================================
    // MAIN
    // ============================================================

    public static void main(String[] args) {

        Scanner scanner =
                new Scanner(System.in);

        int opcion = 0;

        do {

            mostrarMenu();

            try {

                opcion =
                        Integer.parseInt(
                                scanner.nextLine().trim()
                        );

                switch (opcion) {

                    case 1:
                        generarXmlDesdeBinario();
                        break;

                    case 2:
                        modificarPrecioXml(scanner);
                        break;

                    case 3:
                        eliminarVideojuegoXml(scanner);
                        break;

                    case 4:
                        serializarDeserializarXStream();
                        break;

                    case 5:
                        generarXmlMasivo();
                        break;
                    case 6:
                        consultarConXPath(scanner);
                        break;

                    case 7:
                        generarHtmlConXslt();
                        break;

                    case 8:
                        System.out.println(
                                "Programa finalizado."
                        );
                        break;

                    default:
                        System.out.println(
                                "Opción no válida."
                        );
                }

            } catch (NumberFormatException e) {

                System.out.println(
                        "Error: debes introducir una opción numérica."
                );
            }

            System.out.println();

        } while (opcion != 8);

        scanner.close();
    }


    // ============================================================
    // MENÚ
    // ============================================================

    private static void mostrarMenu() {

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "     GESTIÓN AVANZADA DE VIDEOJUEGOS Y FICHEROS XML"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "1.- Generar fichero XML desde videojuegos.dat"
        );

        System.out.println(
                "2.- Modificar el precio de un videojuego en el XML"
        );

        System.out.println(
                "3.- Eliminar un videojuego directamente del XML"
        );

        System.out.println(
                "4.- Serializar y deserializar un videojuego con XStream"
        );

        System.out.println(
                "5.- Generar XML masivo de desarrolladoras y videojuegos"
        );

        System.out.println(
                "6.- Consultar datos utilizando XPath"
        );

        System.out.println(
                "7.- Generar fichero HTML y PDF mediante XSLT"
        );

        System.out.println(
                "8.- Salir"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        System.out.print(
                "Seleccione una opción: "
        );
    }


    // ============================================================
    // OPCIÓN 1
    // GENERAR XML DESDE EL FICHERO BINARIO
    // ============================================================

    private static void generarXmlDesdeBinario() {

        File archivoBinario =
                new File(FICHERO_BINARIO);

        /*
         * Comprobamos que videojuegos.dat exista.
         */
        if (!archivoBinario.exists()) {

            System.out.println(
                    "Error: el fichero "
                            + FICHERO_BINARIO
                            + " no existe."
            );

            return;
        }

        try {

            /*
             * Creamos las herramientas necesarias
             * para trabajar con DOM.
             */
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            /*
             * Creamos un documento XML vacío.
             */
            Document doc =
                    builder.newDocument();

            /*
             * Creamos el elemento raíz:
             *
             * <videojuegos>
             */
            Element raiz =
                    doc.createElement("videojuegos");

            doc.appendChild(raiz);

            /*
             * Abrimos videojuegos.dat en modo lectura.
             */
            try (
                    RandomAccessFile raf =
                            new RandomAccessFile(
                                    archivoBinario,
                                    "r"
                            )
            ) {

                /*
                 * Cada registro debe ocupar exactamente
                 * 173 bytes.
                 */
                if (
                        raf.length()
                                % TAMANO_REGISTRO
                                != 0
                ) {

                    System.out.println(
                            "Error: el fichero no contiene "
                                    + "registros completos de "
                                    + TAMANO_REGISTRO
                                    + " bytes."
                    );

                    return;
                }

                int registrosLeidos = 0;
                int registrosXml = 0;

                /*
                 * Recorremos secuencialmente todo
                 * el fichero binario.
                 */
                while (
                        raf.getFilePointer()
                                < raf.length()
                ) {

                    /*
                     * Los datos deben leerse exactamente
                     * en el mismo orden en que fueron escritos.
                     */

                    int codigo =
                            raf.readInt();

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

                    short anio =
                            raf.readShort();

                    float precio =
                            raf.readFloat();

                    boolean multijugador =
                            raf.readBoolean();

                    float valoracion =
                            raf.readFloat();

                    short codigoDesarrolladora =
                            raf.readShort();

                    String genero =
                            leerCadenaFija(
                                    raf,
                                    LONGITUD_GENERO
                            );

                    short pegi =
                            raf.readShort();

                    int ventas =
                            raf.readInt();

                    registrosLeidos++;

                    /*
                     * Código -1 significa borrado lógico.
                     */
                    if (codigo == -1) {
                        continue;
                    }

                    /*
                     * Creamos:
                     *
                     * <videojuego>
                     */
                    Element videojuego =
                            doc.createElement(
                                    "videojuego"
                            );

                    /*
                     * Añadimos atributos:
                     *
                     * codigo="1"
                     * desarrolladora="1"
                     */
                    videojuego.setAttribute(
                            "codigo",
                            String.valueOf(codigo)
                    );

                    videojuego.setAttribute(
                            "desarrolladora",
                            String.valueOf(
                                    codigoDesarrolladora
                            )
                    );

                    /*
                     * Creamos los elementos hijos.
                     */
                    agregarElemento(
                            doc,
                            videojuego,
                            "titulo",
                            titulo
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "plataforma",
                            plataforma
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "anio",
                            String.valueOf(anio)
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "precio",
                            String.valueOf(precio)
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "multijugador",
                            multijugador ? "S" : "N"
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "valoracion",
                            String.valueOf(valoracion)
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "genero",
                            genero
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "pegi",
                            String.valueOf(pegi)
                    );

                    agregarElemento(
                            doc,
                            videojuego,
                            "ventas",
                            String.valueOf(ventas)
                    );

                    /*
                     * Añadimos <videojuego> al elemento raíz.
                     */
                    raiz.appendChild(
                            videojuego
                    );

                    registrosXml++;
                }

                System.out.println(
                        "Registros leídos del binario: "
                                + registrosLeidos
                );

                System.out.println(
                        "Videojuegos añadidos al XML: "
                                + registrosXml
                );
            }

            /*
             * Guardamos el árbol DOM en videojuegos.xml.
             */
            guardarDocumentoXml(
                    doc,
                    new File(FICHERO_XML)
            );

            System.out.println();

            System.out.println(
                    "Fichero "
                            + FICHERO_XML
                            + " generado correctamente."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error al generar el XML: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ============================================================
    // OPCIÓN 2
    // MODIFICAR PRECIO EN XML
    // ============================================================

    private static void modificarPrecioXml(
            Scanner scanner) {

        File archivoXml =
                new File(FICHERO_XML);

        if (!archivoXml.exists()) {

            System.out.println(
                    "Error: primero debes generar "
                            + FICHERO_XML
                            + " con la opción 1."
            );

            return;
        }

        System.out.print(
                "Introduce el código del videojuego: "
        );

        String codigoBuscado =
                scanner.nextLine().trim();

        try {

            /*
             * Cargamos videojuegos.xml mediante DOM.
             */
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            Document doc =
                    builder.parse(archivoXml);

            doc.getDocumentElement().normalize();

            /*
             * Recuperamos todos los nodos <videojuego>.
             */
            NodeList listaVideojuegos =
                    doc.getElementsByTagName(
                            "videojuego"
                    );

            boolean encontrado = false;

            for (
                    int i = 0;
                    i < listaVideojuegos.getLength();
                    i++
            ) {

                Node nodo =
                        listaVideojuegos.item(i);

                if (
                        nodo.getNodeType()
                                == Node.ELEMENT_NODE
                ) {

                    Element videojuego =
                            (Element) nodo;

                    String codigoActual =
                            videojuego.getAttribute(
                                    "codigo"
                            );

                    if (
                            codigoActual.equals(
                                    codigoBuscado
                            )
                    ) {

                        encontrado = true;

                        String titulo =
                                videojuego
                                        .getElementsByTagName(
                                                "titulo"
                                        )
                                        .item(0)
                                        .getTextContent();

                        /*
                         * Localizamos:
                         *
                         * <precio>...</precio>
                         */
                        Node nodoPrecio =
                                videojuego
                                        .getElementsByTagName(
                                                "precio"
                                        )
                                        .item(0);

                        String precioActual =
                                nodoPrecio.getTextContent();

                        System.out.println();

                        System.out.println(
                                "Videojuego encontrado:"
                        );

                        System.out.println(
                                "Código: "
                                        + codigoActual
                        );

                        System.out.println(
                                "Título: "
                                        + titulo
                        );

                        System.out.println(
                                "Precio actual: "
                                        + precioActual
                                        + " EUR"
                        );

                        System.out.print(
                                "Introduce el nuevo precio: "
                        );

                        float nuevoPrecio =
                                Float.parseFloat(
                                        scanner.nextLine()
                                                .trim()
                                                .replace(
                                                        ',',
                                                        '.'
                                                )
                                );

                        /*
                         * Cambiamos el contenido del nodo.
                         */
                        nodoPrecio.setTextContent(
                                String.valueOf(
                                        nuevoPrecio
                                )
                        );

                        /*
                         * Guardamos todo el documento XML
                         * actualizado.
                         */
                        guardarDocumentoXml(
                                doc,
                                archivoXml
                        );

                        System.out.println(
                                "Precio modificado correctamente."
                        );

                        break;
                    }
                }
            }

            if (!encontrado) {

                System.out.println(
                        "No existe ningún videojuego "
                                + "con código "
                                + codigoBuscado
                                + "."
                );
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Error: el precio introducido "
                            + "no es válido."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error al modificar el XML: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // OPCIÓN 3
    // ELIMINAR VIDEOJUEGO DEL XML
    // ============================================================

    private static void eliminarVideojuegoXml(
            Scanner scanner) {

        File archivoXml =
                new File(FICHERO_XML);

        if (!archivoXml.exists()) {

            System.out.println(
                    "Error: primero debes generar "
                            + FICHERO_XML
                            + " con la opción 1."
            );

            return;
        }

        System.out.print(
                "Introduce el código del videojuego "
                        + "que deseas eliminar: "
        );

        String codigoBuscado =
                scanner.nextLine().trim();

        try {

            /*
             * Cargamos el XML mediante DOM.
             */
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            Document doc =
                    builder.parse(archivoXml);

            doc.getDocumentElement().normalize();

            NodeList listaVideojuegos =
                    doc.getElementsByTagName(
                            "videojuego"
                    );

            boolean encontrado = false;

            for (
                    int i = 0;
                    i < listaVideojuegos.getLength();
                    i++
            ) {

                Node nodo =
                        listaVideojuegos.item(i);

                if (
                        nodo.getNodeType()
                                == Node.ELEMENT_NODE
                ) {

                    Element videojuego =
                            (Element) nodo;

                    String codigoActual =
                            videojuego.getAttribute(
                                    "codigo"
                            );

                    if (
                            codigoActual.equals(
                                    codigoBuscado
                            )
                    ) {

                        encontrado = true;

                        String titulo =
                                videojuego
                                        .getElementsByTagName(
                                                "titulo"
                                        )
                                        .item(0)
                                        .getTextContent();

                        System.out.println();

                        System.out.println(
                                "Videojuego encontrado:"
                        );

                        System.out.println(
                                "Código: "
                                        + codigoActual
                        );

                        System.out.println(
                                "Título: "
                                        + titulo
                        );

                        System.out.print(
                                "¿Seguro que deseas eliminarlo? (S/N): "
                        );

                        String respuesta =
                                scanner.nextLine().trim();

                        if (
                                respuesta.equalsIgnoreCase(
                                        "S"
                                )
                        ) {

                            /*
                             * Obtenemos el padre de <videojuego>.
                             */
                            Node padre =
                                    nodo.getParentNode();

                            /*
                             * Eliminamos completamente el nodo.
                             */
                            padre.removeChild(nodo);

                            /*
                             * Guardamos de nuevo el XML.
                             */
                            guardarDocumentoXml(
                                    doc,
                                    archivoXml
                            );

                            System.out.println(
                                    "Videojuego eliminado "
                                            + "correctamente del XML."
                            );

                        } else {

                            System.out.println(
                                    "Operación cancelada."
                            );
                        }

                        break;
                    }
                }
            }

            if (!encontrado) {

                System.out.println(
                        "No existe ningún videojuego "
                                + "con código "
                                + codigoBuscado
                                + "."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Error al eliminar del XML: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // OPCIÓN 4
    // SERIALIZAR Y DESERIALIZAR CON XSTREAM
    // ============================================================

    /*
     * SERIALIZAR:
     *
     * Objeto Java
     *      ↓
     *   XStream
     *      ↓
     * XML
     *
     * DESERIALIZAR:
     *
     * XML
     *      ↓
     *   XStream
     *      ↓
     * Objeto Java
     */
    private static void serializarDeserializarXStream() {

        try {

            /*
             * Creamos un objeto Videojuego
             * únicamente en memoria.
             */
            Videojuego videojuegoOriginal =
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
             * Creamos XStream utilizando el driver
             * KXml2Driver pedido en el enunciado.
             */
            XStream xstream =
                    new XStream(
                            new KXml2Driver()
                    );

            /*
             * Permitimos que XStream pueda
             * deserializar nuestra clase Videojuego.
             */
            xstream.allowTypes(
                    new Class[]{
                            Videojuego.class
                    }
            );

            /*
             * Indicamos que la etiqueta principal
             * del XML se llamará:
             *
             * <videojuego>
             */
            xstream.alias(
                    "videojuego",
                    Videojuego.class
            );

            /*
             * ====================================================
             * SERIALIZACIÓN
             * ====================================================
             *
             * Convertimos el objeto Java en XML.
             */
            String xml =
                    xstream.toXML(
                            videojuegoOriginal
                    );

            System.out.println();

            System.out.println(
                    "===== OBJETO ORIGINAL ====="
            );

            System.out.println(
                    videojuegoOriginal
            );

            System.out.println();

            System.out.println(
                    "===== OBJETO SERIALIZADO A XML ====="
            );

            System.out.println(xml);

            /*
             * Guardamos el XML generado en disco.
             */
            try (
                    FileWriter writer =
                            new FileWriter(
                                    FICHERO_XSTREAM
                            )
            ) {

                writer.write(xml);
            }

            System.out.println();

            System.out.println(
                    "Fichero "
                            + FICHERO_XSTREAM
                            + " generado correctamente."
            );

            /*
             * ====================================================
             * DESERIALIZACIÓN
             * ====================================================
             *
             * Leemos el XML y reconstruimos
             * un objeto Videojuego.
             */
            Videojuego videojuegoRecuperado =
                    (Videojuego)
                            xstream.fromXML(
                                    xml
                            );

            System.out.println();

            System.out.println(
                    "===== OBJETO DESERIALIZADO ====="
            );

            System.out.println(
                    videojuegoRecuperado
            );

            System.out.println();

            System.out.println(
                    "Serialización y deserialización "
                            + "realizadas correctamente."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error durante XStream: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // ============================================================
// OPCIÓN 5
// GENERAR XML MASIVO
// ============================================================

/*
 * Genera un nuevo XML con 6 desarrolladoras y
 * 100 videojuegos distribuidos entre ellas.
 *
 * Este fichero se utilizará después para realizar
 * las consultas XPath.
 */
private static void generarXmlMasivo() {

    final String FICHERO_MASIVO =
            "videojuegos_masivo.xml";

    try {

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder =
                factory.newDocumentBuilder();

        Document doc =
                builder.newDocument();

        /*
         * Elemento raíz:
         *
         * <desarrolladoras>
         */
        Element raiz =
                doc.createElement("desarrolladoras");

        doc.appendChild(raiz);

        /*
         * Datos de las 6 desarrolladoras.
         */
        String[] nombresDesarrolladoras = {
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

        int codigoVideojuego = 1;

        /*
         * Recorremos las 6 desarrolladoras.
         */
        for (
                int i = 0;
                i < nombresDesarrolladoras.length;
                i++
        ) {

            /*
             * Creamos:
             *
             * <desarrolladora codigo="1">
             */
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
                    nombresDesarrolladoras[i]
            );

            agregarElemento(
                    doc,
                    desarrolladora,
                    "pais",
                    paises[i]
            );

            /*
             * Dentro de cada desarrolladora
             * tendremos un nodo <videojuegos>.
             */
            Element listaVideojuegos =
                    doc.createElement(
                            "videojuegos"
                    );

            desarrolladora.appendChild(
                    listaVideojuegos
            );

            /*
             * Repartimos 100 videojuegos:
             *
             * Las primeras 4 desarrolladoras tendrán 17.
             * Las últimas 2 tendrán 16.
             *
             * 17 + 17 + 17 + 17 + 16 + 16 = 100
             */
            int cantidad =
                    i < 4 ? 17 : 16;

            for (
                    int j = 0;
                    j < cantidad;
                    j++
            ) {

                Element videojuego =
                        doc.createElement(
                                "videojuego"
                        );

                videojuego.setAttribute(
                        "codigo",
                        String.valueOf(
                                codigoVideojuego
                        )
                );

                /*
                 * Generamos datos coherentes de ejemplo.
                 */
                String titulo =
                        "Videojuego "
                                + codigoVideojuego;

                String plataforma =
                        plataformas[
                                (codigoVideojuego - 1)
                                        % plataformas.length
                        ];

                int anio =
                        2015
                                + (
                                codigoVideojuego
                                        % 12
                        );

                float precio =
                        19.99f
                                + (
                                codigoVideojuego
                                        % 5
                        ) * 10;

                boolean multijugador =
                        codigoVideojuego % 2 == 0;

                float valoracion =
                        7.0f
                                + (
                                codigoVideojuego
                                        % 30
                        ) / 10.0f;

                String genero =
                        generos[
                                (codigoVideojuego - 1)
                                        % generos.length
                        ];

                int pegi;

                switch (
                        codigoVideojuego % 4
                ) {

                    case 0:
                        pegi = 18;
                        break;

                    case 1:
                        pegi = 7;
                        break;

                    case 2:
                        pegi = 12;
                        break;

                    default:
                        pegi = 16;
                        break;
                }

                int ventas =
                        100000
                                * codigoVideojuego;

                agregarElemento(
                        doc,
                        videojuego,
                        "titulo",
                        titulo
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "plataforma",
                        plataforma
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "anio",
                        String.valueOf(anio)
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "precio",
                        String.valueOf(precio)
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "multijugador",
                        multijugador ? "S" : "N"
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "valoracion",
                        String.valueOf(
                                valoracion
                        )
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "genero",
                        genero
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "pegi",
                        String.valueOf(pegi)
                );

                agregarElemento(
                        doc,
                        videojuego,
                        "ventas",
                        String.valueOf(
                                ventas
                        )
                );

                listaVideojuegos.appendChild(
                        videojuego
                );

                codigoVideojuego++;
            }

            /*
             * Añadimos la desarrolladora
             * completa al elemento raíz.
             */
            raiz.appendChild(
                    desarrolladora
            );
        }

        /*
         * Guardamos el DOM.
         */
        guardarDocumentoXml(
                doc,
                new File(FICHERO_MASIVO)
        );

        System.out.println(
                "Fichero "
                        + FICHERO_MASIVO
                        + " generado correctamente."
        );

        System.out.println(
                "Desarrolladoras creadas: 6"
        );

        System.out.println(
                "Videojuegos creados: 100"
        );

    } catch (Exception e) {

        System.out.println(
                "Error al generar el XML masivo: "
                        + e.getMessage()
        );
    }
}
// ============================================================
// OPCIÓN 6
// CONSULTAS XPATH
// ============================================================

/*
 * Ejecuta varias consultas XPath sobre
 * videojuegos_masivo.xml.
 */
private static void consultarConXPath(
        Scanner scanner) {

    File archivo =
            new File(
                    "videojuegos_masivo.xml"
            );

    if (!archivo.exists()) {

        System.out.println(
                "Error: primero debes generar "
                        + "videojuegos_masivo.xml "
                        + "con la opción 5."
        );

        return;
    }

    try {

        /*
         * Cargamos el XML como DOM.
         */
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder =
                factory.newDocumentBuilder();

        Document doc =
                builder.parse(archivo);

        doc.getDocumentElement().normalize();

        /*
         * Creamos el objeto XPath.
         */
        XPathFactory xpathFactory =
                XPathFactory.newInstance();

        XPath xpath =
                xpathFactory.newXPath();

        System.out.println();
        System.out.println(
                "========== CONSULTAS XPATH =========="
        );

        System.out.println(
                "1.- Videojuegos con precio superior a una cantidad"
        );

        System.out.println(
                "2.- Videojuegos con valoración igual o superior a una cantidad"
        );

        System.out.println(
                "3.- Videojuegos multijugador"
        );

        System.out.println(
                "4.- Videojuegos de una desarrolladora"
        );

        System.out.println(
                "5.- Contar todos los videojuegos"
        );

        System.out.println(
                "6.- Introducir una expresión XPath manualmente"
        );

        System.out.print(
                "Seleccione una consulta: "
        );

        int opcion =
                Integer.parseInt(
                        scanner.nextLine().trim()
                );

        String expresion;

        switch (opcion) {

            case 1:

                System.out.print(
                        "Precio mínimo: "
                );

                float precio =
                        Float.parseFloat(
                                scanner.nextLine()
                                        .trim()
                                        .replace(',', '.')
                        );

                expresion =
                        "//videojuego[precio > "
                                + precio
                                + "]";

                mostrarResultadoXPath(
                        xpath,
                        doc,
                        expresion
                );

                break;

            case 2:

                System.out.print(
                        "Valoración mínima: "
                );

                float valoracion =
                        Float.parseFloat(
                                scanner.nextLine()
                                        .trim()
                                        .replace(',', '.')
                        );

                expresion =
                        "//videojuego[valoracion >= "
                                + valoracion
                                + "]";

                mostrarResultadoXPath(
                        xpath,
                        doc,
                        expresion
                );

                break;

            case 3:

                expresion =
                        "//videojuego[multijugador='S']";

                mostrarResultadoXPath(
                        xpath,
                        doc,
                        expresion
                );

                break;

            case 4:

                System.out.print(
                        "Código de desarrolladora (1-6): "
                );

                String codigo =
                        scanner.nextLine().trim();

                /*
                 * Buscamos videojuegos que cuelgan
                 * de la desarrolladora indicada.
                 */
                expresion =
                        "//desarrolladora[@codigo='"
                                + codigo
                                + "']//videojuego";

                mostrarResultadoXPath(
                        xpath,
                        doc,
                        expresion
                );

                break;

            case 5:

                /*
                 * count() devuelve un NUMBER,
                 * no un conjunto de nodos.
                 */
                Double total =
                        (Double) xpath.evaluate(
                                "count(//videojuego)",
                                doc,
                                XPathConstants.NUMBER
                        );

                System.out.println(
                        "Total de videojuegos: "
                                + total.intValue()
                );

                break;

            case 6:

                System.out.println(
                        "Ejemplos:"
                );

                System.out.println(
                        "//videojuego[precio > 40]"
                );

                System.out.println(
                        "//videojuego[valoracion >= 9]"
                );

                System.out.println(
                        "//videojuego[multijugador='S']/titulo"
                );

                System.out.println();

                System.out.print(
                        "Introduce la expresión XPath: "
                );

                expresion =
                        scanner.nextLine();

                mostrarResultadoXPath(
                        xpath,
                        doc,
                        expresion
                );

                break;

            default:

                System.out.println(
                        "Consulta no válida."
                );
        }

    } catch (NumberFormatException e) {

        System.out.println(
                "Error: dato numérico no válido."
        );

    } catch (Exception e) {

        System.out.println(
                "Error al realizar la consulta XPath: "
                        + e.getMessage()
        );
    }
}
/*
 * Ejecuta una expresión XPath que devuelve nodos
 * y muestra los resultados.
 */
private static void mostrarResultadoXPath(
        XPath xpath,
        Document doc,
        String expresion) throws Exception {

    System.out.println();
    System.out.println(
            "XPath ejecutado:"
    );

    System.out.println(
            expresion
    );

    NodeList resultados =
            (NodeList) xpath.evaluate(
                    expresion,
                    doc,
                    XPathConstants.NODESET
            );

    System.out.println(
            "Resultados encontrados: "
                    + resultados.getLength()
    );

    for (
            int i = 0;
            i < resultados.getLength();
            i++
    ) {

        Node nodo =
                resultados.item(i);

        /*
         * Si el resultado es un <videojuego>,
         * mostramos código y título.
         */
        if (
                nodo.getNodeType()
                        == Node.ELEMENT_NODE
                &&
                nodo.getNodeName()
                        .equals("videojuego")
        ) {

            Element videojuego =
                    (Element) nodo;

            String codigo =
                    videojuego.getAttribute(
                            "codigo"
                    );

            String titulo =
                    videojuego
                            .getElementsByTagName(
                                    "titulo"
                            )
                            .item(0)
                            .getTextContent();

            String precio =
                    videojuego
                            .getElementsByTagName(
                                    "precio"
                            )
                            .item(0)
                            .getTextContent();

            String valoracion =
                    videojuego
                            .getElementsByTagName(
                                    "valoracion"
                            )
                            .item(0)
                            .getTextContent();

            System.out.println(
                    codigo
                            + " - "
                            + titulo
                            + " | Precio: "
                            + precio
                            + " EUR"
                            + " | Valoración: "
                            + valoracion
            );

        } else {

            /*
             * Por ejemplo si la consulta devuelve:
             *
             * /titulo
             */
            System.out.println(
                    nodo.getTextContent()
            );
        }
    }
}
// ============================================================
// OPCIÓN 7
// GENERAR HTML MEDIANTE XSLT
// ============================================================

private static void generarHtmlConXslt() {

    File xml =
            new File(
                    "videojuegos_masivo.xml"
            );

    File xsl =
            new File(
                    "videojuegos.xsl"
            );

    if (!xml.exists()) {

        System.out.println(
                "Error: primero debes generar "
                        + "videojuegos_masivo.xml "
                        + "con la opción 5."
        );

        return;
    }

    if (!xsl.exists()) {

        System.out.println(
                "Error: no existe videojuegos.xsl."
        );

        return;
    }

    try {

        /*
         * TransformerFactory cargará
         * nuestra hoja XSLT.
         */
        TransformerFactory factory =
                TransformerFactory.newInstance();

        Transformer transformer =
                factory.newTransformer(
                        new javax.xml.transform.stream.StreamSource(
                                xsl
                        )
                );

        /*
         * XML de entrada.
         */
        javax.xml.transform.stream.StreamSource source =
                new javax.xml.transform.stream.StreamSource(
                        xml
                );

        /*
         * HTML que queremos generar.
         */
        StreamResult result =
                new StreamResult(
                        new File(
                                "videojuegos.html"
                        )
                );

        /*
         * Aplicamos la transformación XSLT.
         */
        transformer.transform(
                source,
                result
        );

        System.out.println(
                "Fichero videojuegos.html "
                        + "generado correctamente mediante XSLT."
        );

    } catch (Exception e) {

        System.out.println(
                "Error durante la transformación XSLT: "
                        + e.getMessage()
        );
    }
}
    // ============================================================
    // MÉTODOS AUXILIARES
    // ============================================================

    /*
     * Crea un elemento XML con texto.
     *
     * Ejemplo:
     *
     * nombre = "titulo"
     * valor = "Minecraft"
     *
     * Resultado:
     *
     * <titulo>Minecraft</titulo>
     */
    private static void agregarElemento(
            Document doc,
            Element padre,
            String nombre,
            String valor) {

        Element elemento =
                doc.createElement(nombre);

        elemento.appendChild(
                doc.createTextNode(valor)
        );

        padre.appendChild(elemento);
    }


    /*
     * Lee una cadena de longitud fija
     * desde RandomAccessFile.
     */
    private static String leerCadenaFija(
            RandomAccessFile raf,
            int longitud) throws IOException {

        char[] caracteres =
                new char[longitud];

        for (
                int i = 0;
                i < longitud;
                i++
        ) {

            caracteres[i] =
                    raf.readChar();
        }

        return new String(
                caracteres
        ).trim();
    }


    /*
     * Guarda un árbol DOM en un fichero XML.
     *
     * Utilizamos este mismo método para:
     *
     * - Crear videojuegos.xml.
     * - Modificar un precio.
     * - Eliminar un videojuego.
     */
    private static void guardarDocumentoXml(
            Document doc,
            File archivo) throws Exception {

        TransformerFactory transformerFactory =
                TransformerFactory.newInstance();

        Transformer transformer =
                transformerFactory.newTransformer();

        /*
         * Formateamos el fichero XML.
         */
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

        DOMSource source =
                new DOMSource(doc);

        StreamResult result =
                new StreamResult(archivo);

        transformer.transform(
                source,
                result
        );
    }
}