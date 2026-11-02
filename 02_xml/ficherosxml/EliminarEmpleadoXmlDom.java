

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Scanner;

public class EliminarEmpleadoXmlDom {

    public static void main(String[] args) {
        String ficheroXml = "empleados.xml";
        File archivo = new File(ficheroXml);

        // Validar que el fichero XML existe
        if (!archivo.exists()) {
            System.err.println("El fichero XML '" + ficheroXml + "' no existe. Genera el fichero primero.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- ELIMINAR EMPLEADO EN XML (DOM) ---");
        System.out.print("Introduce el código del empleado que deseas eliminar: ");
        
        String codigoBuscado = scanner.nextLine().trim();

        try {
            // 1. Cargar y parsear el documento XML completo en memoria (Árbol DOM)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            doc.getDocumentElement().normalize();

            // 2. Obtener la lista de todos los elementos <empleado>
            NodeList listaEmpleados = doc.getElementsByTagName("empleado");
            boolean encontrado = false;

            // 3. Recorrer la lista buscando el empleado con el código indicado
            for (int i = 0; i < listaEmpleados.getLength(); i++) {
                Node nodo = listaEmpleados.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoEmpleado = (Element) nodo;
                    String codigoActual = elementoEmpleado.getAttribute("codigo");

                    if (codigoActual.equals(codigoBuscado)) {
                        encontrado = true;

                        // Mostrar los datos del empleado antes de borrar
                        String nombre = elementoEmpleado.getElementsByTagName("nombre").item(0).getTextContent();
                        String correo = elementoEmpleado.getElementsByTagName("correo").item(0).getTextContent();
                        
                        System.out.println("\nEmpleado encontrado:");
                        System.out.println("  • Código: " + codigoActual);
                        System.out.println("  • Nombre: " + nombre);
                        System.out.println("  • Correo: " + correo);

                        // Pedir confirmación
                        System.out.print("\n¿Estás seguro de que deseas eliminar este empleado? (S/N): ");
                        String respuesta = scanner.nextLine().trim();

                        if (respuesta.equalsIgnoreCase("S")) {
                            // Obtener el elemento raíz (<empleados>) y eliminar el nodo hijo
                            Node elementoRaiz = doc.getDocumentElement();
                            elementoRaiz.removeChild(nodo);

                            // 4. Volcar el árbol DOM actualizado de nuevo al fichero XML físico
                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            
                            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                            DOMSource source = new DOMSource(doc);
                            StreamResult result = new StreamResult(archivo);
                            transformer.transform(source, result);

                            System.out.println("\n¡Empleado eliminado correctamente del fichero XML!");
                        } else {
                            System.out.println("\nOperación cancelada. No se ha eliminado ningún empleado.");
                        }

                        break;
                    }
                }
            }

            if (!encontrado) {
                System.out.println("\nNo se ha encontrado ningún empleado con el código '" + codigoBuscado + "' en el fichero XML.");
            }

        } catch (Exception e) {
            System.err.println("Error al procesar o modificar el fichero XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}