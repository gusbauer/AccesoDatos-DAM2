package ficheros;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

public class LeerCochesBinario {

    public static void main(String[] args) {

        String rutaFichero = "coches.dat";

        try (DataInputStream entrada =
                     new DataInputStream(
                             new FileInputStream(rutaFichero))) {

            System.out.println(
                "--- LEYENDO FICHERO BINARIO DE COCHES ---"
            );

            int contador = 1;

            while (true) {

                try {

                    String modelo = entrada.readUTF();
                    int cilindrada = entrada.readInt();
                    long precio = entrada.readLong();
                    boolean hibrido = entrada.readBoolean();
                    float alto = entrada.readFloat();
                    float largo = entrada.readFloat();
                    char color = entrada.readChar();

                    System.out.println("\nCoche #" + contador + ":");
                    System.out.println("Modelo: " + modelo);
                    System.out.println(
                        "Cilindrada: " + cilindrada + " cc"
                    );
                    System.out.println(
                        "Precio: " + precio + " €"
                    );
                    System.out.println(
                        "Híbrido: " + (hibrido ? "Sí" : "No")
                    );
                    System.out.println(
                        "Alto: " + alto + " m"
                    );
                    System.out.println(
                        "Largo: " + largo + " m"
                    );
                    System.out.println(
                        "Color: " + color
                    );

                    contador++;

                } catch (EOFException e) {

                    break;
                }
            }

        } catch (IOException e) {

            System.out.println(
                "Error al leer el fichero: " + e.getMessage()
            );
        }
    }
}
