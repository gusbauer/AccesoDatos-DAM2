package ficheros;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LeerCoches {

    public static void main(String[] args) {
        String rutaFichero = "coches2.dat";
        File fichero = new File(rutaFichero);

        if (!fichero.exists() || !fichero.isFile()) {
            System.err.println("El fichero " + rutaFichero + " no existe.");
            return;
        }

        System.out.println("--- LEYENDO FICHERO BINARIO DE COCHES ---");

        try (DataInputStream dis = new DataInputStream(new FileInputStream(fichero))) {

            int contador = 1;

            // Bucle para leer todos los registros secuencialmente
            while (true) {
                // Lectura en el mismo orden exacto en el que se escribieron los datos:
                String modelo = dis.readUTF();
                int cilindrada = dis.readInt();
                long precio = dis.readLong();
                boolean hibrido = dis.readBoolean();
                float alto = dis.readFloat();
                float largo = dis.readFloat();
                char codigoColor = dis.readChar(); // Lectura del tipo char (2 bytes)

                // InterpretaciÃ³n del cÃ³digo char a texto completo
                String colorTexto;
                switch (Character.toUpperCase(codigoColor)) {
                    case 'R': colorTexto = "Rojo"; break;
                    case 'A': colorTexto = "Amarillo"; break;
                    case 'Z': colorTexto = "Azul"; break;
                    case 'V': colorTexto = "Verde"; break;
                    default:  colorTexto = "Desconocido (" + codigoColor + ")"; break;
                }

                // Muestra de resultados por pantalla
                System.out.printf("Coche #%d:%n", contador);
                System.out.printf("  Modelo: %s%n", modelo);
                System.out.printf("  Cilindrada: %d cc%n", cilindrada);
                System.out.printf("  Precio: %d â‚¬%n", precio);
                System.out.printf("  HÃ­brido: %s%n", hibrido ? "SÃ­" : "No");
                System.out.printf("  Dimensiones (Alto x Largo): %.3f m x %.3f m%n", alto, largo);
                System.out.printf("  Color: %s (%c)%n", colorTexto, codigoColor);
                System.out.println("----------------------------------------");

                contador++;
            }

        } catch (EOFException e) {
            // EOFException indica el final del fichero binario
            System.out.println("Lectura finalizada. Se han procesado todos los coches.");
        } catch (IOException e) {
            System.err.println("Error al leer el fichero binario: " + e.getMessage());
        }
    }
}