import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.Scanner;

public class ConsultaXPathV1 {

    public static void main(String[] args) {
        String ficheroXml = "Departamentos_empleados.xml";
        File archivo = new File(ficheroXml);

        if (!archivo.exists()) {
            System.err.println("El fichero XML '" + ficheroXml + "' no existe. Genera el fichero primero.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- CONSULTA XPATH EN FICHERO DE EMPLEADOS ---");
        System.out.println("Ejemplos:");
        System.out.println("  - Nodos: /departamentos/departamento[@codigo='20']/empleado");
        System.out.println("  - Atributos: //empleado/@tipo");
        System.out.println("  - Conteo: count(/departamentos/departamento[@codigo='20']/empleado)");
        System.out.print("Introduce la sentencia XPath a evaluar: ");
        String expresionXPath = scanner.nextLine().trim();

        try {
            // 1. Cargar y parsear el documento XML en memoria mediante DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);
            doc.getDocumentElement().normalize();

            // 2. Inicializar el evaluador XPath
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            // Normalizamos la expresión a minúsculas para comprobar si es una función numérica
            String exprLower = expresionXPath.toLowerCase();

            // 3. Comprobar si la consulta es una función de agregación numérica (como count)
            if (exprLower.startsWith("count") || exprLower.startsWith("sum")) {
                
                // Evaluamos obteniendo un resultado numérico (Double)
                Double resultadoNumerico = (Double) xpath.evaluate(expresionXPath, doc, XPathConstants.NUMBER);
                
                System.out.println("\n--- Resultado de la función XPath ---");
                System.out.println("Valor obtenido: " + resultadoNumerico);

            } else {
                
                // Evaluación convencional para conjuntos de nodos (NodeSet)
                NodeList nodeList = (NodeList) xpath.evaluate(expresionXPath, doc, XPathConstants.NODESET);

                System.out.println("\nResultados de la búsqueda (Nodos encontrados: " + nodeList.getLength() + "):");
                System.out.println("--------------------------------------------------");

                if (nodeList.getLength() == 0) {
                    System.out.println("No se han encontrado elementos que coincidan con la expresión XPath.");
                } else {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node nodo = nodeList.item(i);

                        // Si el nodo es de tipo Elemento XML
                        if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) nodo;

                            // Si el nodo evaluado es un <empleado>, mostramos sus datos estructurados
                            if (elem.getTagName().equals("empleado")) {
                                String codigo = elem.getAttribute("codigo");
                                String tipo = elem.getAttribute("tipo");
                                String nombre = elem.getElementsByTagName("nombre").item(0).getTextContent();
                                String correo = elem.getElementsByTagName("correo").item(0).getTextContent();
                                String salario = elem.getElementsByTagName("salario").item(0).getTextContent();
                                String carnet = elem.getElementsByTagName("carnetConducir").item(0).getTextContent();
                                String departamento = elem.getElementsByTagName("departamento").item(0).getTextContent();

                                System.out.printf("Resultado #%d:%n", (i + 1));
                                System.out.printf("  - Código: %s | Tipo: %s%n", codigo, tipo);
                                System.out.printf("  - Nombre: %s%n", nombre);
                                System.out.printf("  - Correo: %s%n", correo);
                                System.out.printf("  - Salario: %s €%n", salario);
                                System.out.printf("  - Carnet: %s | Departamento: %s%n", carnet, departamento);
                                System.out.println("--------------------------------------------------");
                            } else {
                                // Si la sentencia XPath apunta a un elemento interno (ej: /departamentos/departamento/nombre)
                                System.out.printf("Elemento <%s>: %s%n", elem.getTagName(), elem.getTextContent());
                                System.out.println("--------------------------------------------------");
                            }
                        } 
                        // Si el nodo seleccionado es un Atributo (ej: //empleado/@tipo)
                        else if (nodo.getNodeType() == Node.ATTRIBUTE_NODE) {
                            System.out.printf("Resultado #%d [Atributo %s]: %s%n", (i + 1), nodo.getNodeName(), nodo.getNodeValue());
                            System.out.println("--------------------------------------------------");
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al evaluar la sentencia XPath: " + e.getMessage());
            System.err.println("Comprueba que la sintaxis de la expresión XPath sea correcta.");
        } finally {
            scanner.close();
        }
    }
}
    

