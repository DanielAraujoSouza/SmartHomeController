package mqtt.servidor.ambiente.paineis;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mqtt.servidor.Ambiente;
import net.miginfocom.swing.MigLayout;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class PanelJanela extends JPanel implements MouseListener, IPanelAmb{
	
	private static final long serialVersionUID = -3284069266299880793L;
	private final ImageIcon janelaIcon = new ImageIcon("src/mqtt/imagens/janelaMini.png");
	private final ImageIcon cortinaAbrir = new ImageIcon("src/mqtt/imagens/cortinaAbrirMini.png");
	private final ImageIcon cortinaFechar = new ImageIcon("src/mqtt/imagens/cortinaFecharMini.png");
	private final ImageIcon trancarOn = new ImageIcon("src/mqtt/imagens/trancarMini.png");
	private final ImageIcon trancarOff = new ImageIcon("src/mqtt/imagens/trancarMiniOff.png");
	private final ImageIcon upMin = new ImageIcon("src/mqtt/imagens/upMin.png");
	private final ImageIcon downMin = new ImageIcon("src/mqtt/imagens/downMin.png");

	private Ambiente amb;
	private String estado;
	private String estadoCortina;
	private Vector <String> lstEqp;

	private JLabel lblIcon;
	private JLabel lblRemover;
	private JLabel lblTranca;
	private JLabel lblEstado;
	private JLabel lblCortina;
	private JLabel lblTotal;

	public PanelJanela(Ambiente amb) {
		super();
		this.amb = amb;
		this.lstEqp = new Vector<String>();
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
				this.publicar("Estado", "aberta");
			}
			else {
				this.mudaEstado("fechada");	
				this.publicar("Estado", "fechada");
			}
		}
		else if (e.getSource() == lblTranca){
			if(this.estado.equals("aberta") || this.estado.equals("fechada")){
				this.mudaEstado("trancada");
				this.publicar("Estado", "trancada");
			}
			else {
				this.mudaEstado("fechada");	
				this.publicar("Estado", "fechada");
			}
		}
		else if (e.getSource() == lblCortina){
			if(this.estadoCortina.equals("aberta")){
				this.mudaEstCortina("fechada");	
				this.publicar("EstCortina", "fechada");
			}
			else {
				//this.estadoCortina = "aberta";
				this.mudaEstCortina("aberta");	
				this.publicar("EstCortina", "aberta");
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todas as janelas deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/Janela", "Desconectar");
				this.amb.removePanelEqp(this);
			}				
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("aberta")){		
				this.lblEstado.setIcon(downMin);
				this.lblTranca.setIcon(trancarOff);
				this.lblCortina.setIcon(cortinaFechar);
				this.estado = "aberta";
				this.estadoCortina = "aberta";

			}
			else if(valor.equals("fechada")){		
				this.lblEstado.setIcon(upMin);
				this.lblTranca.setIcon(trancarOff);
				this.estado = "fechada";
			}
			else if(valor.equals("trancada")){
				this.lblEstado.setIcon(upMin);
				this.lblTranca.setIcon(trancarOn);
				this.estado = "trancada";
			}
		}		
	}
	public void mudaEstCortina (String valor){
		if(!this.estadoCortina.equals(valor)){
			if(valor.equals("fechada")){
				this.lblCortina.setIcon(cortinaAbrir);
				this.estadoCortina = "fechada";
				if(this.estado.equals("aberta")) {						
					this.lblEstado.setIcon(upMin);						
					this.estado = "fechada";
				}
			}
			else {
				this.lblCortina.setIcon(cortinaFechar);
				this.estadoCortina = valor;
			}
		}		
	}
	@Override
	public void addEqp(String eqpNome){
		synchronized (this.lstEqp) {
			this.lstEqp.add(eqpNome);
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	@Override
	public void iniciaValores(String eqpNome){
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniEsta", this.estado);
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniCort", this.estadoCortina);
	}
	@Override
	public boolean descEqp(String eqpNome){
		if(!eqpNome.split("_")[0].equals("Janela")){
			return false;
		}
		synchronized (this.lstEqp) {			
			for (String eqp : this.lstEqp){
				if (eqp.equals(eqpNome)){
					this.lstEqp.remove(eqp);
					this.lblTotal.setText("["+this.lstEqp.size()+"]");
					if(this.lstEqp.size() == 0){
						this.amb.removePanelEqp(this);
					}
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public void publicar(String topico, String mensagem) {
		// TODO Auto-generated method stub
		this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/Janela/"+topico, mensagem);
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
