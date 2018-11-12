package socket.simuladores;

import java.io.IOException;
import java.io.ObjectInputStream;

import socket.servidor.Mensagem;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class MonitorMsg implements Runnable{
	private ISimulador simulador;

	public MonitorMsg(ISimulador simulador) {
		super();
		this.simulador = simulador;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true){
			try {
				ObjectInputStream recebe = new ObjectInputStream(this.simulador.getCentral().getInputStream());
				Mensagem msg = (Mensagem)recebe.readObject();
				this.simulador.recebeMsg(msg);
				if(msg.getTipo().equals("desconectar")){
					break;
				}
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				break;				
			}			
		}
	}
	
}
