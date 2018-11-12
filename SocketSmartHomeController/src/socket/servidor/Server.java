package socket.servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import socket.servidor.Mensagem;
import socket.servidor.ambiente.paineis.IPanelAmb;
import socket.servidor.ambiente.paineis.PanelArCond;
import socket.servidor.ambiente.paineis.PanelIlumComum;
import socket.servidor.ambiente.paineis.PanelIlumDecor;
import socket.servidor.ambiente.paineis.PanelIrrigacao;
import socket.servidor.ambiente.paineis.PanelJanela;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class Server  implements Runnable{
	private Central central;
	private Socket cliente;
	private String eqpNome;
	private IPanelAmb eqpPanel;
	
	public Server(Central central, Socket cliente) {
		super();
		this.central = central;
		this.cliente = cliente;
		this.eqpNome = null;
		this.eqpPanel = null;
	}
	@Override
	public void run() {
		// Thread que receberá as menssagens do dispositivo
		while(true){
			try {
				// Ler a mensagem enviada
				ObjectInputStream recebe = new ObjectInputStream(cliente.getInputStream());
				Mensagem msg = (Mensagem)recebe.readObject();
				
				// Solicitacao do Dispositivo para Central
				if (msg.getTipo().equals("registrar") && this.eqpNome == null){
					this.eqpPanel = this.central.getEqpAmb(msg.getValor());
					
					if(this.eqpPanel != null){
						this.eqpNome = msg.getValor();
						this.eqpPanel.addEqp(cliente, this.eqpNome);
						this.eqpPanel.enviaMsg("registrado", this.eqpPanel.getAmb().getNome(), this.cliente);
						this.eqpPanel.iniciaValores(this.eqpNome);
					}
					else{
						ObjectOutputStream envia = new ObjectOutputStream(cliente.getOutputStream());
						envia.flush();
						envia.writeObject(new Mensagem("ERRO", "Cancelado na Central"));
					}
				}
				// Altera o estado dos dispositivos: on/off
				else if (msg.getTipo().equals("estado") && this.eqpPanel != null){
					this.eqpPanel.mudaEstado(msg.getValor());					
				}
				// Altera a intensidade dos dispositivo de ilumininacao: 1~5
				else if (msg.getTipo().equals("intensidade") && this.eqpPanel != null){
					((PanelIlumComum)this.eqpPanel).mudaIntensidade(msg.getValor());
				}
				// Desconecta um dispositivo da central
				else if (msg.getTipo().equals("desconectar") && this.eqpPanel != null){
					this.eqpPanel.removerEqp(this.eqpNome);
				}
				// Altera a tonalidade dos dispositivo decorativos: 1~5
				else if (msg.getTipo().equals("tonalidade") && this.eqpPanel != null){
					((PanelIlumDecor)this.eqpPanel).mudaTonalidade(msg.getValor());
				}
				// Altera o modo dos dispositivo climatizacao: "auto", "refrig", "desum", "vent"
				else if (msg.getTipo().equals("modo") && this.eqpPanel != null){
					((PanelArCond)this.eqpPanel).mudaModo(msg.getValor());
				}
				// Altera a temperatura do arCondicionado: 10~30
				else if (msg.getTipo().equals("temp") && this.eqpPanel != null){
					((PanelArCond)this.eqpPanel).mudaTemperatura(msg.getValor());
				}
				// Atualiza temperatura medida a cada 10s
				else if (msg.getTipo().equals("tempReal") && this.eqpPanel != null){
					((PanelArCond)this.eqpPanel).mudaTempReal(msg.getValor());
				}
				// Abre ou Fecha a cortina
				else if (msg.getTipo().equals("estCortina") && this.eqpPanel != null){
					((PanelJanela)this.eqpPanel).mudaEstCortina(msg.getValor());
				}
				// Altera modo de irrigacao
				else if (msg.getTipo().equals("modoIr") && this.eqpPanel != null){
					((PanelIrrigacao)this.eqpPanel).mudaModo(msg.getValor());
				}
				// Abre ou Fecha a cortina
				else if (msg.getTipo().equals("umidReal") && this.eqpPanel != null){
					((PanelIrrigacao)this.eqpPanel).mudaUmidade(msg.getValor(), this.eqpNome);
				}
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				break;				
			}			
		}		
	}
}
