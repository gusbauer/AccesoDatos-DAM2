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
 * COMPILAR (Windows):
 * javac -cp ".;*" Videojuego.java GenerarDatosIniciales.java Practica3Videojuegos.java
 *
 * GENERAR EL BINARIO INICIAL:
 * java -cp ".;*" GenerarDatosIniciales
 *
 * EJECUTAR:
 * java -cp ".;*" Practica3Videojuegos
 *
 * FLUJO PRINCIPAL:
 * videojuegos.dat -> RandomAccessFile -> DOM -> videojuegos.xml
 * Videojuego -> XStream -> XML -> Videojuego
 * videojuegos_masivo.xml -> XPath
 * videojuegos_masivo.xml + videojuegos.xsl -> videojuegos.html
 *
 * CLASES IMPORTANTES:
 * RandomAccessFile -> lee el fichero binario.
 * Document -> representa todo el XML en memoria. doc es la variable.
 * Element -> representa una etiqueta XML.
 * Node -> representa un nodo genérico.
 * NodeList -> lista de nodos.
 * DocumentBuilder -> newDocument() crea XML; parse() carga XML.
 * XStream -> toXML() serializa; fromXML() deserializa.
 * XPath -> consulta y filtra nodos XML.
 * Transformer -> guarda DOM a XML y aplica XSLT.
 *
 * REGISTRO BINARIO: 173 bytes
 * Código 4 + Título 80 + Plataforma 30 + Año 2 + Precio 4
 * + Multijugador 1 + Valoración 4 + Desarrolladora 2
 * + Género 40 + PEGI 2 + Ventas 4 = 173 bytes.
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

    // OPCIÓN 1: binario -> DOM -> XML.
    private static void generarXmlDesdeBinario() {
        File fichero = new File(BINARIO);

        if (!fichero.exists()) {
            System.out.println("No existe " + BINARIO + ". Ejecuta antes GenerarDatosIniciales.");
            return;
        }

        try {
            Document doc = nuevoDocumento();
            Element raiz = doc.createElement("videojuegos");
            doc.appendChild(raiz);

            try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
                if (raf.length() % TAM_REGISTRO != 0) {
                    System.out.println("El fichero no contiene registros completos de 173 bytes.");
                    return;
                }

                // Lectura secuencial del fichero binario.
                while (raf.getFilePointer() < raf.length()) {
                    int codigo = raf.readInt();
                    String titulo = leerCadena(raf, LONG_TITULO);
                    String plataforma = leerCadena(raf, LONG_PLATAFORMA);
                    short anio = raf.readShort();
                    float precio = raf.readFloat();
                    boolean multijugador = raf.readBoolean();
                    float valoracion = raf.readFloat();
                    short desarrolladora = raf.readShort();
                    String genero = leerCadena(raf, LONG_GENERO);
                    short pegi = raf.readShort();
                    int ventas = raf.readInt();

                    // Código -1 = borrado lógico.
                    if (codigo == -1) {
                        continue;
                    }

                    Element juego = doc.createElement("videojuego");
                    juego.setAttribute("codigo", String.valueOf(codigo));
                    juego.setAttribute("desarrolladora", String.valueOf(desarrolladora));

                    agregarElemento(doc, juego, "titulo", titulo);
                    agregarElemento(doc, juego, "plataforma", plataforma);
                    agregarElemento(doc, juego, "anio", String.valueOf(anio));
                    agregarElemento(doc, juego, "precio", String.valueOf(precio));
                    agregarElemento(doc, juego, "multijugador", multijugador ? "S" : "N");
                    agregarElemento(doc, juego, "valoracion", String.valueOf(valoracion));
                    agregarElemento(doc, juego, "genero", genero);
                    agregarElemento(doc, juego, "pegi", String.valueOf(pegi));
                    agregarElemento(doc, juego, "ventas", String.valueOf(ventas));

                    raiz.appendChild(juego);
                }
            }

            guardarXml(doc, new File(XML));
            System.out.println(XML + " generado correctamente.");

        } catch (Exception e) {
            System.out.println("Error al generar XML: " + e.getMessage());
        }
    }

    // OPCIÓN 2: parse() carga el XML y setTextContent() cambia <precio>.
    private static void modificarPrecioXml(Scanner sc) {
        if (!new File(XML).exists()) {
            System.out.println("Primero genera " + XML + " con la opción 1.");
            return;
        }

        System.out.print("Código del videojuego: ");
        String codigo = sc.nextLine();

        try {
            Document doc = cargarXml(XML);
            Element juego = buscarJuegoPorCodigo(doc, codigo);

            if (juego == null) {
                System.out.println("Código no encontrado.");
                return;
            }

            Node precio = juego.getElementsByTagName("precio").item(0);
            System.out.println("Título: " + texto(juego, "titulo"));
            System.out.println("Precio actual: " + precio.getTextContent());

            System.out.print("Nuevo precio: ");
            float nuevoPrecio = Float.parseFloat(sc.nextLine().replace(',', '.'));

            precio.setTextContent(String.valueOf(nuevoPrecio));
            guardarXml(doc, new File(XML));
            System.out.println("Precio modificado.");

        } catch (Exception e) {
            System.out.println("Error al modificar XML: " + e.getMessage());
        }
    }

    // OPCIÓN 3: removeChild() elimina el nodo completo del XML.
    private static void eliminarVideojuegoXml(Scanner sc) {
        if (!new File(XML).exists()) {
            System.out.println("Primero genera " + XML + " con la opción 1.");
            return;
        }

        System.out.print("Código a eliminar: ");
        String codigo = sc.nextLine();

        try {
            Document doc = cargarXml(XML);
            Element juego = buscarJuegoPorCodigo(doc, codigo);

            if (juego == null) {
                System.out.println("Código no encontrado.");
                return;
            }

            System.out.println("Título: " + texto(juego, "titulo"));
            System.out.print("¿Eliminar? (S/N): ");

            if (sc.nextLine().equalsIgnoreCase("S")) {
                juego.getParentNode().removeChild(juego);
                guardarXml(doc, new File(XML));
                System.out.println("Videojuego eliminado del XML.");
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    // OPCIÓN 4: toXML() serializa y fromXML() deserializa.
    private static void serializarConXStream() {
        try {
            Videojuego original = new Videojuego(
                    1001, "The Last of Us", "PS5", (short) 2022,
                    49.99f, false, 9.6f, (short) 4,
                    "Aventura", (short) 18, 20000000
            );

            XStream xstream = new XStream(new KXml2Driver());
            xstream.allowTypes(new Class[]{Videojuego.class});
            xstream.alias("videojuego", Videojuego.class);

            String xml = xstream.toXML(original);

            try (FileWriter writer = new FileWriter(XML_XSTREAM)) {
                writer.write(xml);
            }

            Videojuego recuperado = (Videojuego) xstream.fromXML(xml);

            System.out.println("Objeto original:   " + original);
            System.out.println("Objeto recuperado: " + recuperado);
            System.out.println(XML_XSTREAM + " generado.");

        } catch (Exception e) {
            System.out.println("Error con XStream: " + e.getMessage());
        }
    }

    // OPCIÓN 5: genera 6 desarrolladoras y exactamente 100 videojuegos.
    private static void generarXmlMasivo() {
        String[] desarrolladoras = {
                "Mojang", "CD Projekt", "Rockstar Games",
                "Sony Studios", "FromSoftware", "Bethesda"
        };

        String[] paises = {
                "Suecia", "Polonia", "Estados Unidos",
                "Japon", "Japon", "Estados Unidos"
        };

        String[] plataformas = {"PC", "PS5", "Xbox", "Nintendo Switch"};
        String[] generos = {"RPG", "Accion", "Aventura", "Shooter", "Sandbox", "Carreras"};

        try {
            Document doc = nuevoDocumento();
            Element raiz = doc.createElement("desarrolladoras");
            doc.appendChild(raiz);

            int codigoJuego = 1;

            for (int i = 0; i < desarrolladoras.length; i++) {
                Element desarrolladora = doc.createElement("desarrolladora");
                desarrolladora.setAttribute("codigo", String.valueOf(i + 1));
                desarrolladora.setAttribute("nombre", desarrolladoras[i]);
                agregarElemento(doc, desarrolladora, "pais", paises[i]);

                Element juegos = doc.createElement("videojuegos");
                desarrolladora.appendChild(juegos);

                // 17 + 17 + 17 + 17 + 16 + 16 = 100.
                int cantidad = i < 4 ? 17 : 16;

                for (int j = 0; j < cantidad; j++) {
                    Element juego = doc.createElement("videojuego");
                    juego.setAttribute("codigo", String.valueOf(codigoJuego));

                    agregarElemento(doc, juego, "titulo", "Videojuego " + codigoJuego);
                    agregarElemento(doc, juego, "plataforma",
                            plataformas[(codigoJuego - 1) % plataformas.length]);
                    agregarElemento(doc, juego, "anio",
                            String.valueOf(2015 + codigoJuego % 12));
                    agregarElemento(doc, juego, "precio",
                            String.valueOf(19.99f + (codigoJuego % 5) * 10));
                    agregarElemento(doc, juego, "multijugador",
                            codigoJuego % 2 == 0 ? "S" : "N");
                    agregarElemento(doc, juego, "valoracion",
                            String.valueOf(7.0f + (codigoJuego % 30) / 10.0f));
                    agregarElemento(doc, juego, "genero",
                            generos[(codigoJuego - 1) % generos.length]);
                    agregarElemento(doc, juego, "pegi",
                            String.valueOf(new int[]{7, 12, 16, 18}[codigoJuego % 4]));
                    agregarElemento(doc, juego, "ventas",
                            String.valueOf(codigoJuego * 100000));

                    juegos.appendChild(juego);
                    codigoJuego++;
                }

                raiz.appendChild(desarrolladora);
            }

            guardarXml(doc, new File(XML_MASIVO));
            System.out.println(XML_MASIVO + " generado: 6 desarrolladoras y 100 videojuegos.");

        } catch (Exception e) {
            System.out.println("Error al generar XML masivo: " + e.getMessage());
        }
    }

    // OPCIÓN 6: XPath busca elementos mediante expresiones.
    private static void consultarConXPath(Scanner sc) {
        if (!new File(XML_MASIVO).exists()) {
            System.out.println("Primero genera " + XML_MASIVO + " con la opción 5.");
            return;
        }

        try {
            Document doc = cargarXml(XML_MASIVO);
            XPath xpath = XPathFactory.newInstance().newXPath();

            System.out.println("1. Precio superior a una cantidad");
            System.out.println("2. Videojuegos multijugador");
            System.out.println("3. Videojuegos de una desarrolladora");
            System.out.println("4. Expresión XPath manual");
            System.out.print("Consulta: ");

            int opcion = Integer.parseInt(sc.nextLine());
            String expresion;

            switch (opcion) {
                case 1 -> {
                    System.out.print("Precio mínimo: ");
                    float precio = Float.parseFloat(sc.nextLine().replace(',', '.'));
                    expresion = "//videojuego[precio > " + precio + "]";
                }
                case 2 -> expresion = "//videojuego[multijugador='S']";
                case 3 -> {
                    System.out.print("Código de desarrolladora (1-6): ");
                    String codigo = sc.nextLine();
                    expresion = "//desarrolladora[@codigo='" + codigo + "']//videojuego";
                }
                case 4 -> {
                    System.out.print("XPath: ");
                    expresion = sc.nextLine();
                }
                default -> {
                    System.out.println("Consulta no válida.");
                    return;
                }
            }

            NodeList resultados = (NodeList) xpath.evaluate(
                    expresion, doc, XPathConstants.NODESET
            );

            System.out.println("Resultados: " + resultados.getLength());

            for (int i = 0; i < resultados.getLength(); i++) {
                Node nodo = resultados.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE
                        && nodo.getNodeName().equals("videojuego")) {

                    Element juego = (Element) nodo;
                    System.out.println(
                            juego.getAttribute("codigo") + " - "
                            + texto(juego, "titulo") + " - "
                            + texto(juego, "precio") + " €"
                    );
                } else {
                    System.out.println(nodo.getTextContent());
                }
            }

        } catch (Exception e) {
            System.out.println("Error XPath: " + e.getMessage());
        }
    }

    // OPCIÓN 7: XML + XSLT -> HTML.
    private static void generarHtmlConXslt() {
        if (!new File(XML_MASIVO).exists()) {
            System.out.println("Primero genera " + XML_MASIVO + " con la opción 5.");
            return;
        }

        if (!new File(XSL).exists()) {
            System.out.println("No existe " + XSL + ".");
            return;
        }

        try {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(XSL));

            transformer.transform(
                    new StreamSource(XML_MASIVO),
                    new StreamResult(HTML)
            );

            System.out.println(HTML + " generado mediante XSLT.");

        } catch (Exception e) {
            System.out.println("Error XSLT: " + e.getMessage());
        }
    }

    // newDocument() crea un XML nuevo en memoria.
    private static Document nuevoDocumento() throws Exception {
        DocumentBuilder builder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.newDocument();
    }

    // parse() carga un XML existente como árbol DOM en memoria.
    private static Document cargarXml(String nombre) throws Exception {
        DocumentBuilder builder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document doc = builder.parse(new File(nombre));
        doc.getDocumentElement().normalize();
        return doc;
    }

    private static Element buscarJuegoPorCodigo(Document doc, String codigo) {
        NodeList lista = doc.getElementsByTagName("videojuego");

        for (int i = 0; i < lista.getLength(); i++) {
            Element juego = (Element) lista.item(i);
            if (juego.getAttribute("codigo").equals(codigo)) {
                return juego;
            }
        }

        return null;
    }

    private static String texto(Element padre, String etiqueta) {
        return padre.getElementsByTagName(etiqueta).item(0).getTextContent();
    }

    private static void agregarElemento(
            Document doc, Element padre, String nombre, String valor) {

        Element elemento = doc.createElement(nombre);
        elemento.setTextContent(valor);
        padre.appendChild(elemento);
    }

    private static String leerCadena(
            RandomAccessFile raf, int longitud) throws IOException {

        String texto = "";
        for (int i = 0; i < longitud; i++) {
            texto += raf.readChar();
        }
        return texto.trim();
    }

    // Transformer convierte el árbol DOM en un fichero XML físico.
    private static void guardarXml(Document doc, File archivo) throws Exception {
        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "4"
        );

        transformer.transform(
                new DOMSource(doc),
                new StreamResult(archivo)
        );
    }
}
