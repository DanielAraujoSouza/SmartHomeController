package socket.servidor.ambiente.paineis;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import socket.servidor.Ambiente;
import socket.servidor.Mensagem;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class PanelJanela extends JPanel implements MouseListener, IPanelAmb{
	//Classe para guardar os dados de cada dispositivo conectado
	public class Janela {

		private Socket cliente;
		private String nome;
		private String estado;
		private String estadoCortina;

		public Janela(Socket cliente, String nome, String estado, String estadoCortina) {
			super();
			this.cliente = cliente;
			this.nome = nome;
			this.estado = estado;
			this.estadoCortina = estadoCortina;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

		public String getEstadoCortina() {
			return estadoCortina;
		}

		public void setEstadoCortina(String estadoCortina) {
			this.estadoCortina = estadoCortina;
		}

		public Socket getCliente() {
			return cliente;
		}

		public String getNome() {
			return nome;
		}
	}

	private static final long serialVersionUID = -3284069266299880793L;
	private final ImageIcon janelaIcon = new ImageIcon("src/socket/imagens/janelaMini.png");
	private final ImageIcon cortinaAbrir = new ImageIcon("src/socket/imagens/cortinaAbrirMini.png");
	private final ImageIcon cortinaFechar = new ImageIcon("src/socket/imagens/cortinaFecharMini.png");
	private final ImageIcon trancarOn = new ImageIcon("src/socket/imagens/trancarMini.png");
	private final ImageIcon trancarOff = new ImageIcon("src/socket/imagens/trancarMiniOff.png");
	private final ImageIcon upMin = new ImageIcon("src/socket/imagens/upMin.png");
	private final ImageIcon downMin = new ImageIcon("src/socket/imagens/downMin.png");

	private Ambiente amb;
	private Vector <Janela> lstEqp;
	private String estado;
	private String estadoCortina;

	private JLabel lblIcon;
	private JLabel lblRemover;
	private JLabel lblTranca;
	private JLabel lblEstado;
	private JLabel lblCortina;
	private JLabel lblTotal;

	public PanelJanela(Ambiente amb) {
		super();
		this.amb = amb;
		this.lstEqp = new Vector<Janela>();
		this.estado = "trancada";
		this.estadoCortina = "fechada";

		setLayout(new MigLayout("", "[30px][80%][50px:n][50px:n][50px:n]", "[][]"));

		lblIcon = new JLabel(janelaIcon);
		add(lblIcon, "cell 0 0,alignx left,aligny top");

		lblRemover = new JLabel("Remover");
		lblRemover.addMouseListener(this);

		lblEstado = new JLabel(upMin);
		lblEstado.addMouseListener(this);
		add(lblEstado, "cell 2 0,alignx center");

		lblTranca = new JLabel(trancarOn);
		lblTranca.addMouseListener(this);
		add(lblTranca, "cell 3 0,alignx center");

		lblCortina = new JLabel(cortinaAbrir);
		lblCortina.addMouseListener(this);
		add(lblCortina, "cell 4 0,alignx center");

		lblTotal = new JLabel("[1]");
		lblTotal.setForeground(new Color(0, 139, 219));
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblTotal, "cell 0 1,alignx center");

		lblRemover.setForeground(new Color(0, 139, 219));
		lblRemover.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblRemover, "cell 4 1,alignx right");
	}
	@Override
	public Ambiente getAmb() {
		return amb;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == lblEstado){
			if(this.estado.equals("fechada") || this.estado.equals("trancada")){
				this.mudaEstado("aberta");						
			}
			else {
				this.mudaEstado("fechada");					
			}
		}
		else if (e.getSource() == lblTranca){
			if(this.estado.equals("aberta") || this.estado.equals("fechada")){
				this.mudaEstado("trancada");						
			}
			else {
				this.mudaEstado("fechada");						
			}
		}
		else if (e.getSource() == lblCortina){
			if(this.estadoCortina.equals("aberta")){
				this.mudaEstCortina("fechada");						
			}
			else {
				//this.estadoCortina = "aberta";
				this.mudaEstCortina("aberta");						
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todas as janelas deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.enviaMsgTodos("desconectar", null);
				this.amb.removePanelEqp(this);
			}				
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(valor.equals("aberta")){		
			this.lblEstado.setIcon(downMin);
			this.lblTranca.setIcon(trancarOff);
			this.lblCortina.setIcon(cortinaFechar);

			this.atualizaEstado(valor);
			this.atualizaEstadoCortina(valor);

			this.enviaMsgTodos("estado", valor);
		}
		else if(valor.equals("fechada")){		
			this.lblEstado.setIcon(upMin);
			this.lblTranca.setIcon(trancarOff);

			this.atualizaEstado(valor);
			this.enviaMsgTodos("estado", valor);
		}
		else if(valor.equals("trancada")){
			this.lblEstado.setIcon(upMin);
			this.lblTranca.setIcon(trancarOn);

			this.atualizaEstado(valor);
			this.enviaMsgTodos("estado", valor);
		}
	}
	public void atualizaEstado (String valor){
		this.estado = valor;
		synchronized (this.lstEqp) {			
			for (Janela j : this.lstEqp){
				j.setEstado(valor);
			}
		}
	}
	public void mudaEstCortina (String valor){
		if(valor.equals("fechada")){
			this.lblCortina.setIcon(cortinaAbrir);
			this.atualizaEstadoCortina(valor);
			this.enviaMsgTodos("estCortina", valor);
			if(this.estado.equals("aberta")) {						
				this.lblEstado.setIcon(upMin);						
				this.atualizaEstado(valor);
			}
		}
		else {
			this.lblCortina.setIcon(cortinaFechar);
			this.atualizaEstadoCortina(valor);
			this.enviaMsgTodos("estCortina", valor);
		}
	}
	public void atualizaEstadoCortina (String valor){
		this.estadoCortina = valor;
		synchronized (this.lstEqp) {			
			for (Janela j : this.lstEqp){
				j.setEstadoCortina(valor);
			}
		}
	}
	//Envia mensagens a todos os equipamentos vinculados a este painel
	@Override
	public void enviaMsgTodos(String tipo, String valor){
		synchronized (this.lstEqp) {			
			for (int i = 0; i < lstEqp.size(); i++){
				if(!this.enviaMsg(tipo, valor, lstEqp.get(i).getCliente())){
					this.lstEqp.remove(i);
					i--;
				}
			}
			if(this.lstEqp.size() == 0){
				amb.removePanelEqp(this);
				JOptionPane.showMessageDialog(this,  "Erro de Comunicação com Equipamento!", "Erro", JOptionPane.ERROR_MESSAGE);
			}
		}		
	}
	//Envia mensagens a um equipamento
	@Override
	public Boolean enviaMsg(String tipo, String valor, Socket cliente){
		try{
			ObjectOutputStream envia = new ObjectOutputStream(cliente.getOutputStream());
			envia.flush();
			envia.writeObject(new Mensagem(tipo, valor));			
			return true;			
		}catch(IOException e1){
			return false;
		}
	}
	@Override
	public void addEqp(Socket cliente, String eqpNome){
		synchronized (this.lstEqp) {
			this.lstEqp.add(new Janela(cliente, eqpNome, "trancada", "fechada"));
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	public void iniciaValores(String eqpNome){
		synchronized (this.lstEqp) {			
			for (Janela j : this.lstEqp){
				if(j.getNome().equals(eqpNome)){
					if(!j.getEstado().equals(this.estado)){
						j.setEstado(this.estado);
						this.enviaMsg("estado", this.estado, j.getCliente());
					}
					if(j.getEstadoCortina() != this.estadoCortina){
						j.setEstadoCortina(this.estadoCortina);
						this.enviaMsg("estCortina", this.estadoCortina, j.getCliente());						
					}
					break;
				}
			}
		}
	}
	@Override
	public void removerEqp(String eqpNome){
		synchronized (this.lstEqp) {			
			for (Janela j : this.lstEqp){
				if (j.getNome().equals(eqpNome)){
					this.lstEqp.remove(j);
					this.lblTotal.setText("["+this.lstEqp.size()+"]");
					if(this.lstEqp.size() == 0){
						amb.removePanelEqp(this);
					}
					break;
				}
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
