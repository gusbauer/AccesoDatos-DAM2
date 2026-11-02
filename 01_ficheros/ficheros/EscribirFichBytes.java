package ficheros;

import java.io.*;

public class EscribirFichBytes {
    public static void main(String[] args) throws IOException {

        java.io.File fichero = new java.io.File("ciennumeros2.bin");  // ✅ calificado

        FileOutputStream fileout = new FileOutputStream(fichero, true);  // ✅ ya compila
        FileInputStream filein = new FileInputStream(fichero);          // ✅ ya compila
        int i;

        for (i = 1; i < 2000; i++)
            fileout.write(i);
        fileout.close();

        while ((i = filein.read()) != -1)
            System.out.println(i);
        filein.close();

        System.out.println("Fichero creado correctamente");
    }
}