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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import socket.servidor.Ambiente;
import socket.servidor.Mensagem;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class PanelArCond extends JPanel implements MouseListener,IPanelAmb{
	//Classe para guardar os dados de cada dispositivo conectado
	public class ArCond {
		private Socket cliente;
		private String nome;
		private String estado;
		private int temperatura;
		private String modo;
		
		public ArCond(Socket cliente, String nome, String estado, int temperatura, String modo) {
			super();
			this.cliente = cliente;
			this.nome = nome;
			this.estado = estado;
			this.temperatura = temperatura;
			this.modo = modo;
			
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

		public int getTemperatura() {
			return temperatura;
		}

		public void setTemperatura(int temperatura) {
			this.temperatura = temperatura;
		}

		public String getModo() {
			return modo;
		}

		public void setModo(String modo) {
			this.modo = modo;
		}

		public Socket getCliente() {
			return cliente;
		}

		public String getNome() {
			return nome;
		}
					
	}
	private static final long serialVersionUID = -1757430387839481275L;
	private final ImageIcon arCondOFF = new ImageIcon("src/socket/imagens/minIlumSplitOff.png");
	private final ImageIcon arCondON = new ImageIcon("src/socket/imagens/minIlumSplitOn.png");
	private final ImageIcon autoMode = new ImageIcon("src/socket/imagens/autoMode.png");
	private final ImageIcon refrigMode = new ImageIcon("src/socket/imagens/refrig.png");
	private final ImageIcon desumMode = new ImageIcon("src/socket/imagens/umid.png");
	private final ImageIcon ventMode = new ImageIcon("src/socket/imagens/ventilar.png");
	private final ImageIcon upMin = new ImageIcon("src/socket/imagens/upMin.png");
	private final ImageIcon downMin = new ImageIcon("src/socket/imagens/downMin.png");
	
	private Ambiente amb;
	private Vector <ArCond> lstEqp;
	private String estado;
	private int temperatura;
	private String modo;

	private JLabel lblIcon;
	private JLabel lblRemover;
	private JSeparator separator1;
	private JLabel lblDown;
	private JLabel lblUp;
	private JLabel lblTemp;
	private JSeparator separator2;
	private JLabel lblModo;	
	private JLabel lblTmpAmbiente;
	private JLabel lblTotal;

	public PanelArCond(Ambiente amb) {
		super();
		this.amb = amb;
		this.lstEqp = new Vector<ArCond>();
		this.estado = "off";
		this.temperatura = 25;
		this.modo = "auto";
		setLayout(new MigLayout("", "[30px][][33%][34%][33%][][30px]", "[][]"));
		
		lblIcon = new JLabel(arCondOFF);
		lblIcon.addMouseListener(this);
		add(lblIcon, "cell 0 0,alignx left,aligny top");
		
		lblRemover = new JLabel("Remover");
		lblRemover.addMouseListener(this);
		
		separator1 = new JSeparator();
		separator1.setVisible(false);
		separator1.setForeground(Color.WHITE);
		separator1.setOrientation(SwingConstants.VERTICAL);
		add(separator1, "cell 1 0,growy");
		
		lblDown = new JLabel(downMin);
		lblDown.setVisible(false);
		lblDown.addMouseListener(this);
		add(lblDown, "cell 2 0,alignx right");
		
		lblTemp = new JLabel("25 °C");
		lblTemp.setVisible(false);
		lblTemp.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblTemp.setForeground(new Color(77,129,204));
		add(lblTemp, "cell 3 0,alignx center");
		
		lblUp = new JLabel(upMin);
		lblUp.setVisible(false);
		lblUp.addMouseListener(this);
		add(lblUp, "cell 4 0,alignx left");
		
		separator2 = new JSeparator();
		separator2.setVisible(false);
		separator2.setForeground(Color.WHITE);
		separator2.setOrientation(SwingConstants.VERTICAL);
		add(separator2, "cell 5 0,growy");
		
		lblModo = new JLabel(autoMode);
		lblModo.setVisible(false);
		lblModo.addMouseListener(this);
		add(lblModo, "cell 6 0,alignx right");
		
		lblTmpAmbiente = new JLabel("");
		lblTmpAmbiente.setForeground(new Color(0, 139, 219));
		lblTmpAmbiente.setFont(new Font("Tahoma", Font.ITALIC, 9));
		add(lblTmpAmbiente, "cell 3 1");
		
		lblTotal = new JLabel("[1]");
		lblTotal.setForeground(new Color(0, 139, 219));
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblTotal, "cell 0 1,alignx center");
		
		lblRemover.setForeground(new Color(0, 139, 219));
		lblRemover.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblRemover, "cell 6 1,alignx right");
	}
	@Override
	public Ambiente getAmb() {
		return amb;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == lblIcon){
			if(this.estado.equals("off")){
				this.mudaEstado("on");				
			}
			else{
				this.mudaEstado("off");
			}
		}
		else if(e.getSource() == lblDown){
			int ntemp = this.temperatura-1;
			if(ntemp >= 10){
				this.mudaTemperatura(Integer.toString(ntemp));
			}			
		} 
		else if(e.getSource() == lblUp){
			int ntemp = this.temperatura+1;
			if(ntemp <= 30){
				this.mudaTemperatura(Integer.toString(ntemp));
			}			
		}
		else if(e.getSource() == lblModo){
			if(this.modo.equals("auto")){
				this.mudaModo("refrig");
			}
			else if(this.modo.equals("refrig")){
				this.mudaModo("desum");
			}
			else if(this.modo.equals("desum")){
				this.mudaModo("vent");
			}
			else if(this.modo.equals("vent")){
				this.mudaModo("auto");
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todos os Ar-Condicionados deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.enviaMsgTodos("desconectar", null);
				this.amb.removePanelEqp(this);
			}			
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("on")){
				this.atualizaEstado("on");
				this.enviaMsgTodos("estado", "on");
				this.lblIcon.setIcon(arCondON);
				this.separator1.setVisible(true);
				this.lblDown.setVisible(true);
				this.lblTemp.setVisible(true);
				this.lblUp.setVisible(true);
				this.separator2.setVisible(true);
				this.lblModo.setVisible(true);
				this.lblTmpAmbiente.setVisible(true);
			}
			else{
				this.atualizaEstado("off");
				this.enviaMsgTodos("estado", "off");
				this.lblIcon.setIcon(arCondOFF);
				this.separator1.setVisible(false);
				this.lblDown.setVisible(false);
				this.lblTemp.setVisible(false);
				this.lblUp.setVisible(false);
				this.separator2.setVisible(false);
				this.lblModo.setVisible(false);
				this.lblTmpAmbiente.setVisible(false);
			}
		}
	}
	public void atualizaEstado(String valor){
		this.estado = valor;
		synchronized (this.lstEqp) {		
			for (ArCond ac : this.lstEqp){
				ac.setEstado(valor);
			}
		}
	}
	public void mudaModo(String valor) {
		// TODO Auto-generated method stub
		if(!this.modo.equals(valor)){
			if (valor.equals("auto")){
				this.atualizaModo(valor);
				this.enviaMsgTodos("modo", valor);
				this.lblModo.setIcon(autoMode);
			} else if (valor.equals("refrig")){
				this.atualizaModo(valor);
				this.enviaMsgTodos("modo", valor);
				this.lblModo.setIcon(refrigMode);
			} else if (valor.equals("desum")){
				this.atualizaModo(valor);
				this.enviaMsgTodos("modo", valor);
				this.lblModo.setIcon(desumMode);
			} else if (valor.equals("vent")){
				this.atualizaModo(valor);
				this.enviaMsgTodos("modo", valor);
				this.lblModo.setIcon(ventMode);
			}
		}		
	}
	public void atualizaModo(String valor){
		this.modo = valor;
		synchronized (this.lstEqp) {		
			for (ArCond ac : this.lstEqp){
				ac.setModo(valor);
			}
		}
	}
	public void mudaTemperatura(String valor) {
		// TODO Auto-generated method stub
		int nvalor = Integer.parseInt(valor);
		if(this.temperatura != nvalor){
			this.atualizaTemperatura(nvalor);
			this.enviaMsgTodos("temp", valor);
		}
	}
	public void atualizaTemperatura(int valor){
		this.temperatura = valor;
		this.lblTemp.setText(Integer.toString(valor) + " °C");
		synchronized (this.lstEqp) {		
			for (ArCond ac : this.lstEqp){
				ac.setTemperatura(valor);
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
			this.lstEqp.add(new ArCond(cliente, eqpNome, "off", 25, "auto"));
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	@Override
	public void iniciaValores(String eqpNome){
		synchronized (this.lstEqp) {			
			for (ArCond ac : this.lstEqp){
				if(ac.getNome().equals(eqpNome)){
					if(!ac.getEstado().equals(this.estado)){
						ac.setEstado(this.estado);
						this.enviaMsg("estado", this.estado, ac.getCliente());
					}
					if(!ac.getModo().equals(this.modo)){
						ac.setModo(this.modo);
						this.enviaMsg("modo", this.modo, ac.getCliente());						
					}
					if(ac.getTemperatura() != this.temperatura){
						ac.setTemperatura(this.temperatura);
						this.enviaMsg("temp", Integer.toString(this.temperatura), ac.getCliente());						
					}
					break;
				}
			}
		}
	}
	@Override
	public void removerEqp(String eqpNome){
		synchronized (this.lstEqp) {			
			for (ArCond ac : this.lstEqp){
				if (ac.getNome().equals(eqpNome)){
					this.lstEqp.remove(ac);
					this.lblTotal.setText("["+this.lstEqp.size()+"]");
					if(this.lstEqp.size() == 0){
						amb.removePanelEqp(this);
					}
					break;
				}
			}
		}
	}
	//Informa a medicao periodica de temperatura
	public void mudaTempReal (String valor){
		this.lblTmpAmbiente.setText("Temp. Real: " + valor + " °C");
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
