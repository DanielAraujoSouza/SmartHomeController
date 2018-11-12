package socket.servidor;

import java.io.Serializable;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class Mensagem implements Serializable{

	private static final long serialVersionUID = 208934674899565566L;
	private String tipo;
	private String valor;
	public Mensagem(String tipo, String valor) {
		super();
		this.tipo = tipo;
		this.valor = valor;
	}
	public String getTipo() {
		return tipo;
	}
	public String getValor() {
		return valor;
	}
	public String toString() {
		// TODO Auto-generated method stub
		return "Tipo: " + this.tipo + " - Valor: " + this.valor;
	}
}
