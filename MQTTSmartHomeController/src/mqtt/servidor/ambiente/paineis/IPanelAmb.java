package mqtt.servidor.ambiente.paineis;

import mqtt.servidor.Ambiente;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public interface IPanelAmb {
	// Adiciona um novo equipamento
	public void addEqp(String eqpNome);
	public Ambiente getAmb();
	// Publica um mensagem no Broker
	public void publicar(String topico, String mensagem);
	// Sincroniza o estado do equipamento com os da central
	public void iniciaValores(String eqpNome);
	// Liga/Desliga
	public void mudaEstado(String valor);
	// Remove um equipamento que for desconectado
	public boolean descEqp(String eqpNome);
}
