/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.socketchatV01;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloServidor extends Thread {

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Map< ObjectOutputStream, Mensaje> clientes;
    private List<Canal> canalesServidor;
    private Mensaje mensaje = null;
    boolean clienteValidado = false;

    public HiloServidor(Socket socket, Map< ObjectOutputStream, Mensaje> clientes, List<Canal> canalesServidor) {
        this.socket = socket;
        this.clientes = clientes;
        this.canalesServidor = canalesServidor;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            while (!clienteValidado) {               
                mensaje = (Mensaje) ois.readObject();
                if (comprobarClienteExistente() == false) {            
                    clienteValidado = true;
                    Cliente c = new Cliente("");
                    oos.writeObject(new Mensaje(c, "Validado"));
                } else {
                    Cliente c = new Cliente("");
                    oos.writeObject(new Mensaje(c, "Usuario ya existente"));
                }

            }

            synchronized (clientes) {
                clientes.put(oos, mensaje);
            }
            while ((mensaje = (Mensaje) ois.readObject()) != null) {

                synchronized (clientes) {
                    clientes.put(oos, mensaje);
                }

                if (mensaje.getTexto().equals("*")) {
                    System.out.println("Cierre moderado");
                    break;
                }

                tratarMensaje();
            }
        } catch (Exception e) {
            System.err.println("Cierre abrupto.");
            e.printStackTrace();
        } finally {

            synchronized (clientes) {
                clientes.remove(oos);
            }
            cerrarConexiones();
        }
    }

    private void enviarATodos() {

        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            try {
                ObjectOutputStream clienteOOS = entry.getKey();
                clienteOOS.writeObject(mensaje);
                clienteOOS.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void tratarMensaje() {

        String texto = mensaje.getTexto();
        if (mensaje.getTexto().equals("") || mensaje.getTexto().isEmpty()) {

        } else {

            if (texto.startsWith("@")) {
                String[] contenido = new String[3];
                int espacio = texto.indexOf(" ");
                if (espacio != -1) {
                    String destinatario = texto.substring(1, espacio);
                    String mensajePrivado = texto.substring(espacio + 1);
                    contenido[0] = destinatario;
                    contenido[1] = mensajePrivado;
                    contenido[2] = mensaje.getCliente().getNombreUsuario();
                    System.out.println("Mensaje privado para " + destinatario + ": " + mensajePrivado);
                    enviarMensajePrivado(contenido);
                } else {
                    System.err.println("Mensaje privado mal formado.");
                }

            } else if (texto.startsWith("/")) {
                String[] contenido = new String[3];
                int espacio = texto.indexOf(" ");
                int siguienteEspacio = texto.indexOf(" ", espacio + 1);
                if (espacio != -1) {
                    String opcion = texto.substring(0, espacio);
                    String canalPerjudicado = texto.substring(espacio + 1);
                    contenido[0] = opcion;
                    contenido[1] = canalPerjudicado;

                    if (contenido[0].equals("/unirse")) {
                        unirseCanal(canalPerjudicado);
                    } else if (contenido[0].equals("/salir")) {
                        salirsecanal(canalPerjudicado);
                    } else if (contenido[0].equals("/mg")) {
                        opcion = texto.substring(0, espacio);
                        canalPerjudicado = texto.substring(espacio + 1, siguienteEspacio);
                        String mensaje = texto.substring(siguienteEspacio + 1);
                        contenido[0] = opcion;
                        contenido[1] = canalPerjudicado;
                        contenido[2] = mensaje;
                        comprobarCanal(contenido);
                    }

                } else {
                    if (texto.equals("/listarCanales")) {
                        listarCanales(canalesServidor);
                    } else if (texto.equals("/misCanales")) {
                        misCanales();
                    } else if (texto.equals("/listarUsuarios")) {
                        listarUsuariosConectados();
                    } else {
                        System.err.println("Comando desconocido");
                    }
                }

            } else {

                enviarATodos();
            }
        }
    }

    private void enviarMensajePrivado(String[] contenido) {
        String destinatario = contenido[0];
        String mensajePrivado = contenido[1];
        String Emisor = contenido[2];

        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            ObjectOutputStream clienteOOS = entry.getKey();
            Mensaje mensaje2 = entry.getValue();

            if (mensaje2.getCliente().getNombreUsuario().equals(destinatario)) {
                try {
                    Cliente c = new Cliente("Mensaje Privado de " + Emisor);
                    Mensaje mensajePrivadoObj = new Mensaje(c, mensajePrivado);
                    clienteOOS.writeObject(mensajePrivadoObj);
                    clienteOOS.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        System.out.println("No se ha encontrado al usuario");

    }

    private void listarCanales(List<Canal> canalesServidor) {
        for (Canal canal : canalesServidor) {
            try {
                oos.writeObject(canal);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void misCanales() {
        List<Canal> c = mensaje.getCliente().getCanales();
        for (Canal canal : c) {
            try {
                oos.writeObject(canal);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void unirseCanal(String canalPerjudicado) {

        for (Canal canal : canalesServidor) {
            if (canal.getNombre().equals(canalPerjudicado)) {

                mensaje.getCliente().getCanales().add(canal);

                return;
            }

        }
        System.err.println("No existe el canal expecificado");
    }

    private void salirsecanal(String canalPerjudicado) {
        for (Canal canal : canalesServidor) {
            if (canal.getNombre().equals(canalPerjudicado)) {

                mensaje.getCliente().getCanales().remove(canal);

                return;
            }

        }
        System.err.println("No existe el canal expecificado");
    }

    private void comprobarCanal(String[] contenido) {
        System.out.println(contenido[0]);
        System.out.println(contenido[1]);
        System.out.println(contenido[2]);
        for (Canal canal : canalesServidor) {

            if (canal.getNombre().equals(contenido[1])) {
                escribirCanal(contenido[1], contenido[2]);
            }
        }
    }

    private void cerrarConexiones() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void escribirCanal(String canalPerjudicado, String contenidoMensaje) {

        mensaje.setTexto(canalPerjudicado + ":" + contenidoMensaje);
        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            try {
                ObjectOutputStream clienteOOS = entry.getKey();
                Mensaje m = entry.getValue();
                for (Canal canale : m.getCliente().getCanales()) {
                    if (canale.getNombre().equals(canalPerjudicado)) {
                        clienteOOS.writeObject(mensaje);
                        clienteOOS.flush();
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private boolean comprobarClienteExistente() {

        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            Mensaje m = entry.getValue();
            if (m.getCliente().getNombreUsuario().equals(mensaje.getCliente().getNombreUsuario())) {

                return true;
            }
        }

        return false;
    }

    private void listarUsuariosConectados() {
        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            try {

                Mensaje m = entry.getValue();
                if (m.getCliente() != mensaje.getCliente()) {
                    oos.writeObject(m.getCliente() + " esta en linea");
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
