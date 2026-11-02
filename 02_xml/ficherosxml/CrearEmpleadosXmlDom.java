

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
import java.io.RandomAccessFile;

public class CrearEmpleadosXmlDom {

    // Tamaño de cada registro en bytes (173 bytes)
    private static final int TAMANIO_REGISTRO = 173;

    public static void main(String[] args) {
        String ficheroBinario = "empleados.dat";
        String ficheroXml = "empleados.xml";

        File archivo = new File(ficheroBinario);
        if (!archivo.exists()) {
            System.err.println("El fichero binario '" + ficheroBinario + "' no existe.");
            return;
        }

        System.out.println("Leyendo el fichero binario y generando el XML con DOM (modificado)...");

        try {
            // 1. Inicializar la factoría y crear el documento DOM en memoria
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Crear el elemento raíz <empleados>
            Element elementoRaiz = doc.createElement("empleados");
            doc.appendChild(elementoRaiz);

            // 2. Abrir el fichero binario en modo lectura ("r")
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                
                // Recorrer el fichero de forma secuencial
                while (raf.getFilePointer() < raf.length()) {
                    
                    // 1. Leer Código (int - 4 bytes)
                    int codigo = raf.readInt();

                    // 2. Leer Nombre (40 caracteres - 80 bytes)
                    char[] nombreChars = new char[40];
                    for (int i = 0; i < 40; i++) {
                        nombreChars[i] = raf.readChar();
                    }
                    String nombre = new String(nombreChars).trim();

                    // 3. Leer Correo (40 caracteres - 80 bytes)
                    char[] correoChars = new char[40];
                    for (int i = 0; i < 40; i++) {
                        correoChars[i] = raf.readChar();
                    }
                    String correo = new String(correoChars).trim();

                    // 4. Leer Salario (float - 4 bytes)
                    float salario = raf.readFloat();

                    // 5. Leer Carnet de conducir (boolean - 1 byte)
                    boolean carnet = raf.readBoolean();

                    // 6. Leer Departamento (short - 2 bytes)
                    short departamento = raf.readShort();

                    // 7. Leer Tipo de empleado (char - 2 bytes)
                    char tipoChar = raf.readChar();

                    // Control de borrado lógico (si el código es -1, se ignora)
                    if (codigo == -1) {
                        continue;
                    }

                    // Traducir el carnet de boolean a 'S' o 'N'
                    String carnetTexto = carnet ? "S" : "N";

                    // Traducir el tipo de empleado a texto descriptivo ('T'->Tecnico, 'C'->Comercial, 'S'->Staff)
                    String tipoTexto = switch (tipoChar) {
                        case 'T' -> "Tecnico";
                        case 'C' -> "Comercial";
                        case 'S' -> "Staff";
                        default -> "Desconocido";
                    };

                    // 3. Crear la estructura de nodos DOM para este empleado
                    Element elemEmpleado = doc.createElement("empleado");
                    
                    // Atributos del elemento <empleado>
                    elemEmpleado.setAttribute("codigo", String.valueOf(codigo));
                    elemEmpleado.setAttribute("tipo", tipoTexto); 
                    // El tipo de empleado como atributo

                    // Elemento <nombre>
                    Element elemNombre = doc.createElement("nombre");
                    elemNombre.appendChild(doc.createTextNode(nombre));
                    elemEmpleado.appendChild(elemNombre);

                    // Elemento <correo>
                    Element elemCorreo = doc.createElement("correo");
                    elemCorreo.appendChild(doc.createTextNode(correo));
                    elemEmpleado.appendChild(elemCorreo);

                    // Elemento <salario>
                    Element elemSalario = doc.createElement("salario");
                    elemSalario.appendChild(doc.createTextNode(String.valueOf(salario)));
                    elemEmpleado.appendChild(elemSalario);

                    // Elemento <carnetConducir> ('S' o 'N')
                    Element elemCarnet = doc.createElement("carnetConducir");
                    elemCarnet.appendChild(doc.createTextNode(carnetTexto));
                    elemEmpleado.appendChild(elemCarnet);

                    // Elemento <departamento>
                    Element elemDepto = doc.createElement("departamento");
                    elemDepto.appendChild(doc.createTextNode(String.valueOf(departamento)));
                    elemEmpleado.appendChild(elemDepto);

                    // Agregar el empleado completo al elemento raíz
                    elementoRaiz.appendChild(elemEmpleado);
                }
            }

            // 4. Volcar el árbol DOM de la memoria al fichero XML físico
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            // Propiedades de formato e indentación para legibilidad
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            
            // ¡AQUÍ ES DONDE SE INDICA EL DOCTYPE ANTES DE TRANSFORMAR!
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "empleados.dtd");
            
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(ficheroXml));
            transformer.transform(source, result);

            System.out.println("¡Fichero XML '" + ficheroXml + "' generado correctamente con las modificaciones!");

        } catch (Exception e) {
            System.err.println("Error durante la generación del XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}