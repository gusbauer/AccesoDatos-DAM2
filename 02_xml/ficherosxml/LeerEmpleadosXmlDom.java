

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class LeerEmpleadosXmlDom {

    public static void main(String[] args) {
        String ficheroXml = "empleados.xml";
        File archivo = new File(ficheroXml);

        // Validar que el fichero XML existe
        if (!archivo.exists()) {
            System.err.println("El fichero XML '" + ficheroXml + "' no existe. Genera el XML primero.");
            return;
        }

        System.out.println("--- LEYENDO Y CONSULTANDO EL FICHERO XML MEDIANTE DOM ---");

        try {
            // 1. Inicializar la factorÃ­a para construir el analizador DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // 2. Parsear el documento XML completo cargÃ¡ndolo en la memoria RAM (Ãrbol de Nodos)
            Document doc = dBuilder.parse(archivo);

            // Normalizar el documento para evitar problemas con espacios en blanco entre nodos
            doc.getDocumentElement().normalize();

            // Obtener todos los elementos <empleado> del documento
            NodeList listaEmpleados = doc.getElementsByTagName("empleado");
            System.out.println("Total de empleados encontrados en el XML: " + listaEmpleados.getLength() + "\n");

            // 3. Recorrer la lista de nodos <empleado>
            for (int i = 0; i < listaEmpleados.getLength(); i++) {
                Node nodo = listaEmpleados.item(i);

                // Comprobar que el nodo actual es un Elemento XML
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoEmpleado = (Element) nodo;

                    // Leer atributos del elemento <empleado>
                    String codigo = elementoEmpleado.getAttribute("codigo");
                    String tipo = elementoEmpleado.getAttribute("tipo");

                    // Leer los elementos hijos (etiquetas internas)
                    String nombre = elementoEmpleado.getElementsByTagName("nombre").item(0).getTextContent();
                    String correo = elementoEmpleado.getElementsByTagName("correo").item(0).getTextContent();
                    String salario = elementoEmpleado.getElementsByTagName("salario").item(0).getTextContent();
                    String carnetConducir = elementoEmpleado.getElementsByTagName("carnetConducir").item(0).getTextContent();
                    String departamento = elementoEmpleado.getElementsByTagName("departamento").item(0).getTextContent();

                    // Mostrar los datos formateados por pantalla
                    System.out.printf("Empleado #%d:%n", (i + 1));
                    System.out.printf("  - CÃ³digo: %s%n", codigo);
                    System.out.printf("  - Tipo: %s%n", tipo);
                    System.out.printf("  - Nombre: %s%n", nombre);
                    System.out.printf("  - Correo: %s%n", correo);
                    System.out.printf("  - Salario: %s â‚¬%n", salario);
                    System.out.printf("  - Carnet de Conducir: %s%n", carnetConducir);
                    System.out.printf("  - Departamento: %s%n", departamento);
                    System.out.println("--------------------------------------------------");
                }
            }

            System.out.println("Lectura del fichero XML finalizada con Ã©xito.");

        } catch (Exception e) {
            System.err.println("Error al leer o parsear el fichero XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}