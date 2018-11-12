package socket.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

public class PanelPorta extends JPanel implements MouseListener, IPanelAmb{

	private static final long serialVersionUID = 4572163047026275801L;
	private final ImageIcon portaAberta = new ImageIcon("src/socket/imagens/portaAbertaMin.png");
	private final ImageIcon portaFechada = new ImageIcon("src/socket/imagens/portaFechadaMin.png");
	private final ImageIcon portaTrancada = new ImageIcon("src/socket/imagens/portaTrancadaMin.png");
	private final ImageIcon trancarOn = new ImageIcon("src/socket/imagens/trancarMini.png");
	private final ImageIcon trancarOff = new ImageIcon("src/socket/imagens/trancarMiniOff.png");
	
	private Ambiente amb;
	private Socket cliente;
	private String pNome;
	private String estado;

	private JLabel lblIcon;
	private JLabel lblRemover;
	private JLabel lblTranca;
	private JLabel lblEstado;

	public PanelPorta(Ambiente amb) {
		this.amb = amb;
		this.cliente = null;
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == lblTranca){
			if(this.estado.equals("aberta")){
				JOptionPane.showMessageDialog(this,  "Não é possivel trancar a porta com ela aberta!", "Erro", JOptionPane.ERROR_MESSAGE);
			}
			else if(this.estado.equals("fechada")){
				this.mudaEstado("trancada");
				this.enviaMsg("estado", "trancada", this.cliente);	
			}
			else if(this.estado.equals("trancada")){
				this.mudaEstado("fechada");
				this.enviaMsg("estado", "fechada", this.cliente);	
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar '" + this.pNome + "'  deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.enviaMsg("desconectar", null, this.cliente);
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
		this.cliente = cliente;
		this.pNome = eqpNome;
	}
	@Override
	public void removerEqp(String eqpNome){
		amb.removePanelEqp(this);
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
	public void enviaMsgTodos(String tipo, String valor){	
		// TODO Auto-generated method stub
	}
	@Override
	public void iniciaValores(String eqpNome){	
		// TODO Auto-generated method stub
	}
}
