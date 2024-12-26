/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.socketchatV01;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {

    private static final int PUERTO = 6666;
    private static Map< ObjectOutputStream, Mensaje> clientes = new HashMap<>();
    private static List<Canal> canalesServidor = new ArrayList<>();

    public static void main(String[] args) {
        rellenarCanalesServidor();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());
                HiloServidor hiloCliente = new HiloServidor(socketCliente, clientes, canalesServidor);
                hiloCliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void rellenarCanalesServidor() {
        Canal a = new Canal("Deporte");
        Canal b = new Canal("Noticias");
        Canal c = new Canal("Gaming");
        canalesServidor.add(a);
        canalesServidor.add(b);
        canalesServidor.add(c);

    }
}
