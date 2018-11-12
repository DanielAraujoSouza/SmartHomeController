package socket.simuladores;

import java.net.Socket;

import socket.servidor.Mensagem;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public interface ISimulador {
	public void recebeMsg(Mensagem msg);
	public void desconectarEqp();
	public Socket getCentral();
	public Boolean enviaMsg(String tipo, String valor, Boolean infoErro);
}
