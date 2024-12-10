/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ChatRealV01;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

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
        System.out.println(mensaje.getCliente() + ":Enviado mensaje a todos los usuarios || " + LocalDateTime.now().format(formatoHora));
        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            try {
                if (oos.equals(entry.getKey())) {
                } else {
                    ObjectOutputStream clienteOOS = entry.getKey();
                    clienteOOS.writeObject(mensaje);
                    clienteOOS.flush();
                }
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
                    enviarMensajePrivado(contenido);
                } else {
                    try {
                        oos.writeObject(new Mensaje(new Cliente(""), "Mensaje privado mal formado."));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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
                    System.out.println(mensaje.getCliente() + ":Mensaje privado para " + destinatario + "-> " + mensajePrivado + " || " + LocalDateTime.now().format(formatoHora));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        try {
            oos.writeObject(new Mensaje(new Cliente(""), "No se encontro al usuario"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void listarCanales(List<Canal> canalesServidor) {
        System.out.println(mensaje.getCliente() + ":Listado los canales || " + LocalDateTime.now().format(formatoHora));
        for (Canal canal : canalesServidor) {
            try {
                oos.writeObject(canal);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void misCanales() {
        System.out.println(mensaje.getCliente() + ":Listado sus canales || " + LocalDateTime.now().format(formatoHora));
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
System.out.println(mensaje.getCliente() + ":se unio al canal " +canalPerjudicado+ " || " + LocalDateTime.now().format(formatoHora));
                mensaje.getCliente().getCanales().add(canal);
                
                return;
            }

        }
        try {
            oos.writeObject(new Mensaje(new Cliente(""), "No existe el canal."));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void salirsecanal(String canalPerjudicado) {
        for (Canal canal : canalesServidor) {
            if (canal.getNombre().equals(canalPerjudicado)) {

                mensaje.getCliente().getCanales().remove(canal);
                System.out.println(mensaje.getCliente() + ":abandono el canal " +canalPerjudicado+ " || " + LocalDateTime.now().format(formatoHora));
                return;
            }

        }
        
        try {
            oos.writeObject(new Mensaje(new Cliente(""), "No existe el canal."));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

        mensaje.setTexto(canalPerjudicado + " -> " + contenidoMensaje);
        for (Map.Entry<ObjectOutputStream, Mensaje> entry : clientes.entrySet()) {
            try {
                ObjectOutputStream clienteOOS = entry.getKey();
                Mensaje m = entry.getValue();
                for (Canal canale : m.getCliente().getCanales()) {
                    if (canale.getNombre().equals(canalPerjudicado) && !entry.getKey().equals(oos)) {
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
          System.out.println(mensaje.getCliente() + ":Ha listado los usuarios en línea || " + LocalDateTime.now().format(formatoHora));
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