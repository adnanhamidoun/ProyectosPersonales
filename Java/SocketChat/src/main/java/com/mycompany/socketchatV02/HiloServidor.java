/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.socketchatV02;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class HiloServidor extends Thread {

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Map<String, ObjectOutputStream> clientes = new ConcurrentHashMap<>();
    private Mensaje mensaje = null;
    private Cliente cliente = new Cliente("");
    ;
    boolean loginExitoso = false;
    private DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private ControladorBaseDeDatos cbd = new ControladorBaseDeDatos();
    private Apis apis = new Apis();

    public HiloServidor(Socket socket, Map<String, ObjectOutputStream> clientes) {
        this.socket = socket;
        this.clientes = clientes;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            do {
                mensaje = (Mensaje) ois.readObject();
                if (!mensaje.getCliente().getRegistro()) {
                    loginExitoso = procesarLogin(mensaje.getCliente());
                } else {
                    procesarRegistro(mensaje.getCliente());
                }
            } while (!loginExitoso);
            clientes.put(mensaje.getCliente().getNombreUsuario(), oos);
            cliente = mensaje.getCliente();
            int contador = cbd.countMensajesNuevos();
            if (contador > 0) {
                enviarMensaje("Mensajes nuevos -> " + contador + "  Escribe /entradas para verlos.", oos);
            }
            while ((mensaje = (Mensaje) ois.readObject()) != null) {
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
            cbd.setUsuarioOffline(cliente.getNombreUsuario());
            clientes.remove(oos);
            cerrarConexiones();
        }
    }


    private boolean procesarLogin(Cliente cliente) {
        boolean loginExitoso = cbd.loginUsuario(cliente.getNombreUsuario(), cliente.getPassword());
        if (loginExitoso) {
            cbd.setUsuarioOnline(cliente.getNombreUsuario());
        }
        String respuesta = loginExitoso ? "Bienvenido " + cliente.getNombreUsuario() : "Nickname o contraseña incorrectos, inténtelo de nuevo";
        enviarMensaje(respuesta, oos);
        return loginExitoso;
    }

    private void procesarRegistro(Cliente cliente) {
        boolean clienteExistente = cbd.verificarNickName(cliente.getNombreUsuario());
        String respuesta = clienteExistente ? "El nickname ya existe, inténtelo de nuevo" : "Usuario registrado con éxito, por favor inicie sesión";

        if (!clienteExistente) {
            cbd.registrarUsuario(cliente.getNombreUsuario(), cliente.getPassword());
        }
        enviarMensaje(respuesta, oos);
    }

    private void tratarMensaje() {
        String texto = mensaje.getTexto();


        if (texto == null || texto.isEmpty()) {
            return;
        }

        if (texto.startsWith("@")) {
            procesarMensajePrivado(texto);
            return;
        }

        if (texto.startsWith("/")) {
            procesarComando(texto);
            return;
        }

        enviarATodos();
    }


    private void procesarMensajePrivado(String texto) {
        int espacio = texto.indexOf(" ");
        if (espacio != -1) {
            String destinatario = texto.substring(1, espacio);
            String mensajePrivado = texto.substring(espacio + 1);
            String[] contenido = {destinatario, mensajePrivado};
            enviarMensajePrivado(contenido);
        } else {
            enviarMensaje("Mensaje privado mal formado.", oos);
        }
    }

    private void enviarMensajePrivado(String[] contenido) {
        String destinatario = contenido[0];
        String mensajePrivado = contenido[1];
        String resultado = cbd.checkUserIsConnected(destinatario);
        switch (resultado) {
            case "true":
                enviarMensaje("Mensaje privado de " + cliente.getNombreUsuario() + ": " + mensajePrivado, clientes.get(destinatario));
                break;
            case "false":
                cbd.guardarMensaje(cliente.getNombreUsuario(), mensajePrivado, destinatario);
                enviarMensaje("El usuario está desconectado. El mensaje se ha guardado en su bandeja de entrada para cuando se conecte.", oos);
                break;
            case null:
                enviarMensaje("Usuario desconocido.", oos);
                break;
            default:
        }

    }


    private void procesarComando(String texto) {
        int espacio = texto.indexOf(" ");
        int siguienteEspacio = texto.indexOf(" ", espacio + 1);
        String[] contenido;


        if (espacio != -1) {
            String comando = texto.substring(0, espacio);
            String argumento = texto.substring(espacio + 1);

            switch (comando) {
                case "/unirse":
                    unirseCanal(argumento);
                    break;
                case "/salir":
                    salirsecanal(argumento);
                    break;
                case "/mg":
                    contenido = procesarComando(espacio, siguienteEspacio, comando, texto);
                    if (contenido != null) {
                        comprobarCanal(contenido);
                    }
                    break;
                default:
                    enviarMensaje("Comando desconocido: " + comando, oos);
                    break;
            }
        } else {
            switch (texto) {
                case "/listarCanales":
                    listarCanales();
                    break;
                case "/misCanales":
                    misCanales();
                    break;
                case "/listarUsuarios":
                    listarUsuariosConectados();
                    break;
                case "/entradas":
                    visualizarMensajes();
                    break;
                case "/laLiga":
                    visualizarLaLiga();
                    break;
                case "/noticias":
                    visualizarNoticias();
                    break;
                case "/criptos":
                    visualizarCriptoMonedas();
                    break;
                case "/help":
                    visualizarAyuda();
                    break;
                default:
                    enviarMensaje("Comando desconocido: ", oos);
                    break;
            }
        }
    }

    private void visualizarAyuda() {
        String ayuda = "      GUIA DE COMANDOS DEL CHAT     \n" +
                "  @usuario mensaje  = Enviar un mensaje privado.\n" +
                "  /unirse canal     = Unirte a un canal.\n" +
                "  /salir canal      = Salir de un canal.\n" +
                "  /listarCanales    = Ver canales disponibles.\n" +
                "  /misCanales       = Ver canales a los que estás unido.\n" +
                "  /listarUsuarios   = Ver usuarios conectados.\n" +
                "  /mg canal mensaje = Enviar mensaje a un canal.\n" +
                "  /entradas         = Ver mensajes recibidos mientras estabas fuera.\n" +
                "  /laLiga           = Listar la clasificación de la Liga Española Actualmente (Solo disponible para canal Futbol).\n" +
                "  /noticias         = Listar las Noticias mas actuales (Solo disponible para canal Noticias).\n" +
                "  /criptos          = Listar top criptomonedas actuales (Solo disponible para canal Criptomonedas).\n" +
                "  (*) Desconectar   = Cerrar la conexión.\n" +
                "  ¡Disfruta la comunicacion!        ";
        enviarMensaje(ayuda, oos);
    }


    private void visualizarCriptoMonedas() {
        System.out.println(cbd.listarCanalesByNickName(cliente.getNombreUsuario()));
        if (cbd.listarCanalesByNickName(cliente.getNombreUsuario()).contains("CriptoMonedas")) {
            String output = apis.getCriptoMonedas();
            enviarMensaje(output, oos);
        } else {
            enviarMensaje("Este comando es exclusivo para usuarios unidos al canal de CriptoMonedas.", oos);
        }
    }

    private void visualizarNoticias() {
        if (cbd.listarCanalesByNickName(cliente.getNombreUsuario()).contains("Noticias")) {
            String output = apis.getNoticias();
            enviarMensaje(output, oos);
        } else {
            enviarMensaje("Este comando es exclusivo para usuarios unidos al canal de Noticias.", oos);
        }
    }

    private void visualizarLaLiga() {
        if (cbd.listarCanalesByNickName(cliente.getNombreUsuario()).contains("Futbol")) {
            String output = apis.getClasificacionSpain();
            enviarMensaje(output, oos);
        } else {
            enviarMensaje("Este comando es exclusivo para usuarios unidos al canal de Futbol.", oos);
        }
    }

    private void visualizarMensajes() {
        List<String> mensajes = cbd.getMensajesByNickName(cliente.getNombreUsuario());
        for (String mensaje : mensajes) {
            enviarMensaje(mensaje, oos);
        }
        cbd.setMensajesLeidos();
    }


    private void enviarATodos() {
        System.out.println(mensaje.getCliente() + ":Enviado mensaje a todos los usuarios || " + LocalDateTime.now().format(formatoHora));
        for (Map.Entry<String, ObjectOutputStream> entry : clientes.entrySet()) {
            try {
                if (!cliente.equals(entry.getKey())) {
                    enviarMensaje(cliente.getNombreUsuario() + ":" + mensaje.getTexto(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void listarCanales() {
        System.out.println(mensaje.getCliente() + ":Listado los canales || " + LocalDateTime.now().format(formatoHora));
        List<String> canales = cbd.listarCanales();
        enviarMensaje("Canales disponibles: " + String.valueOf(canales.size()) + "\n" + canales.toString(), oos);
    }

    private void misCanales() {
        System.out.println(mensaje.getCliente() + ":Listado sus canales || " + LocalDateTime.now().format(formatoHora));
        List<String> canales = cbd.listarCanalesByNickName(cliente.getNombreUsuario());
        enviarMensaje("Canales a los que perteneces: " + String.valueOf(canales.size()), oos);
        if (canales.size() > 0) {
            enviarMensaje(canales.toString(), oos);
        }
    }

    private String[] procesarComando(int espacio, int siguienteEspacio, String comando, String texto) {
        if (siguienteEspacio != -1) {
            String argumento = texto.substring(espacio + 1, siguienteEspacio);
            String mensaje = texto.substring(siguienteEspacio + 1);
            String[] contenido = {comando, argumento, mensaje};
            return contenido;
        } else {
            enviarMensaje("Comando /mg mal formado.", oos);
            return null;
        }
    }

    private void unirseCanal(String canalPerjudicado) {
        if (Character.isUpperCase(canalPerjudicado.charAt(0))) {
            if (cbd.insertarUsuarioEnCanal(cliente.getNombreUsuario(), canalPerjudicado)) {
                enviarMensaje("Te has unido al canal " + canalPerjudicado, oos);
            } else {
                enviarMensaje("El canal " + canalPerjudicado + " no existe", oos);
            }
        } else {
            enviarMensaje("El canal " + canalPerjudicado + " no existe", oos);
        }
    }

    private void salirsecanal(String canalPerjudicado) {
        if (cbd.deleteUserFromCanal(cliente.getNombreUsuario(), canalPerjudicado)) {
            enviarMensaje("Has abandonado el canal " + canalPerjudicado, oos);
        } else {
            enviarMensaje("El canal " + canalPerjudicado + " no existe", oos);
        }
    }


    private void comprobarCanal(String[] contenido) {
        List<String> canalesServidor = cbd.listarCanales();
        for (String canal : canalesServidor) {
            if (canal.equals(contenido[1])) {
                escribirCanal(contenido[1], contenido[2]);
            }
        }
    }


    private void escribirCanal(String canalPerjudicado, String contenidoMensaje) {
        for (Map.Entry<String, ObjectOutputStream> entry : clientes.entrySet()) {
            try {
                for (String canal : cbd.listarCanalesByNickName(entry.getKey())) {
                    if (canal.equals(canalPerjudicado) && !entry.getKey().equals(cliente.getNombreUsuario())) {
                        enviarMensaje(canalPerjudicado + "||" + cliente.getNombreUsuario() + "||" + contenidoMensaje, entry.getValue());
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    private void listarUsuariosConectados() {
        System.out.println(mensaje.getCliente() + ":Ha listado los usuarios en línea || " + LocalDateTime.now().format(formatoHora));
        List<String> usuariosOnline = cbd.listarUsuariosOnline();
        enviarMensaje("Usuarios conectados: " + String.valueOf(usuariosOnline.size()) + "\n" + usuariosOnline.toString(), oos);
    }


    private void enviarMensaje(String mensaje, ObjectOutputStream objectoutputstream) {
        try {
            objectoutputstream.writeObject(new Mensaje(new Cliente(""), mensaje));
            objectoutputstream.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
}
