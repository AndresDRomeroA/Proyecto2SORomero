/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

public class Nodo {
    private Nodo next;
    private Personaje element;
    
    public Nodo(Personaje elemento){
        this.next = null;
        this.element = elemento;
    }

    /**
     * @return the next
     */
    public Nodo getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(Nodo next) {
        this.next = next;
    }

    /**
     * @return the element
     */
    public Personaje getElement() {
        return element;
    }

    /**
     * @param element the element to set
     */
    public void setElement(Personaje element) {
        this.element = element;
    }
    
}
