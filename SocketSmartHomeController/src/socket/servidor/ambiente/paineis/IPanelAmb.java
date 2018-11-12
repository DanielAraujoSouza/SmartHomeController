package socket.servidor.ambiente.paineis;

import java.net.Socket;

import socket.servidor.Ambiente;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public interface IPanelAmb {
	public void addEqp(Socket cliente, String eqpNome);
	public Ambiente getAmb();
	public Boolean enviaMsg(String tipo, String valor, Socket cliente);
	public void enviaMsgTodos(String tipo, String valor);
	public void iniciaValores(String eqpNome);
	public void mudaEstado(String valor);
	public void removerEqp(String eqpNome);
}
