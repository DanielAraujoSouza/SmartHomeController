package socket.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
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

public class PanelIrrigacao extends JPanel implements MouseListener, IPanelAmb{

	//Classe para guardar os dados de cada dispositivo conectado
	public class Irrigacao{
		private Socket cliente;
		private String nome;
		private String estado;
		private String modo;
		private int nivelUmidade;
		public Irrigacao(Socket cliente, String nome, String estado, String modo, int nivelUmidade) {
			super();
			this.cliente = cliente;
			this.nome = nome;
			this.estado = estado;
			this.modo = modo;
			this.nivelUmidade = nivelUmidade;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
		public String getModo() {
			return modo;
		}
		public void setModo(String modo) {
			this.modo = modo;
		}
		public int getNivelUmidade() {
			return nivelUmidade;
		}
		public void setNivelUmidade(int nivelUmidade) {
			this.nivelUmidade = nivelUmidade;
		}
		public Socket getCliente() {
			return cliente;
		}
		public String getNome() {
			return nome;
		}
		
	}
	private static final long serialVersionUID = 872981041170115154L;
	private final ImageIcon irrigacaoOn = new ImageIcon("src/socket/imagens/irrigacaoMinOn.png");
	private final ImageIcon irrigacaoOff = new ImageIcon("src/socket/imagens/irrigacaoMinOff.png");
	private final ImageIcon UmidadeSeco = new ImageIcon("src/socket/imagens/UmidadeMinSeco.png");	
	private final ImageIcon UmidadeUmido = new ImageIcon("src/socket/imagens/UmidadeMinUmido.png");
	private final ImageIcon UmidadeMolhado = new ImageIcon("src/socket/imagens/UmidadeMinMolhado.png");
	private final ImageIcon UmidadeEnxarcado = new ImageIcon("src/socket/imagens/UmidadeMinEnxarcado.png");	
	private final ImageIcon irrigacaoAuto = new ImageIcon("src/socket/imagens/irrigacaoMinAuto.png");
	private final ImageIcon irrigacaoAutoOff = new ImageIcon("src/socket/imagens/irrigacaoMinAutoOff.png");
	private final DecimalFormat df = new DecimalFormat("0");
	
	private Ambiente amb;
	private Vector <Irrigacao> lstEqp;
	private String estado;
	private String modo;

	private JLabel lblIcon;
	private JLabel lblRemover;
	private JLabel lblTotal;
	private JLabel lblModo;
	private JLabel lblUmidade;
	private JLabel lblTeor;
	private JLabel lblTeorDegua;

	public PanelIrrigacao(Ambiente amb) {
		this.amb = amb;
		this.lstEqp = new Vector<Irrigacao>();
		this.estado = "off";
		this.modo = "manual";
		setLayout(new MigLayout("", "[30px:30px][30px:30px][100%][40px:40px]", "[][]"));

		lblIcon = new JLabel(irrigacaoOff);
		lblIcon.addMouseListener(this);
		add(lblIcon, "cell 0 0,alignx left,aligny top");
		
		lblUmidade = new JLabel(UmidadeMolhado);
		add(lblUmidade, "cell 1 0");
		
		lblTeor = new JLabel("--");
		lblTeor.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblTeor.setForeground(new Color(77,129,204));
		add(lblTeor, "cell 2 0");
		
		lblModo = new JLabel(irrigacaoAutoOff);
		lblModo.addMouseListener(this);
		add(lblModo, "cell 3 0");

		lblTotal = new JLabel("[1]");
		lblTotal.setForeground(new Color(0, 139, 219));
		lblTotal.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblTotal, "cell 0 1,alignx center");
			
		
		lblTeorDegua = new JLabel("Teor M\u00E9dio de \u00C1gua");
		lblTeorDegua.setForeground(new Color(0, 139, 219));
		lblTeorDegua.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblTeorDegua, "cell 2 1");
		
		lblRemover = new JLabel("Remover");
		lblRemover.addMouseListener(this);	
		lblRemover.setForeground(new Color(0, 139, 219));
		lblRemover.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblRemover, "cell 3 1,alignx right");
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
		else if(e.getSource() == lblModo){
			if(this.modo.equals("auto")){
				this.mudaModo("manual");
			}
			else{
				this.mudaModo("auto");
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todos irrigadores deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.enviaMsgTodos("desconectar", null);
				this.amb.removePanelEqp(this);
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
				this.lblIcon.setIcon(irrigacaoOff);
			}
			else{
				this.atualizaEstado("on");
				this.enviaMsgTodos("estado", "on");
				this.lblIcon.setIcon(irrigacaoOn);
			}
		}		
	}
	public void atualizaEstado(String valor){
		this.estado = valor;
		synchronized (this.lstEqp) {		
			for (Irrigacao ir : this.lstEqp){
				ir.setEstado(valor);
			}
		}
	}
	public void mudaModo(String valor) {
		// TODO Auto-generated method stub
		if(!this.modo.equals(valor)){
			if(valor.equals("auto")){
				this.atualizaModo("auto");
				this.enviaMsgTodos("modoIr", "auto");			
				this.lblModo.setIcon(irrigacaoAuto);
			}
			else{
				this.atualizaModo("manual");
				this.enviaMsgTodos("modoIr", "manual");			
				this.lblModo.setIcon(irrigacaoAutoOff);
			}
		}		
	}
	public void atualizaModo(String valor){
		this.modo = valor;
		synchronized (this.lstEqp) {		
			for (Irrigacao ir : this.lstEqp){
				ir.setModo(valor);
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
			this.lstEqp.add(new Irrigacao(cliente, eqpNome, "off", "manual", 60));
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}
	}
	public void iniciaValores(String eqpNome){
		synchronized (this.lstEqp) {			
			for (Irrigacao ir : this.lstEqp){
				if(ir.getNome().equals(eqpNome)){
					if(!ir.getEstado().equals(this.estado)){
						ir.setEstado(this.estado);
						this.enviaMsg("estado", this.estado, ir.getCliente());
					}
					if(ir.getModo() != this.modo){
						ir.setModo(this.modo);
						this.enviaMsg("modoIr", this.modo, ir.getCliente());						
					}
					break;
				}
			}
		}
	}
	@Override
	public void removerEqp(String eqpNome){
		synchronized (this.lstEqp) {			
			for (Irrigacao ir : this.lstEqp){
				if (ir.getNome().equals(eqpNome)){
					this.lstEqp.remove(ir);
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
	public void mudaUmidade(String valor, String eqpNome) {
		// TODO Auto-generated method stub
		double media = 0;
		boolean ligado = false;
		int umd = Integer.parseInt(valor);
		synchronized (this.lstEqp) {		
			for (Irrigacao ir : this.lstEqp){
				if (ir.getNome().equals(eqpNome)){
					if(umd > ir.getNivelUmidade()){
						ligado = true;
						ir.setEstado("on");
					}
					else if(ir.getEstado().equals("on")){
						ir.setEstado("off");
					}
					ir.setNivelUmidade(umd);
					media += umd;
				}
				else{
					if(ir.getEstado().equals("on")){
						ligado = true;
					}
					media += ir.getNivelUmidade();
				}
			}
			media = media/lstEqp.size();
			this.lblTeor.setText(df.format(media)+"%");
			if(media <= 20){
				this.lblUmidade.setIcon(UmidadeSeco);
			}
			else if (media <= 40){
				this.lblUmidade.setIcon(UmidadeUmido);
			}
			else if (media <= 60){
				this.lblUmidade.setIcon(UmidadeMolhado);
			}
			else{
				this.lblUmidade.setIcon(UmidadeEnxarcado);
			}
			if (this.modo.equals("auto")){
				if(ligado && !this.estado.equals("on")){
					this.estado = "on";
					this.lblIcon.setIcon(irrigacaoOn);
				}
				else if(!ligado && !this.estado.equals("off")){
					this.estado = "off";
					this.lblIcon.setIcon(irrigacaoOff);
				}
			}			
		}
	}

}
