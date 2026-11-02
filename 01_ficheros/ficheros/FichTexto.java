package ficheros;

import java.io.*;

public class FichTexto {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {

        java.io.File fichero = new java.io.File("edades.bin");   // ✅
        FileReader fic = new FileReader(fichero);
        int i;

        while ((i = fic.read()) != -1)
            System.out.print((char) i);
        fic.close();

        fic = new FileReader(fichero);
        char b[] = new char[20];
        while ((i = fic.read(b)) != -1) {
            System.out.print(new String(b, 0, i));   // ✅ solo lo leído
        }
        fic.close();

        /*
        java.io.File fichesal = new java.io.File("c:\\clase\\Pruebadam24610.txt");
        FileWriter ficsal = new FileWriter(fichesal);
        String cadena = "Esto es una prueba del FileWriter, de como se escribe.";
        System.out.println(cadena);
        char[] cad = cadena.toCharArray();
        for (i = 0; i < cad.length; i++)
            ficsal.write(cad[i]);
        ficsal.append('*');
        ficsal.write(cad);
        ficsal.close();
        System.out.println("Fichero escrito");
        */
    }
}