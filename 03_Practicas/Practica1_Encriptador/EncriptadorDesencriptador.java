/*
 * ============================================================
 * PRÁCTICA Nº1 - ENCRIPTADOR / DESENCRIPTADOR
 * ============================================================
 *
 * COMPILAR:
 * javac EncriptadorDesencriptador.java
 *
 * EJECUTAR:
 * java EncriptadorDesencriptador
 *
 * FICHERO DE PRUEBA:
 * testP1.txt
 *
 * La opción 1 genera:
 * encriptado.txt
 *
 * Para probar:
 * 1. Ejecutar el programa.
 * 2. Elegir la opción 1.
 * 3. Introducir testP1.txt.
 * 4. Introducir una contraseña.
 * 5. Se crea encriptado.txt.
 * 6. Elegir la opción 2.
 * 7. Introducir encriptado.txt y la misma contraseña.
 *
 * TIPO:
 * Fichero de texto con acceso secuencial.
 * ============================================================
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/*
 * ============================================================
 * PRÁCTICA Nº1 - ENCRIPTADOR / DESENCRIPTADOR DE FICHEROS
 * ============================================================
 *
 * COMPILAR:
 * javac EncriptadorDesencriptador.java
 *
 * EJECUTAR:
 * java EncriptadorDesencriptador
 *
 * ============================================================
 * TIPO DE FICHERO
 * ============================================================
 *
 * Esta práctica trabaja con FICHEROS DE TEXTO.
 *
 * El acceso es SECUENCIAL porque el contenido se lee
 * desde el principio hasta el final, carácter a carácter.
 *
 * No utilizamos acceso directo ni seek() como en la práctica 2.
 *
 * ============================================================
 * CLASES UTILIZADAS
 * ============================================================
 *
 * File:
 * Representa un fichero o una ruta.
 * IMPORTANTE: File NO lee ni escribe el contenido.
 *
 * Se utiliza para:
 * - representar el fichero;
 * - comprobar si existe con exists();
 * - comprobar si realmente es un fichero con isFile().
 *
 *
 * FileReader:
 * Permite leer caracteres de un fichero de texto.
 *
 *
 * BufferedReader:
 * Envuelve al FileReader y permite realizar la lectura
 * de forma más eficiente.
 *
 * Métodos utilizados:
 *
 * read()     -> lee un carácter.
 * readLine() -> lee una línea completa.
 *
 *
 * FileWriter:
 * Permite escribir caracteres en un fichero de texto.
 *
 *
 * BufferedWriter:
 * Envuelve al FileWriter y permite escribir de forma
 * más eficiente.
 *
 * Métodos utilizados:
 *
 * write()   -> escribe texto o caracteres.
 * newLine() -> escribe un salto de línea.
 *
 *
 * Scanner:
 * Se utiliza para leer datos introducidos por teclado.
 *
 *
 * IOException:
 * Permite controlar errores relacionados con lectura
 * o escritura de ficheros.
 *
 * ============================================================
 * DE DÓNDE SALE EL FICHERO
 * ============================================================
 *
 * El usuario introduce por teclado el nombre del fichero:
 *
 * String nombreFichero = scanner.nextLine();
 *
 * Por ejemplo:
 *
 * testP1.txt
 *
 * Después:
 *
 * File ficheroEntrada = new File(nombreFichero);
 *
 * File representa ese fichero.
 *
 * ============================================================
 * LECTURA
 * ============================================================
 *
 * El fichero se abre para lectura así:
 *
 * BufferedReader lector =
 *     new BufferedReader(new FileReader(ficheroEntrada));
 *
 * FileReader accede al fichero.
 * BufferedReader realiza la lectura.
 *
 * Para leer carácter a carácter:
 *
 * lector.read()
 *
 * read() devuelve -1 cuando llega al final del fichero.
 *
 * Para leer una línea completa:
 *
 * lector.readLine()
 *
 * En la desencriptación se utiliza readLine() para leer
 * la primera línea donde está almacenada la contraseña.
 *
 * ============================================================
 * ESCRITURA
 * ============================================================
 *
 * El fichero de salida se abre así:
 *
 * BufferedWriter escritor =
 *     new BufferedWriter(new FileWriter(ficheroSalida));
 *
 * Se escribe utilizando:
 *
 * escritor.write(...)
 *
 * Y para añadir un salto de línea:
 *
 * escritor.newLine()
 *
 * El resultado de la encriptación se guarda en:
 *
 * encriptado.txt
 *
 * ============================================================
 * ENCRIPTACIÓN
 * ============================================================
 *
 * Se lee el fichero carácter a carácter.
 *
 * Cada carácter se busca en la cadena ENTRADA:
 *
 * ENTRADA.indexOf(caracter)
 *
 * Si existe, obtenemos la misma posición en SALIDA:
 *
 * SALIDA.charAt(posicion)
 *
 * Ejemplo conceptual:
 *
 * ENTRADA -> buscar posición
 * SALIDA  -> obtener carácter de esa posición
 *
 * Si el carácter no aparece en ENTRADA, se deja sin modificar.
 *
 * ============================================================
 * DESENCRIPTACIÓN
 * ============================================================
 *
 * Es el proceso inverso:
 *
 * SALIDA -> buscar posición
 * ENTRADA -> recuperar carácter original
 *
 * Antes se comprueba que la contraseña introducida coincida
 * con la almacenada en la primera línea.
 *
 * ============================================================
 * TRY-WITH-RESOURCES
 * ============================================================
 *
 * try (
 *     BufferedReader lector = ...;
 *     BufferedWriter escritor = ...
 * ) {
 *
 * }
 *
 * Hace que los ficheros se cierren automáticamente cuando
 * termina el bloque, incluso si ocurre una excepción.
 *
 * ============================================================
 */ 
/*   
* Si te pregunta «¿qué tipo de fichero es?»:
Es un fichero de texto y se trabaja de forma secuencial, porque lo leo carácter a carácter desde el principio hasta el final.

* Si pregunta «¿qué hace File?»:
File representa el fichero o su ruta, pero no lee ni escribe su contenido. Lo utilizo para comprobar si existe y si es realmente un fichero.

* Si pregunta «¿qué clases utilizas para leer?»:
FileReader y BufferedReader.

* Si pregunta «¿para escribir?»:
FileWriter y BufferedWriter.

* Si pregunta «¿cómo lees?»:
lector.read()

Para leer carácter a carácter.

Y:
lector.readLine()

Para leer una línea completa.

* Si pregunta «¿cómo escribes?»:
escritor.write(...)

* Si pregunta «¿de dónde coge el fichero?»:
El usuario introduce el nombre mediante Scanner; después creo un objeto File con ese nombre.

* Si pregunta «¿qué genera?»:
encriptado.txt

* Si pregunta «¿cómo encripta?»:
Busca cada carácter en ENTRADA con indexOf() y utiliza esa posición para obtener el carácter correspondiente de SALIDA con charAt().
*/

public class EncriptadorDesencriptador {

    private static final String ENTRADA =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String SALIDA =
            "jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi5678901234";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        // do-while: el menú se muestra al menos una vez
        // y se repite hasta elegir la opción 3.    
        do {

            System.out.println();
            System.out.println("Elige una opción:");
            System.out.println("1 .- Encriptar un fichero");
            System.out.println("2 .- Desencriptar un fichero");
            System.out.println("3 .- Salir");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                // switch ejecuta una operación distinta según la opción elegida.
                switch (opcion) {

                    case 1:
                        encriptarFichero(scanner);
                        break;

                    case 2:
                        desencriptarFichero(scanner);
                        break;

                    case 3:
                        System.out.println("Programa finalizado.");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Error: debes introducir una opción numérica.");
            }

        } while (opcion != 3);

        scanner.close();
    }

    private static void encriptarFichero(Scanner scanner) {

        System.out.println();
        System.out.println("Introduce el nombre del fichero:");
        String nombreFichero = scanner.nextLine();
        
        // File representa la ruta/fichero.
        // No lee ni escribe el contenido.
        File ficheroEntrada = new File(nombreFichero); // Creamos un objeto File para el fichero de entrada

        // exists() comprueba que exista.
// isFile() comprueba que sea un fichero y no, por ejemplo, un directorio.
        if (!ficheroEntrada.exists() || !ficheroEntrada.isFile()) {
            System.out.println();
            System.out.println("Error: el fichero no existe");
            return;
        }

        System.out.println();
        System.out.println("Introduce una contrasena para encriptar:");
        String contrasenia = scanner.nextLine();

        File ficheroSalida = new File("encriptado.txt");

        // FileReader lee caracteres del fichero.
        // BufferedReader añade una lectura con búfer.
        //
        // FileWriter escribe caracteres.
        // BufferedWriter añade una escritura con búfer.
        try (
                BufferedReader lector =
                        new BufferedReader(new FileReader(ficheroEntrada));

                BufferedWriter escritor =
                        new BufferedWriter(new FileWriter(ficheroSalida))
        ) {

            escritor.write("Contrasenia:" + contrasenia); // escribimos la contraseña en la primera línea
            escritor.newLine();

            int caracter;

            // read() lee un carácter y devuelve un int.
            // Devuelve -1 cuando se alcanza el final del fichero.
            // Por eso recorremos el fichero secuencialmente.
            /*  por qué int caracter y no char:
            Porque read() necesita poder devolver también -1 para indicar el final del fichero. */
            while ((caracter = lector.read()) != -1) {

                char caracterOriginal = (char) caracter;

                // indexOf() busca la posición del carácter.
                // Si no lo encuentra devuelve -1.
                int posicion = ENTRADA.indexOf(caracterOriginal);

                if (posicion != -1) {
                    // charAt() obtiene el carácter que ocupa esa misma posición
                    // en la cadena SALIDA.
                    escritor.write(SALIDA.charAt(posicion));
                } else {
                    escritor.write(caracterOriginal);
                }
            }

            System.out.println();
            System.out.println("Mensaje encriptado");
            System.out.println("Fichero creado correctamente: encriptado.txt");

        } catch (IOException e) {
            System.out.println("Error al encriptar el fichero: " + e.getMessage());
        }
    }

    private static void desencriptarFichero(Scanner scanner) {

        System.out.println();
        System.out.println("Introduce el nombre del fichero:");
        String nombreFichero = scanner.nextLine();

        File fichero = new File(nombreFichero);

        if (!fichero.exists() || !fichero.isFile()) {
            System.out.println();
            System.out.println("Error: el fichero no existe");
            return;
        }

        System.out.println();
        System.out.println("Introduce una contrasena para desencriptar:");
        String contraseniaIntroducida = scanner.nextLine();

        try (
                BufferedReader lector =
                        new BufferedReader(new FileReader(fichero))
        ) {
            
            // readLine() lee una línea completa.
            // Aquí leemos la primera línea donde se guardó la contraseña.  
            String primeraLinea = lector.readLine();  // leemos la primera línea del fichero

            if (primeraLinea == null ||
                    !primeraLinea.startsWith("Contrasenia:")) {

                System.out.println("Error: el fichero no tiene el formato esperado");
                return;
            }

            String contraseniaGuardada =
                    primeraLinea.substring("Contrasenia:".length());

            // equals() compara el contenido de dos String.
            // No utilizamos == para comparar textos.
            if (!contraseniaGuardada.equals(contraseniaIntroducida)) {

                System.out.println();
                System.out.println("Error: contrasena no valida");
                return;
            }

            System.out.println();
            System.out.println("Mensaje desencriptado");
            System.out.println();

            int caracter;

            while ((caracter = lector.read()) != -1) { // lo leemos caracter a caracter 

                char caracterEncriptado = (char) caracter;

                int posicion = SALIDA.indexOf(caracterEncriptado);

                if (posicion != -1) {
                    System.out.print(ENTRADA.charAt(posicion));
                } else {
                    System.out.print(caracterEncriptado);
                }
            }

            System.out.println();

        } catch (IOException e) {
            System.out.println("Error al desencriptar el fichero: " + e.getMessage());
        }
    }
}