package ficheros;
import java.io.*;
public class FichTexto {
  @SuppressWarnings("resource")
public static void main(String[] args) throws IOException {
    File fichero = new File("edades.bin");//declarar fichero
    FileReader fic = new FileReader(fichero); //crear el flujo de entrada  
    int i;
    while ((i = fic.read()) != -1) //se va leyendo un carÃ¡cter
      System.out.print( (char) i);
    fic.close(); //cerrar fichero  
    fic = new FileReader(fichero);
    char b[] = new char [20];
    while ((i = fic.read(b)) != -1){
        System.out.println(b);
    }
/*    File fichesal = new File("c:\\clase\\Pruebadam24610.txt");//declarar fichero
    FileWriter ficsal = new FileWriter(fichesal); //crear el flujo de salida  
    String cadena ="Esto es una prueba del FileWriter, de como se escribe.";
    System.out.println(cadena);
    char[] cad = cadena.toCharArray();
    for (i=0; i<cad.length; i++)
        ficsal.write(cad[i]);
    ficsal.append('*');
    ficsal.write(cad);
    ficsal.close();
      System.out.println("Fichero escrito");*/
    }
    }
    
