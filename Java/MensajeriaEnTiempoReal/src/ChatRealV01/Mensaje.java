/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatRealV01;

import java.io.Serializable;



public class Mensaje implements Serializable {
    private String texto;
    private Cliente cliente;

    public Mensaje(Cliente cliente, String texto) {
        this.cliente = cliente;
        this.texto = texto;
    }

    public Mensaje() {
    }

    public String getTexto() {
        return texto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    @Override
    public String toString() {
        return cliente.getNombreUsuario() + ":" + texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}

