/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import interfaz.ColaUi;
import interfaz.GlobalUi;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InteligenciaArtificial extends Thread {

    private Administrador administrador;
    Personaje carroLambo;
    Personaje carroBg;
    
    int simulationTime;
    Random random = new Random();
    public Semaphore mutex;
    
    public String[] winners = new String[500];

    private int winnersPointer = 0;
    public int lamboWins = 0;
    public int bgWins = 0;
    

    public InteligenciaArtificial() {
        this.mutex = Main.mutex;
        // 10 segundos por defecto
        this.simulationTime = 10 * 1000;
    }

    @Override
    public void run() {
        try {
            while (true) {   
                
                long auxTime = simulationTime;
                this.mutex.acquire();
                
                if(this.carroLambo == null || this.carroBg == null){
                    Main.sistemaOperativo.regresarCarrosAColas(carroLambo, carroBg);
                    Thread.sleep(auxTime);
                    
                } else{
                    // Decidiendo
                    System.out.println("estoy decidiendo");
                    GlobalUi.getMainPage().getStatusLabel().setText("Decidiendo");
                    //tomar un numero random para determinar ganador. 0=lambo, 1=bugatti
                    Personaje winner = this.decideWinner();

                    Thread.sleep((long) (auxTime * 0.3));
                    
                    // entero para determinar resultado de la simulacion
                    int decision = random.nextInt(100);
                    
                    if(decision <= 40){ //hay ganador
                        System.out.println("Gano alguien");
                        GlobalUi.getMainPage().getStatusLabel().setText("Hay un ganador");
                        String car = winner.getMarca();
                        if (car.equals("lambo")) {
                             winners[winnersPointer] = "Avatar-" + Integer.toString(winner.getId());
                        } else if (car.equals("bugatti")) {
                            winners[winnersPointer] = "UnShowMas-" + Integer.toString(winner.getId());
                        }
                        if(winnersPointer < winners.length){
                            winnersPointer++;
                        }           
                        
                        GlobalUi.getMainPage().getWinnersLabel().setText(printWinners());
                        if(winner.getMarca().equals("lambo")){ // es lambo
                            this.lamboWins++;
                            //sumar contador de carreras ganadas
                            GlobalUi.getMainPage().getLamboWinsLabel().setText(Integer.toString(lamboWins));
                            //decir que gano
                            GlobalUi.getMainPage().getLamboWinnerLabel().setText("Ganador!");
                            
                        }else if(winner.getMarca().equals("bugatti")){ //es bugatti                            
                            this.bgWins++;
                            GlobalUi.getMainPage().getBugattiWinsLabel().setText(Integer.toString(bgWins));
                            GlobalUi.getMainPage().getBugattiWinnerLabel().setText("Ganador!");
                        }
                        
                        System.out.println("Avatar ganadas: " + lamboWins);
                        System.out.println("UnShowMas ganadas: " + bgWins);
                        Thread.sleep((long) (auxTime * 0.5));
                        
                    }else if(decision <= 67){ //hay empate
                        System.out.println("hubo empate");
                        GlobalUi.getMainPage().getStatusLabel().setText("Hubo Empate");
                        Thread.sleep((long) (auxTime * 0.5));
                        Main.sistemaOperativo.regresarVehiculoCola1(carroLambo);
                        Main.sistemaOperativo.regresarVehiculoCola1(carroBg);                        
                    }else{ //van a refuerzo
                        System.out.println("nos vamos a refuerzo");
                        GlobalUi.getMainPage().getStatusLabel().setText("Vamos a refuerzo");
                        Thread.sleep((long) (auxTime * 0.5));
                        //enviar a la cola de refuerzo
                        Main.sistemaOperativo.enviarCarrosColaRefuerzo(this.carroLambo, this.carroBg);
                    }                    
                    System.out.println("Esperando");
                    GlobalUi.getMainPage().getStatusLabel().setText("Esperando");
                }
                this.administrador.updateColasUi();
                System.out.println(this.administrador.bugattiColaNivel1.print());
                Thread.sleep((long) (auxTime * 0.2));
                
                GlobalUi.getMainPage().getLamboWinnerLabel().setText("");
                GlobalUi.getMainPage().getBugattiWinnerLabel().setText("");
                
                this.mutex.release();
                Thread.sleep(500);                
            }                                   
        } catch (InterruptedException ex) {
            Logger.getLogger(InteligenciaArtificial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    private String printWinners(){
        String text = "";
        for(int i = 0; i < winnersPointer; i++){
            String value = winners[i];
            text += value + ", ";
        }
        return text;
    }
    
    public void setCarroLambo(Personaje nuevoLambo){
        this.carroLambo = nuevoLambo;
    }
    
    public void setCarroBugatti(Personaje nuevoBg){
        this.carroBg = nuevoBg;
    }
    
    public void setSimulationTime(int timeSec){
        this.simulationTime = timeSec * 1000;
    }
    
    public void setAdministrador(){
        this.administrador = Main.sistemaOperativo;
    }
    
    public Personaje decideWinner(){
        
        Personaje winner = null;
        
        // si la diferencia de caballos de fuerza es <= 50, entonces gana el de mejor calidad, si es la misma, gana el que tenga mas caballos
        if(Math.abs(carroLambo.getCaballosFuerza() - carroBg.getCaballosFuerza()) <= 50){
            
            if (this.carroBg.getCalidadFinal() < this.carroLambo.getCalidadFinal()){
                winner = this.carroBg; 
            }else if(this.carroLambo.getCalidadFinal() < this.carroBg.getCalidadFinal()){
                winner = this.carroLambo;
            }else if(this.carroBg.getCalidadFinal() == this.carroLambo.getCalidadFinal()){ //misma calidad
                winner = this.winnerByHP();
            }
            
        }else { // la diferencia es mayor a 50 caballos, gana el que tenga mas caballos
            winner = this.winnerByHP();
        }
        
        return winner;   
    }
    
    public Personaje winnerByHP(){
        Personaje winner = null;
        
        if(carroLambo.getCaballosFuerza() > carroBg.getCaballosFuerza()){
            //lambo tiene mas HP
            winner = this.carroLambo;
        }else if (carroLambo.getCaballosFuerza() < carroBg.getCaballosFuerza()){
            winner = this.carroBg;
        }else{ // tienen los mismos caballos, se hace al azar
            int num = random.nextInt(2);
            winner = (num == 0) ? this.carroLambo : this.carroBg;
        }
        
        return winner;
    }
    
}   

