package socket.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
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
import java.awt.Graphics;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class PanelIlumDecor extends JPanel implements ChangeListener,MouseListener,IPanelAmb{
	//Classe para guardar os dados de cada dispositivo conectado
	public class IlumDecor {
		private Socket cliente;
		private String nome;
		private String estado;
		private int tonalidade;
		
		public IlumDecor(Socket cliente, String nome, String estado, int tonalidade) {
			super();
			this.cliente = cliente;
			this.nome = nome;
			this.estado = estado;
			this.tonalidade = tonalidade;
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

		public int getTonalidade() {
			return tonalidade;
		}

		public void setTonalidade(int tonalidade) {
			this.tonalidade = tonalidade;
		}

		public Socket getCliente() {
			return cliente;
		}
		
	}

	private static final long serialVersionUID = 219121901941984460L;
	private final ImageIcon ilumDecorOff = new ImageIcon("src/socket/imagens/minIlumDecorOff.png");
	private final ImageIcon ilumDecorOn = new ImageIcon("src/socket/imagens/minIlumDecorOn.png");
	
	private Vector <IlumDecor> lstEqp;
	private String estado;
	private int tonalidade;
	
	private JLabel lblIcon;
	private JSlider sldIntens;
	private Ambiente amb;
	private JLabel lblRemover;
	private JLabel lblTotal;

	public PanelIlumDecor(Ambiente amb) {
		this.amb = amb;
		this.lstEqp = new Vector<IlumDecor>();
		this.estado = "off";
		this.tonalidade = 5;
		setLayout(new MigLayout("", "[30px][100%]", "[][]"));
		
		lblIcon = new JLabel(ilumDecorOff);
		lblIcon.addMouseListener(this);
		add(lblIcon, "cell 0 0,alignx left,aligny top");
		
		sldIntens = new JSlider(JSlider.HORIZONTAL,1,5,1);
		sldIntens.setValue(5);
		sldIntens.setEnabled(false);
		sldIntens.addChangeListener(this);
		sldIntens.setMajorTickSpacing(1);
		sldIntens.setPaintTicks(true);
		Hashtable<Integer, JLabel> lbltable = new Hashtable<Integer, JLabel>();
		lbltable.put(new Integer(1), new JLabel(criarIconeColorido(0,255,0)));
		lbltable.put(new Integer(2), new JLabel(criarIconeColorido(255,255,0)));
		lbltable.put(new Integer(3), new JLabel(criarIconeColorido(152,60,25)));
		lbltable.put(new Integer(4), new JLabel(criarIconeColorido(77,129,204)));
		lbltable.put(new Integer(5), new JLabel(criarIconeColorido(153,56,103)));
		sldIntens.setLabelTable(lbltable);
		sldIntens.setPaintLabels(true);
		sldIntens.setSnapToTicks(true);
		add(sldIntens, "cell 1 0,growx,aligny top");
		
		lblTotal = new JLabel("[1]");
		lblTotal.setForeground(new Color(0, 139, 219));
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblTotal, "cell 0 1,alignx center");
		
		lblRemover = new JLabel("Remover");
		lblRemover.addMouseListener(this);
		lblRemover.setForeground(new Color(0, 139, 219));
		lblRemover.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblRemover, "cell 1 1,alignx right");
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
			if(JOptionPane.showConfirmDialog(this, "Desconectar todas as iluminações decorativas deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.enviaMsgTodos("desconectar", null);
				this.amb.removePanelEqp(this);
			}			
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sldIntens){
			if(this.sldIntens.getValue() != this.tonalidade){
				this.atualizaTonalidade(sldIntens.getValue());
				enviaMsgTodos("tonalidade", Integer.toString(sldIntens.getValue()));				
			}
		}		
	}
	public void atualizaTonalidade(int valor){
		this.tonalidade = valor;
		synchronized (this.lstEqp) {			
			for (IlumDecor id : this.lstEqp){
				id.setTonalidade(valor);
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
				this.lblIcon.setIcon(ilumDecorOff);
				this.sldIntens.setEnabled(false);
			}
			else{
				this.atualizaEstado("on");
				this.enviaMsgTodos("estado", "on");
				this.lblIcon.setIcon(ilumDecorOn);
				this.sldIntens.setEnabled(true);
			}
		}
	}
	public void atualizaEstado(String valor){
		this.estado = valor;
		synchronized (this.lstEqp) {		
			for (IlumDecor id : this.lstEqp){
				id.setEstado(valor);
			}
		}
	}
	public void mudaTonalidade(String valor){
		if(this.tonalidade != Integer.parseInt(valor)){			
			this.atualizaTonalidade(sldIntens.getValue());
			enviaMsgTodos("tonalidade", valor);			
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
			this.lstEqp.add(new IlumDecor(cliente, eqpNome, "off", 5));
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	public void iniciaValores(String eqpNome){
		synchronized (this.lstEqp) {			
			for (IlumDecor id : this.lstEqp){
				if(id.getNome().equals(eqpNome)){
					if(!id.getEstado().equals(this.estado)){
						id.setEstado(this.estado);
						this.enviaMsg("estado", this.estado, id.getCliente());
					}
					if(id.getTonalidade() != this.tonalidade){
						id.setTonalidade(this.tonalidade);
						this.enviaMsg("tonalidade", Integer.toString(this.tonalidade), id.getCliente());						
					}
					break;
				}
			}
		}
	}
	@Override
	public void removerEqp(String eqpNome){
		synchronized (this.lstEqp) {			
			for (IlumDecor id : this.lstEqp){
				if (id.getNome().equals(eqpNome)){
					this.lstEqp.remove(id);
					this.lblTotal.setText("["+this.lstEqp.size()+"]");
					if(this.lstEqp.size() == 0){
						amb.removePanelEqp(this);
					}
					break;
				}
			}
		}
	}
	//Cria os icones das tonalidades
	private ImageIcon criarIconeColorido(int R, int G, int B){
		int comprimento = 34;
		int altura = 14;
		BufferedImage img = new BufferedImage(comprimento, altura, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(R,G,B));
		g.fillRect(0, 0, comprimento, altura);
		g.dispose();
		return new ImageIcon(img);
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
