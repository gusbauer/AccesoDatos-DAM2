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

public class ModificarSalarioXmlDom {

    public static void main(String[] args) {
        String ficheroXml = "empleados.xml";
        File archivo = new File(ficheroXml);

        // Validar que el fichero XML existe
        if (!archivo.exists()) {
            System.err.println("El fichero XML '" + ficheroXml + "' no existe. Genera el fichero primero.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- MODIFICAR SALARIO DE UN EMPLEADO EN XML (DOM) ---");
        System.out.print("Introduce el cÃ³digo del empleado cuyo salario deseas modificar: ");
        String codigoBuscado = scanner.nextLine().trim();

        try {
            // 1. Cargar y parsear el documento XML completo en memoria (Ãrbol DOM)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            doc.getDocumentElement().normalize();

            // 2. Obtener la lista de todos los elementos <empleado>
            NodeList listaEmpleados = doc.getElementsByTagName("empleado");
            boolean encontrado = false;

            // 3. Recorrer la lista buscando el empleado con el cÃ³digo indicado
            for (int i = 0; i < listaEmpleados.getLength(); i++) {
                Node nodoEmpleado = listaEmpleados.item(i);

                if (nodoEmpleado.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoEmpleado = (Element) nodoEmpleado;
                    String codigoActual = elementoEmpleado.getAttribute("codigo");

                    if (codigoActual.equals(codigoBuscado)) {
                        encontrado = true;

                        // Obtener los datos actuales para mostrarlos
                        String nombre = elementoEmpleado.getElementsByTagName("nombre").item(0).getTextContent();
                        Node nodoSalario = elementoEmpleado.getElementsByTagName("salario").item(0);
                        String salarioActual = nodoSalario.getTextContent();

                        System.out.println("\n--------------------------------------------------");
                        System.out.println("            EMPLEADO ENCONTRADO");
                        System.out.println("--------------------------------------------------");
                        System.out.println("  â€¢ CÃ³digo         : " + codigoActual);
                        System.out.println("  â€¢ Nombre         : " + nombre);
                        System.out.println("  â€¢ Salario Actual : " + salarioActual + " â‚¬");
                        System.out.println("--------------------------------------------------");

                        // 4. Pedir el nuevo salario por pantalla
                        System.out.print("Introduce el nuevo salario: ");
                        String nuevoSalarioStr = scanner.nextLine().trim().replace(',', '.');
                        
                        float nuevoSalario;
                        try {
                            nuevoSalario = Float.parseFloat(nuevoSalarioStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: El salario introducido no es vÃ¡lido. OperaciÃ³n cancelada.");
                            return;
                        }

                        // Pedir confirmaciÃ³n
                        System.out.print("Â¿Deseas confirmar el cambio de " + salarioActual + " â‚¬ a " + String.format("%.2f", nuevoSalario) + " â‚¬? (S/N): ");
                        String respuesta = scanner.nextLine().trim();

                        if (respuesta.equalsIgnoreCase("S")) {
                            // 5. Modificar el texto del nodo <salario> en el Ã¡rbol DOM
                            nodoSalario.setTextContent(String.format(java.util.Locale.US, "%.2f", nuevoSalario));

                            // 6. Volcar el Ã¡rbol DOM actualizado de nuevo al fichero XML fÃ­sico
                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            
                            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                            DOMSource source = new DOMSource(doc);
                            StreamResult result = new StreamResult(archivo);
                            transformer.transform(source, result);

                            System.out.println("\nÂ¡El salario ha sido modificado correctamente en el fichero XML!");
                        } else {
                            System.out.println("\nOperaciÃ³n cancelada. No se ha modificado ningÃºn dato.");
                        }

                        break;
                    }
                }
            }

            if (!encontrado) {
                System.out.println("\nNo se ha encontrado ningÃºn empleado con el cÃ³digo '" + codigoBuscado + "' en el fichero XML.");
            }

        } catch (Exception e) {
            System.err.println("Error al procesar o modificar el fichero XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}