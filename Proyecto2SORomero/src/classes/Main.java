/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package classes;

import interfaz.GlobalUi;
import java.util.concurrent.Semaphore;

public class Main {
    public static Semaphore mutex = new Semaphore(1);
    public static InteligenciaArtificial ia = new InteligenciaArtificial();
    public static Administrador sistemaOperativo = new Administrador();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ia.setAdministrador();
        
        sistemaOperativo.start();
        ia.start();
        
        GlobalUi.openMainPage();
    }
    
}
