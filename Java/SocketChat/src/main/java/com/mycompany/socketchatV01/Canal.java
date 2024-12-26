/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.socketchatV01;

import java.io.Serializable;

/**
 *
 * @author adnan
 */
public class Canal implements Serializable{
    private String nombre ;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Canal(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Canal{" + "nombre=" + nombre + '}';
    }
    
}
