/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.socketchatV01;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cliente implements Serializable {

    private String nombreUsuario;
    private List<Canal> canales = new ArrayList<>();

    public Cliente(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public String toString() {
        return nombreUsuario;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Cliente c = null;
        Mensaje m = null;

        try (Socket socket = new Socket("localhost", 6666); ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            do {

                System.out.print("Introduce tu nombre de usuario: ");
                String usuario = sc.nextLine();
                c = new Cliente(usuario);
                oos.writeObject(new Mensaje(c, ""));
                oos.flush();
                m = (Mensaje) ois.readObject();
                System.out.println(m.getTexto());

            } while (!m.getTexto().equals("Validado"));
            final Cliente cliente = c;
            Thread enviarHilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String texto;
                        System.out.println("Escribe un mensaje (/help para informacion de comandos): ");
                        do {
                            texto = sc.nextLine();
                            if (texto.equals("/help")) {
                                informacion();
                            } else {
                                oos.writeObject(new Mensaje(cliente, texto));
                                oos.flush();
                            }

                        } while (!texto.equals("*"));
                    } catch (Exception e) {
                        System.err.println("Error enviando mensaje.");
                        e.printStackTrace();
                    }
                }

                private void informacion() {

                    System.out.println("      GUIA DE COMANDOS DEL CHAT     ");

                    System.out.println();

                    System.out.println("Comandos Privados:");
                    System.out.println("  @usuario mensaje  = Enviar un mensaje privado.");
                    System.out.println("  (*) Desconectar   = Cerrar la conexión.");
                    System.out.println();
                    System.out.println("Comandos de Canal:");
                    System.out.println("  /unirse canal     = Unirte a un canal.");
                    System.out.println("  /salir canal      = Salir de un canal.");
                    System.out.println("  /listarCanales    = Ver canales disponibles.");
                    System.out.println("  /misCanales       = Ver canales a los que estás unido.");
                    System.out.println("  /listarUsuarios   = Ver usuarios conectados.");
                    System.out.println("  /mg canal mensaje = Enviar mensaje a un canal.");
                    System.out.println();
                    System.out.println("  ¡Disfruta la comunicacion!        ");

                }

            });

            Thread recibirHilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object o;
                        while ((o = ois.readObject()) != null) {
                            System.out.println(o);
                        }
                    } catch (Exception e) {
                        System.err.println("Desconectado del servidor.");
                        e.printStackTrace();
                    }
                }
            });

            enviarHilo.start();
            recibirHilo.start();
            //Obligatorio en este caso
            enviarHilo.join();

        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    public List<Canal> getCanales() {
        return canales;
    }

    public void setCanales(List<Canal> canales) {
        this.canales = canales;
    }

}
