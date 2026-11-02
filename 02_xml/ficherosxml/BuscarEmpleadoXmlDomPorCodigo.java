
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Scanner;

public class BuscarEmpleadoXmlDomPorCodigo {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        String ficheroXml = "empleados.xml";
        File archivo = new File(ficheroXml);

        // Validar que el fichero XML existe
        if (!archivo.exists()) {
            System.err.println("El fichero XML '" + ficheroXml + "' no existe. Genera el fichero primero.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- CONSULTA DE EMPLEADO POR CÃ“DIGO (DOM) ---");
        System.out.print("Introduce el cÃ³digo del empleado que deseas buscar: ");
        
        String codigoBuscado;
        try {
            codigoBuscado = scanner.nextLine().trim();
        } catch (Exception e) {
            System.err.println("Error al leer el cÃ³digo introducido.");
            return;
        }

        try {
            // 1. Cargar y parsear el documento XML completo en memoria (Ãrbol DOM)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            // Normalizar el documento para evitar nodos de texto vacÃ­os o fragmentados
            doc.getDocumentElement().normalize();

            // 2. Obtener todos los elementos <empleado> del documento
            NodeList listaEmpleados = doc.getElementsByTagName("empleado");
            boolean encontrado = false;

            // 3. Recorrer la lista buscando el empleado que coincida con el cÃ³digo
            for (int i = 0; i < listaEmpleados.getLength(); i++) {
                Node nodo = listaEmpleados.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementoEmpleado = (Element) nodo;

                    // Leer el atributo "codigo"
                    String codigoActual = elementoEmpleado.getAttribute("codigo");

                    // Comprobar si el cÃ³digo coincide con el buscado
                    if (codigoActual.equals(codigoBuscado)) {
                        encontrado = true;

                        // Extraer el resto de atributos y elementos hijos
                        String tipo = elementoEmpleado.getAttribute("tipo");
                        String nombre = elementoEmpleado.getElementsByTagName("nombre").item(0).getTextContent();
                        String correo = elementoEmpleado.getElementsByTagName("correo").item(0).getTextContent();
                        String salario = elementoEmpleado.getElementsByTagName("salario").item(0).getTextContent();
                        String carnetConducir = elementoEmpleado.getElementsByTagName("carnetConducir").item(0).getTextContent();
                        String departamento = elementoEmpleado.getElementsByTagName("departamento").item(0).getTextContent();

                        // Mostrar los resultados de forma estructurada
                        System.out.println("\n==================================================");
                        System.out.println("            Â¡EMPLEADO ENCONTRADO!");
                        System.out.println("==================================================");
                        System.out.println("  â€¢ CÃ³digo           : " + codigoActual);
                        System.out.println("  â€¢ Tipo             : " + tipo);
                        System.out.println("  â€¢ Nombre           : " + nombre);
                        System.out.println("  â€¢ Correo           : " + correo);
                        System.out.println("  â€¢ Salario          : " + salario + " â‚¬");
                        System.out.println("  â€¢ Carnet Conducir  : " + carnetConducir);
                        System.out.println("  â€¢ Departamento     : " + departamento);
                        System.out.println("==================================================");
                        
                        break; // Terminamos la bÃºsqueda al encontrarlo
                    }
                }
            }

            if (!encontrado) {
                System.out.println("\nNo se ha encontrado ningÃºn empleado con el cÃ³digo '" + codigoBuscado + "' en el fichero XML.");
            }

        } catch (Exception e) {
            System.err.println("Error al procesar el fichero XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}