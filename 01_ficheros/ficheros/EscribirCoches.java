package ficheros;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EscribirCoches {

    public static void main(String[] args) {
        String rutaFichero = "coches2.dat";

        // Arrays de datos con la informaciÃ³n de los 10 coches
        String[] modelos = {
            "Toyota Corolla", "Seat Leon", "Tesla Model 3", "Ford Focus", "Hyundai Tucson",
            "BMW Serie 3", "Audi A4", "Peugeot 3008", "Renault Clio", "Volkswagen Golf"
        };
        int[] cilindradas = {1800, 1500, 0, 1000, 1600, 2000, 2000, 1200, 1000, 1500};
        long[] precios = {28500L, 24000L, 41000L, 22500L, 31000L, 45000L, 43500L, 29000L, 18500L, 27000L};
        boolean[] hibridos = {true, false, false, false, true, true, false, true, false, false};
        float[] altos = {1.435f, 1.456f, 1.443f, 1.452f, 1.650f, 1.442f, 1.428f, 1.624f, 1.440f, 1.491f};
        float[] largos = {4.370f, 4.368f, 4.694f, 4.378f, 4.500f, 4.709f, 4.762f, 4.447f, 4.050f, 4.284f};
        
        // Array para el atributo color: 'R' (Rojo), 'A' (Amarillo), 'Z' (Azul), 'V' (Verde)
        char[] colores = {'R', 'A', 'Z', 'V', 'R', 'Z', 'V', 'A', 'R', 'Z'};

        System.out.println("Escribiendo los datos de los 10 coches en el fichero binario...");

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(rutaFichero))) {

            for (int i = 0; i < 10; i++) {
                // writeUTF escribe 2 bytes con la longitud en bytes + el texto en codificaciÃ³n UTF-8 variable (1 a 4 bytes/char)
                dos.writeUTF(modelos[i]); 

                // writeInt escribe un entero firmado de 32 bits en formato de 4 bytes
                dos.writeInt(cilindradas[i]); 

                // writeLong escribe un entero largo firmado de 64 bits en formato de 8 bytes
                dos.writeLong(precios[i]); 

                // writeBoolean escribe un valor booleano en formato de 1 byte (0x01 para true, 0x00 para false)
                dos.writeBoolean(hibridos[i]); 

                // writeFloat escribe un nÃºmero en coma flotante de 32 bits segÃºn el estÃ¡ndar IEEE 754 en formato de 4 bytes
                dos.writeFloat(altos[i]); 

                // writeFloat escribe un nÃºmero en coma flotante de 32 bits segÃºn el estÃ¡ndar IEEE 754 en formato de 4 bytes
                dos.writeFloat(largos[i]); 

                // writeChar escribe un carÃ¡cter Unicode de 16 bits en formato de 2 bytes
                dos.writeChar(colores[i]);
            }

            System.out.println("Fichero binario '" + rutaFichero + "' creado con Ã©xito.");

        } catch (IOException e) {
            System.err.println("Error al escribir en el fichero: " + e.getMessage());
        }
    }
}