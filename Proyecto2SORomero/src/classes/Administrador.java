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


public class Administrador extends Thread {
    int counter = 0;
    boolean running = true;
    
    public InteligenciaArtificial ia;
    public Semaphore mutex;
    
    public Cola lamboColaNivel1;
    public Cola lamboColaNivel2;
    public Cola lamboColaNivel3;
    public Cola bugattiColaNivel1;
    public Cola bugattiColaNivel2;
    public Cola bugattiColaNivel3;
    public Cola lamboRefuerzo;
    public Cola bugattiRefuerzo;
    
    // colas de la interfaz
    public ColaUi colaLamboUi1;
    public ColaUi colaLamboUi2;
    public ColaUi colaLamboUi3;
    public ColaUi colaLamboUiRef;
    
    public ColaUi colaBgUi1;
    public ColaUi colaBgUi2;
    public ColaUi colaBgUi3;
    public ColaUi colaBgUiRef;
    
    final Random porcentaje = new Random();
    
    public Administrador(){
        creacionColas();
        this.mutex = Main.mutex;
        for (int i = 0; i < 10; i++) {
            this.createVehiculosIniciales("lambo");
            this.createVehiculosIniciales("bugatti");

        }
        updateColasUi();
        System.out.println("cola numero 1 de UnShowMas: " + this.bugattiColaNivel1.print());
        System.out.println("cola numero 2 de UnShowMas: " + this.bugattiColaNivel2.print());
        System.out.println("cola numero 3 de UnShowMas: " + this.bugattiColaNivel3.print());
        System.out.println("cola numero 1 de Avatar: " + this.lamboColaNivel1.print());
        System.out.println("cola numero 2 de Avatar: " + this.lamboColaNivel2.print());
        System.out.println("cola numero 3 de Avatar: " + this.lamboColaNivel3.print());
        this.ia = Main.ia;

    }

    private void creacionColas() {
        this.bugattiColaNivel1 = new Cola(); 
        this.bugattiColaNivel2 = new Cola(); 
        this.bugattiColaNivel3 = new Cola(); 
        this.lamboColaNivel1 = new Cola();
        this.lamboColaNivel2 = new Cola();
        this.lamboColaNivel3 = new Cola();
        this.lamboRefuerzo = new Cola();
        this.bugattiRefuerzo = new Cola();
        
        this.colaLamboUi1 = GlobalUi.getMainPage().getColaLamboUi1();
        this.colaLamboUi2 = GlobalUi.getMainPage().getColaLamboUi2();
        this.colaLamboUi3 = GlobalUi.getMainPage().getColaLamboUi3();
        this.colaLamboUiRef = GlobalUi.getMainPage().getColaLamboUiRef();
        
        this.colaBgUi1 = GlobalUi.getMainPage().getColaBgUi1();
        this.colaBgUi2 = GlobalUi.getMainPage().getColaBgUi2();
        this.colaBgUi3 = GlobalUi.getMainPage().getColaBgUi3();
        this.colaBgUiRef = GlobalUi.getMainPage().getColaBgUiRef();
    }
    
    @Override
    public void run(){
        try {
            while(this.running){
                this.mutex.acquire();
                this.setCounter(this.counter + 1);
                
                this.desencolarRefuerzoVehiculo(lamboRefuerzo);
                this.desencolarRefuerzoVehiculo(bugattiRefuerzo);
                
                if(this.counter >= 2){
                    int result = porcentaje.nextInt(100);

                    this.addVehiculo("lambo", result);
                    this.addVehiculo("bugatti", result);
                    this.setCounter(0);
                }   

                this.sumarContadorCambiarPrioridad(lamboColaNivel2);
                this.sumarContadorCambiarPrioridad(lamboColaNivel3);
                this.sumarContadorCambiarPrioridad(bugattiColaNivel2);
                this.sumarContadorCambiarPrioridad(bugattiColaNivel3);
                
                updateColasUi();

                // obtener los carros
                Personaje lambo = this.getVehiculoColas(lamboColaNivel1, lamboColaNivel2, lamboColaNivel3);
                Personaje bugatti = this.getVehiculoColas(bugattiColaNivel1, bugattiColaNivel2, bugattiColaNivel3);
                
                // pasarle a la ia los carros
                ia.setCarroLambo(lambo);
                ia.setCarroBugatti(bugatti);
                
                //actualizar ids y carros en interfaz
                GlobalUi.getMainPage().getUiLamboId().setText(("Avatar-" + lambo.getId()));
                GlobalUi.getMainPage().getUiBugattiId().setText(("UnShowMas-" + bugatti.getId()));
                GlobalUi.getMainPage().setCarsImgsUi();
                // actualizar HP y calidad en interfaz
                GlobalUi.getMainPage().getCalidadLamboUi().setText(Integer.toString((int) lambo.getCalidadFinal()));
                GlobalUi.getMainPage().getCalidadBgUi().setText(Integer.toString((int) bugatti.getCalidadFinal()));
                GlobalUi.getMainPage().getLamboHP().setText(Integer.toString(lambo.getCaballosFuerza()));
                GlobalUi.getMainPage().getBugattiHP().setText(Integer.toString(bugatti.getCaballosFuerza()));
                
                // setear en 0 el contador de inanicion de cada carro
                if(lambo != null){
                    lambo.setContadorRondas(0);
                }
                if(bugatti != null){
                    bugatti.setContadorRondas(0);
                }
                
                updateColasUi();
                this.mutex.release();
                Thread.sleep(500);

            }
            
        } catch (InterruptedException e){
            Logger.getLogger(Administrador.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    private void addVehiculo(String marca, int result) { // todo agregar vehiculo a su cola de prioridad

        int prioridadFinal = calidadFinal();
        if (result <= 80) {
            if(marca.equals("lambo")){
                Personaje lambo = this.crearVehiculo(result, marca, prioridadFinal, prioridadFinal);
//                    System.out.println(lambo);
                this.ponerVehiculoEnSuCola(lambo);

            }else if(marca.equals("bugatti")){
                Personaje bugatti = this.crearVehiculo(result, marca, prioridadFinal, prioridadFinal);
                this.ponerVehiculoEnSuCola(bugatti);
            }
        }
    }
    
    private void createVehiculosIniciales(String marca){
        int prioridadFinal = calidadFinal();
        
        int result = porcentaje.nextInt(100);
        
        if(marca.equals("lambo")){
            
            Personaje lambo = this.crearVehiculo(result, marca, prioridadFinal, prioridadFinal);
            
            
            if(lambo.getPrioridad() == 1){
                this.lamboColaNivel1.encolar(lambo);
            }else if(lambo.getPrioridad() == 2){
                this.lamboColaNivel2.encolar(lambo);
            }else if(lambo.getPrioridad() == 3){
                this.lamboColaNivel3.encolar(lambo);
            }                
        }else if(marca.equals("bugatti")){ 
            Personaje bugatti = this.crearVehiculo(result, marca, prioridadFinal, prioridadFinal);
            if(bugatti.getPrioridad() == 1){
                this.bugattiColaNivel1.encolar(bugatti);
            }else if(bugatti.getPrioridad() == 2){
                this.bugattiColaNivel2.encolar(bugatti);
            }else if(bugatti.getPrioridad() == 3){
                this.bugattiColaNivel3.encolar(bugatti);
            }                
        }
    }
    
    private void desencolarRefuerzoVehiculo(Cola refuerzo) {
        if (refuerzo.isEmpty()) {
            return;
        }else {
            int result = porcentaje.nextInt(100);
            if (result <= 40) {
                Personaje vehiculo = refuerzo.dispatch();
                vehiculo.setPrioridad(1);
                this.regresarVehiculoCola1(vehiculo);
            } else {
                Personaje vehiculo = refuerzo.dispatch();
                refuerzo.encolar(vehiculo);
            }
        }       
    }
    
    public void setCounter(int counter) {
        this.counter = counter;
    }
    
    public Personaje crearVehiculo(int id, String marca, int prioridad, int calidadFinal){
        return new Personaje(id, marca, prioridad, calidadFinal);
        
    }
    
    public void ponerVehiculoEnSuCola(Personaje carro){
        if(carro.getMarca().equals("lambo")){
            if(carro.getPrioridad() == 1){
                this.lamboColaNivel1.encolar(carro);
            }else if(carro.getPrioridad() == 2){
                this.lamboColaNivel2.encolar(carro);
            }else{
                this.lamboColaNivel3.encolar(carro);
            }
        }else if(carro.getMarca().equals("bugatti")){
            if(carro.getPrioridad() == 1){
                this.bugattiColaNivel1.encolar(carro);
            }else if(carro.getPrioridad() == 2){
                this.bugattiColaNivel2.encolar(carro);
            }else{
                this.bugattiColaNivel3.encolar(carro);
            }
        }
    }
    
    public void regresarVehiculoCola1(Personaje marca){
        if(marca.getMarca().equals("lambo")){
            this.lamboColaNivel1.encolar(marca);
        }else if(marca.getMarca().equals("bugatti")){
            this.bugattiColaNivel1.encolar(marca);
        }
    }
    
    private void sumarContadorCambiarPrioridad(Cola cola){
        int longitud = cola.getSize();
        int i = 0;
        
        while(i<longitud){
            Personaje vehiculo = cola.dispatch();
            vehiculo.setContadorRondas(vehiculo.getContadorRondas() + 1);         
            
            if(vehiculo.getContadorRondas()>=8){
                if (vehiculo.getPrioridad()>1){
                    vehiculo.setPrioridad(vehiculo.getPrioridad() - 1);
                    if(vehiculo.getMarca().equals("lambo")){
                        if(vehiculo.getPrioridad() == 1){
                            this.lamboColaNivel1.encolar(vehiculo);
                        }else if(vehiculo.getPrioridad() == 2){
                            this.lamboColaNivel2.encolar(vehiculo);
                        }else if(vehiculo.getPrioridad() == 3){
                            this.lamboColaNivel3.encolar(vehiculo);
                        }
                    }else if(vehiculo.getMarca().equals("bugatti")){
                        if(vehiculo.getPrioridad() == 1){
                            this.bugattiColaNivel1.encolar(vehiculo);
                        }else if(vehiculo.getPrioridad() == 2){
                            this.bugattiColaNivel2.encolar(vehiculo);
                        }else if(vehiculo.getPrioridad() == 3){
                            this.bugattiColaNivel3.encolar(vehiculo);
                        }
                    }
                }else{
                    cola.encolar(vehiculo);
                }
                vehiculo.setContadorRondas(1);
            }else{
                cola.encolar(vehiculo);
            }
            i++;
        }
    }
    
    public void enviarCarrosColaRefuerzo(Personaje lambito, Personaje bugga){
        if(lambito != null ){
            this.lamboRefuerzo.encolar(lambito);
        }
        if(bugga != null){
            this.bugattiRefuerzo.encolar(bugga);
        }
    }
    
    private Personaje getVehiculoColas(Cola cola1, Cola cola2, Cola cola3) {
        
        if (!cola1.isEmpty()) {
            return cola1.dispatch();
        } else if (!cola2.isEmpty()) {
            return cola2.dispatch();
        } else if (!cola3.isEmpty()) {
            return cola3.dispatch();
        }
        return null;
    }
    
    public void updateColasUi(){
        this.colaLamboUi1.updateUiQueue(lamboColaNivel1);
        this.colaLamboUi2.updateUiQueue(lamboColaNivel2);
        this.colaLamboUi3.updateUiQueue(lamboColaNivel3);
        this.colaLamboUiRef.updateUiQueue(lamboRefuerzo);
        
        this.colaBgUi1.updateUiQueue(bugattiColaNivel1);
        this.colaBgUi2.updateUiQueue(bugattiColaNivel2);
        this.colaBgUi3.updateUiQueue(bugattiColaNivel3);
        this.colaBgUiRef.updateUiQueue(bugattiRefuerzo);
    }
    
    public void regresarCarrosAColas(Personaje lambo, Personaje bugatti) {
        if (lambo != null) {
            if(lambo.getPrioridad() == 1){
                this.lamboColaNivel1.encolar(lambo);
            }else if(lambo.getPrioridad() == 2){
                this.lamboColaNivel2.encolar(lambo);
            }else if(lambo.getPrioridad() == 3){
                this.lamboColaNivel3.encolar(lambo);
            }
        }
        if (bugatti != null) {
            if(bugatti.getPrioridad() == 1){
                this.bugattiColaNivel1.encolar(bugatti);
            }else if(bugatti.getPrioridad() == 2){
                this.bugattiColaNivel2.encolar(bugatti);
            }else if(bugatti.getPrioridad() == 3){
                this.bugattiColaNivel3.encolar(bugatti);
            }
        }
    }
    
    public int calidadFinal(){
        int calidadCarroceria = 0;
        int prioridadRandom = porcentaje.nextInt(100);
        if(prioridadRandom <= 60){
            calidadCarroceria = 1;
        }
        int calidadChasis = 0;
        prioridadRandom = porcentaje.nextInt(100);
        if (prioridadRandom <= 70){
            calidadChasis = 1;
        }
        
        int calidadMotor = 0;
        prioridadRandom = porcentaje.nextInt(100);
        if (prioridadRandom <= 50){
            calidadMotor = 1;
        }
        
        int calidadRueda = 0;
        prioridadRandom = porcentaje.nextInt(100);
        if (prioridadRandom <= 40){
            calidadRueda = 1;
        }
        int prioridadFinal = 0;
        int sumaCalidad = calidadCarroceria + calidadRueda + calidadMotor + calidadChasis;
        if(sumaCalidad >= 3){
            prioridadFinal = 1;
        }else if(sumaCalidad == 2){
            prioridadFinal = 2;
        }else{
            prioridadFinal = 3;
        }
        return prioridadFinal;
    }
    
}
