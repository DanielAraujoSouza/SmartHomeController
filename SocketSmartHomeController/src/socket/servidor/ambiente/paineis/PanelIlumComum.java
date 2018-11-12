package socket.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import socket.servidor.Ambiente;
import socket.servidor.Mensagem;
import java.awt.Font;
import java.awt.Color;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class PanelIlumComum extends JPanel implements ChangeListener,MouseListener, IPanelAmb{
	//Classe para guardar os dados de cada dispositivo conectado
	public class IlumComum {
		private Socket cliente;
		private String nome;
		private String estado;
		private int intensidade;

		public IlumComum(Socket cliente, String nome, String estado, int intensidade) {
			super();
			this.cliente = cliente;
			this.nome = nome;
			this.estado = estado;
			this.intensidade = intensidade;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

		public int getIntensidade() {
			return intensidade;
		}

		public void setIntensidade(int intensidade) {
			this.intensidade = intensidade;
		}

		public Socket getCliente() {
			return cliente;
		}

	}

	private static final long serialVersionUID = -4332586714695973563L;
	private final ImageIcon ilumComumOff = new ImageIcon("src/socket/imagens/minIlumComumOff.png");
	private final ImageIcon ilumComumOn = new ImageIcon("src/socket/imagens/minIlumComumOn.png");

	private Ambiente amb;
	private Vector <IlumComum> lstEqp;
	private String estado;
	private int intensidade;

	private JLabel lblIcon;
	private JSlider sldIntens;
	private JLabel lblRemover;
	private JLabel lblTotal;

	public PanelIlumComum(Ambiente amb) {
		this.amb = amb;
		this.lstEqp = new Vector<IlumComum>();
		this.estado = "off";
		this.intensidade = 5;
		setLayout(new MigLayout("", "[30px][][100%]", "[][]"));

		lblIcon = new JLabel(ilumComumOff);
		lblIcon.addMouseListener(this);
		add(lblIcon, "cell 0 0,alignx left,aligny top");

		sldIntens = new JSlider(JSlider.HORIZONTAL,1,5,1);
		sldIntens.setValue(5);
		sldIntens.setEnabled(false);
		sldIntens.addChangeListener(this);
		sldIntens.setMajorTickSpacing(1);
		sldIntens.setPaintTicks(true);
		Hashtable<Integer, JLabel> lbltable = new Hashtable<Integer, JLabel>();
		lbltable.put(new Integer(1), new JLabel("20%"));
		lbltable.put(new Integer(2), new JLabel("40%"));
		lbltable.put(new Integer(3), new JLabel("60%"));
		lbltable.put(new Integer(4), new JLabel("80%"));
		lbltable.put(new Integer(5), new JLabel("100%"));
		sldIntens.setLabelTable(lbltable);
		sldIntens.setPaintLabels(true);
		sldIntens.setSnapToTicks(true);
		add(sldIntens, "cell 2 0,growx,aligny top");

		lblTotal = new JLabel("[1]");
		lblTotal.setForeground(new Color(0, 139, 219));
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblTotal, "cell 0 1,alignx center");

		lblRemover = new JLabel("Remover");
		lblRemover.addMouseListener(this);		
		lblRemover.setForeground(new Color(0, 139, 219));
		lblRemover.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblRemover, "cell 2 1,alignx right");
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
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todas as iluminações comuns deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.enviaMsgTodos("desconectar", null);
				this.amb.removePanelEqp(this);
			}			
		}
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sldIntens){
			if(this.sldIntens.getValue() != this.intensidade){
				this.atualizaIntensidade(sldIntens.getValue());
				enviaMsgTodos("intensidade", Integer.toString(sldIntens.getValue()));				
			}	
		}		
	}
	public void atualizaIntensidade(int valor){
		this.intensidade = valor;
		synchronized (this.lstEqp) {			
			for (IlumComum ic : this.lstEqp){
				ic.setIntensidade(valor);
			}
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("off")){
				this.atualizaEstado("off");
				this.enviaMsgTodos("estado", "off");			
				this.lblIcon.setIcon(ilumComumOff);
				this.sldIntens.setEnabled(false);
			}
			else{
				this.atualizaEstado("on");
				this.enviaMsgTodos("estado", "on");
				this.lblIcon.setIcon(ilumComumOn);
				this.sldIntens.setEnabled(true);
			}
		}		
	}
	public void atualizaEstado(String valor){
		this.estado = valor;
		synchronized (this.lstEqp) {		
			for (IlumComum ic : this.lstEqp){
				ic.setEstado(valor);
			}
		}
	}
	public void mudaIntensidade(String valor){
		if(this.intensidade != Integer.parseInt(valor)){			
			this.atualizaIntensidade(sldIntens.getValue());
			enviaMsgTodos("intensidade", valor);			
			this.sldIntens.setValue(Integer.parseInt(valor));			
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
			this.lstEqp.add(new IlumComum(cliente, eqpNome, "off", 5));
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	public void iniciaValores(String eqpNome){
		synchronized (this.lstEqp) {			
			for (IlumComum ic : this.lstEqp){
				if(ic.getNome().equals(eqpNome)){
					if(!ic.getEstado().equals(this.estado)){
						ic.setEstado(this.estado);
						this.enviaMsg("estado", this.estado, ic.getCliente());
					}
					if(ic.getIntensidade() != this.intensidade){
						ic.setIntensidade(this.intensidade);
						this.enviaMsg("intensidade", Integer.toString(this.intensidade), ic.getCliente());						
					}
					break;
				}
			}
		}
	}
	@Override
	public void removerEqp(String eqpNome){
		synchronized (this.lstEqp) {			
			for (IlumComum ic : this.lstEqp){
				if (ic.getNome().equals(eqpNome)){
					this.lstEqp.remove(ic);
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
