
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Scanner;

public class AnadirEmpleadoXmlDom {

    public static void main(String[] args) {
        String ficheroXml = "empleados.xml";
        File archivo = new File(ficheroXml);

        // Verificar si el fichero XML existe previamente
        if (!archivo.exists()) {
            System.err.println("El fichero XML '" + ficheroXml + "' no existe. Debes crearlo primero.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- AÃ‘ADIR NUEVO EMPLEADO AL FICHERO XML (DOM) ---");

        try {
            // 1. Pedir los datos del nuevo empleado por pantalla
            System.out.print("Introduce el cÃ³digo del empleado (entero): ");
            int codigo = Integer.parseInt(scanner.nextLine());

            System.out.print("Introduce el nombre (mÃ¡x. 40 caracteres): ");
            String nombre = scanner.nextLine();

            System.out.print("Introduce el correo electrÃ³nico: ");
            String correo = scanner.nextLine();

            System.out.print("Introduce el salario (float): ");
            float salario = Float.parseFloat(scanner.nextLine());

            System.out.print("Â¿Tiene carnet de conducir? (S/N): ");
            String carnetInput = scanner.nextLine().trim().toUpperCase();
            String carnetConducir = carnetInput.startsWith("S") ? "S" : "N";

            System.out.print("Introduce el cÃ³digo de departamento (10, 20, 30, 40, 50): ");
            short departamento = Short.parseShort(scanner.nextLine());

            System.out.print("Introduce el tipo de empleado ('T' TÃ©cnico, 'C' Comercial, 'S' Staff): ");
            char tipoChar = scanner.nextLine().trim().toUpperCase().charAt(0);
            
            String tipoTexto = switch (tipoChar) {
                case 'T' -> "Tecnico";
                case 'C' -> "Comercial";
                case 'S' -> "Staff";
                default -> "Desconocido";
            };

            // 2. Cargar el documento XML actual en la memoria RAM (Ãrbol DOM)
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(archivo);

            // Normalizar el documento
            doc.getDocumentElement().normalize();

            // 3. Obtener el elemento raÃ­z <empleados>
            Element elementoRaiz = doc.getDocumentElement();

            // 4. Crear el nuevo elemento <empleado> y sus atributos
            Element elemEmpleado = doc.createElement("empleado");
            elemEmpleado.setAttribute("codigo", String.valueOf(codigo));
            elemEmpleado.setAttribute("tipo", tipoTexto);

            // Crear elementos hijos
            Element elemNombre = doc.createElement("nombre");
            elemNombre.appendChild(doc.createTextNode(nombre));
            elemEmpleado.appendChild(elemNombre);

            Element elemCorreo = doc.createElement("correo");
            elemCorreo.appendChild(doc.createTextNode(correo));
            elemEmpleado.appendChild(elemCorreo);

            Element elemSalario = doc.createElement("salario");
            elemSalario.appendChild(doc.createTextNode(String.valueOf(salario)));
            elemEmpleado.appendChild(elemSalario);

            Element elemCarnet = doc.createElement("carnetConducir");
            elemCarnet.appendChild(doc.createTextNode(carnetConducir));
            elemEmpleado.appendChild(elemCarnet);

            Element elemDepto = doc.createElement("departamento");
            elemDepto.appendChild(doc.createTextNode(String.valueOf(departamento)));
            elemEmpleado.appendChild(elemDepto);

            // 5. Adjuntar el nuevo nodo hijo al elemento raÃ­z <empleados>
            elementoRaiz.appendChild(elemEmpleado);

            // 6. Volcar (guardar) el Ã¡rbol DOM actualizado de nuevo al fichero XML fÃ­sico
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            // Configurar formato e indentaciÃ³n
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(archivo);
            transformer.transform(source, result);

            System.out.println("\nÂ¡Empleado aÃ±adido correctamente al final del fichero XML '" + ficheroXml + "'!");

        } catch (NumberFormatException e) {
            System.err.println("Error de formato en los datos numÃ©ricos introducidos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al modificar o guardar el fichero XML: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}