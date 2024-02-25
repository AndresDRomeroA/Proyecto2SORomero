/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.util.Random;

public class Personaje {
    private int id;
    private String idString;
    private String marca;
    private int prioridad;
    private int calidadFinal;
    private int contadorRondas;
    private int caballosFuerza;
    private Random r = new Random();

    public Personaje(int id, String marca, int prioridad, int calidadFinal) {
        this.id = id;
        this.marca = marca;
        this.prioridad = prioridad;
        this.calidadFinal = calidadFinal;
        this.contadorRondas = 0;
        this.caballosFuerza = r.nextInt(900 - 400) + 400;
    }

    // Getters y setters

    public int getId() {
        return id;
    }
    
    public int getCaballosFuerza(){
        return this.caballosFuerza;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public double getCalidadFinal() {
        return calidadFinal;
    }

    public void setCalidadFinal(int calidadFinal) {
        this.calidadFinal = calidadFinal;
    }

    public int getContadorRondas() {
        return contadorRondas;
    }

    public void setContadorRondas(int contadorRondas) {
        this.contadorRondas = contadorRondas;
    }
    @Override
    public String toString() {
        return "Vehiculo{" +
                "id=" + id +
                ", marca='" + marca + '\'' +
                ", prioridad=" + prioridad +
                ", calidadFinal=" + calidadFinal +
                ", contadorRondas=" + contadorRondas +
                '}';
    }
}
