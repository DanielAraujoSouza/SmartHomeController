package mqtt.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mqtt.servidor.Ambiente;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class PanelPorta extends JPanel implements MouseListener, IPanelAmb{

	private static final long serialVersionUID = 4572163047026275801L;
	private final ImageIcon portaAberta = new ImageIcon("src/mqtt/imagens/portaAbertaMin.png");
	private final ImageIcon portaFechada = new ImageIcon("src/mqtt/imagens/portaFechadaMin.png");
	private final ImageIcon portaTrancada = new ImageIcon("src/mqtt/imagens/portaTrancadaMin.png");
	private final ImageIcon trancarOn = new ImageIcon("src/mqtt/imagens/trancarMini.png");
	private final ImageIcon trancarOff = new ImageIcon("src/mqtt/imagens/trancarMiniOff.png");
	
	private Ambiente amb;
	private String pNome;
	private String estado;

	private JLabel lblIcon;
	private JLabel lblRemover;
	private JLabel lblTranca;
	private JLabel lblEstado;

	public PanelPorta(Ambiente amb) {
		this.amb = amb;
		this.pNome = null;
		this.estado = "fechada";
		setLayout(new MigLayout("", "[40px:n][100%][40px:n]", "[][]"));

		lblIcon = new JLabel(portaFechada);
		add(lblIcon, "cell 0 0,alignx left,aligny top");

		lblRemover = new JLabel("Remover");
		lblRemover.addMouseListener(this);		
		
		lblEstado = new JLabel("Porta Fechada");
		lblEstado.setFont(new Font("Verdana", Font.PLAIN, 16));
		lblEstado.setForeground(new Color(77,129,204));
		add(lblEstado, "cell 1 0");
		
		lblTranca = new JLabel(trancarOff);
		lblTranca.addMouseListener(this);
		add(lblTranca, "cell 2 0");
		lblRemover.setForeground(new Color(0, 139, 219));
		lblRemover.setFont(new Font("Tahoma", Font.PLAIN, 9));
		add(lblRemover, "cell 2 1,alignx right");
	}
	@Override
	public Ambiente getAmb() {
		return amb;
	}
	public String getNome(){
		return this.pNome;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == lblTranca){
			if(this.estado.equals("aberta")){
				JOptionPane.showMessageDialog(this,  "Não é possivel trancar a porta com ela aberta!", "Erro", JOptionPane.ERROR_MESSAGE);
			}
			else if(this.estado.equals("fechada")){
				this.mudaEstado("trancada");
				this.publicar("Estado", "trancada");	
			}
			else if(this.estado.equals("trancada")){
				this.mudaEstado("fechada");
				this.publicar("Estado", "fechada");	
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar '" + this.pNome + "'  deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/Porta", "Desconectar");
				this.amb.removePanelEqp(this);
			}			
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("fechada")){
				this.estado = valor;		
				this.lblIcon.setIcon(portaFechada);
				this.lblTranca.setIcon(trancarOff);
				this.lblEstado.setText("Porta Fechada");
			}
			else if(valor.equals("trancada")){
				this.estado = valor;		
				this.lblIcon.setIcon(portaTrancada);
				this.lblTranca.setIcon(trancarOn);
				this.lblEstado.setText("Porta Trancada");
			}
			else if(valor.equals("aberta")){
				this.estado = valor;
				this.lblIcon.setIcon(portaAberta);
				this.lblTranca.setIcon(trancarOff);
				this.lblEstado.setText("Porta Aberta");
			}
		}		
	}	
	@Override
	public void addEqp(String eqpNome){
		this.pNome = eqpNome;
	}
	
	//Envia mensagens a um equipamento	
	@Override
	public void publicar(String topico, String mensagem) {
		// TODO Auto-generated method stub
		this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/Porta/"+this.pNome+"/"+topico, mensagem);
	}
	
	@Override
	public boolean descEqp(String eqpNome){
		amb.removePanelEqp(this);
		return true;
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
	@Override
	public void iniciaValores(String eqpNome){	
		// TODO Auto-generated method stub
	}
}
