/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.socketchatV02;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente implements Serializable {


    private String nombreUsuario;
    private String password;
    private boolean registro;

    public Cliente(String nombreUsuario, String password) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
    }



    public Cliente(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public boolean getRegistro() {
        return registro;
    }

    public void setRegistro(boolean registro) {
        this.registro = registro;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public String toString() {
        return nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Cliente c = null;
        Mensaje m = null;

        try (Socket socket = new Socket("localhost", 6666); ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            do {
                boolean esRegistro = (interfazLogin(sc));
                System.out.println("Introduce un nickname");
                String nickname = sc.nextLine();
                System.out.println("Introduce una contraseña");
                String password = sc.nextLine();
                c = new Cliente(nickname, password);
                c.setRegistro(esRegistro);
                oos.writeObject(new Mensaje(c, ""));
                oos.flush();
                m = (Mensaje) ois.readObject();
                System.out.println(m.getTexto());

            } while (!m.getTexto().startsWith("Bienvenido"));
            final Cliente cliente = c;
            Thread enviarHilo = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String texto;
                        System.out.println("Escribe un mensaje (/help para informacion de comandos)");
                        do {
                            texto = sc.nextLine();
                            oos.writeObject(new Mensaje(cliente, texto));
                            oos.flush();

                        } while (!texto.equals("*"));
                    } catch (Exception e) {
                        System.err.println("Error enviando mensaje.");
                        e.printStackTrace();
                    }
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

    private static boolean interfazLogin(Scanner sc) {
        String opcion = "";
        do {
            System.out.println("1-Registrarse");
            System.out.println("2-Loguearse");
            opcion = sc.nextLine();
        } while (!opcion.equalsIgnoreCase("1") && !opcion.equalsIgnoreCase("2"));
        return opcion.equalsIgnoreCase("1") ? true : false;

    }


}
