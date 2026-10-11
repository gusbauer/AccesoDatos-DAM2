/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author antonio
 */
package ficheros;
import java.io.*;
public class Escribir100 {
  public static void main(String[] args) {
  try{      
      BufferedWriter fichero = new BufferedWriter
    		  (new FileWriter("c:\\clase\\lineas.txt"));
      for (int i=1; i<21; i++){
	    fichero.write("Fila numero: "+i); //escribe una lÃ­nea
	    fichero.newLine(); //escribe un salto de lÃ­nea
      }
      fichero.close(); 
      }
	catch (FileNotFoundException fn ){                      
               System.out.println("No se encuentra el fichero");}
	catch (IOException io) {
               System.out.println("Error de E/S ");}
  }
}
