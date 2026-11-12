import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EncriptadorDesencriptador {

    private static final String ENTRADA =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final String SALIDA =
            "jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi5678901234";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {

            System.out.println();
            System.out.println("Elige una opción:");
            System.out.println("1 .- Encriptar un fichero");
            System.out.println("2 .- Desencriptar un fichero");
            System.out.println("3 .- Salir");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

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

        File ficheroEntrada = new File(nombreFichero);

        if (!ficheroEntrada.exists() || !ficheroEntrada.isFile()) {
            System.out.println();
            System.out.println("Error: el fichero no existe");
            return;
        }

        System.out.println();
        System.out.println("Introduce una contrasena para encriptar:");
        String contrasenia = scanner.nextLine();

        File ficheroSalida = new File("encriptado.txt");

        try (
                BufferedReader lector =
                        new BufferedReader(new FileReader(ficheroEntrada));

                BufferedWriter escritor =
                        new BufferedWriter(new FileWriter(ficheroSalida))
        ) {

            escritor.write("Contrasenia:" + contrasenia);
            escritor.newLine();

            int caracter;

            while ((caracter = lector.read()) != -1) {

                char caracterOriginal = (char) caracter;

                int posicion = ENTRADA.indexOf(caracterOriginal);

                if (posicion != -1) {
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

            String primeraLinea = lector.readLine();

            if (primeraLinea == null ||
                    !primeraLinea.startsWith("Contrasenia:")) {

                System.out.println("Error: el fichero no tiene el formato esperado");
                return;
            }

            String contraseniaGuardada =
                    primeraLinea.substring("Contrasenia:".length());

            if (!contraseniaGuardada.equals(contraseniaIntroducida)) {

                System.out.println();
                System.out.println("Error: contrasena no valida");
                return;
            }

            System.out.println();
            System.out.println("Mensaje desencriptado");
            System.out.println();

            int caracter;

            while ((caracter = lector.read()) != -1) {

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