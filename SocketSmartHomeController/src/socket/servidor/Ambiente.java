package socket.servidor;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import socket.servidor.ambiente.paineis.IPanelAmb;
import socket.servidor.ambiente.paineis.PanelArCond;
import socket.servidor.ambiente.paineis.PanelIlumComum;
import socket.servidor.ambiente.paineis.PanelIlumDecor;
import socket.servidor.ambiente.paineis.PanelIrrigacao;
import socket.servidor.ambiente.paineis.PanelJanela;
import socket.servidor.ambiente.paineis.PanelPorta;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class Ambiente extends JPanel implements MouseListener{

	private static final long serialVersionUID = 2631809446519186805L;
	private String nome;
	private final ImageIcon closeIcon = new ImageIcon("src/socket/imagens/ambclose.png");
	private Central central;
	private Vector <IPanelAmb> lstPanelEqp;
	private int line;
	private JPanel panel;
	private JLabel btnDel;

	public Ambiente(String nome, Central central) {
		setBorder(new LineBorder(UIManager.getColor("Button.background")));
		this.line = 1;
		this.lstPanelEqp = new Vector <IPanelAmb> ();
		this.central = central;
		this.setName(nome);
		this.nome = nome;
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[346px,grow]", "[32px][grow]"));
		
		panel = new JPanel();
		add(panel, "cell 0 0,growx,aligny top");
		panel.setLayout(new MigLayout("", "[80%][20%]", "[18px]"));
		
		JLabel label = new JLabel(nome);
		label.setFont(new Font("Verdana", Font.PLAIN, 14));
		panel.add(label, "cell 0 0,alignx left,aligny top");
		
		btnDel = new JLabel(closeIcon);
		btnDel.setName(nome);
		btnDel.addMouseListener(this);
		panel.add(btnDel, "cell 1 0,alignx right,aligny center");		
	}
	public String getNome() {
		return this.nome;
	}
	//Retorna o panel que controla os equipamento do tipo "eqpTipo"
	public IPanelAmb getPanelEqp(String eqpTipo){
		if(eqpTipo.equals("ilumComum")){
			//Se ja existir retorna a referencia
			for(IPanelAmb eqp : this.lstPanelEqp){
				if(eqp instanceof PanelIlumComum){
					return eqp;
				}
			}
			//Se nao existir cria
			PanelIlumComum np = new PanelIlumComum(this);
			this.lstPanelEqp.add(np);
			this.atualizaPaineis();
			return np;
		} else if(eqpTipo.equals("ilumDecor")){
			//Se ja existir retorna a referencia
			for(IPanelAmb eqp : this.lstPanelEqp){
				if(eqp instanceof PanelIlumDecor){
					return eqp;
				}
			}
			//Se nao existir cria
			PanelIlumDecor np = new PanelIlumDecor(this);
			this.lstPanelEqp.add(np);
			this.atualizaPaineis();
			return np;
		} else if(eqpTipo.equals("arCond")){
			//Se ja existir retorna a referencia
			for(IPanelAmb eqp : this.lstPanelEqp){
				if(eqp instanceof PanelArCond){
					return eqp;
				}
			}
			//Se nao existir cria
			PanelArCond np = new PanelArCond(this);
			this.lstPanelEqp.add(np);
			this.atualizaPaineis();
			return np;
		} else if(eqpTipo.equals("janela")){
			//Se ja existir retorna a referencia
			for(IPanelAmb eqp : this.lstPanelEqp){
				if(eqp instanceof PanelJanela){
					return eqp;
				}
			}
			//Se nao existir cria
			PanelJanela np = new PanelJanela(this);
			this.lstPanelEqp.add(np);
			this.atualizaPaineis();
			return np;
		} else if(eqpTipo.equals("porta")){
			PanelPorta np = new PanelPorta(this);
			this.lstPanelEqp.add(np);
			this.atualizaPaineis();
			return np;
		} else if(eqpTipo.equals("irrigJrdm")){
			//Se ja existir retorna a referencia
			for(IPanelAmb eqp : this.lstPanelEqp){
				if(eqp instanceof PanelIrrigacao){
					return eqp;
				}
			}
			//Se nao existir cria
			PanelIrrigacao np = new PanelIrrigacao(this);
			this.lstPanelEqp.add(np);
			this.atualizaPaineis();
			return np;
		}
		return null;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnDel){
			if(JOptionPane.showConfirmDialog(this, "Deseja Apagar '" + this.nome + "'?", "Apagar Ambiente", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION){				
				this.removeEqps();
				this.central.removeAmb(this);
			}
		}
	}
	//Remove um panel do ambiente e atualiza a janela
	public void removePanelEqp (JPanel peqp){
		synchronized (this.lstPanelEqp){
			lstPanelEqp.remove(peqp);
			this.atualizaPaineis();
		}
	}
	public void atualizaPaineis (){
		synchronized (this.lstPanelEqp){
			this.removeAll();
			this.line = 1;
			add(panel, "cell 0 0,growx,aligny top");
			for (IPanelAmb panel : lstPanelEqp){
				this.add((JPanel)panel, "cell 0 " + this.line + ",growx,aligny top");
				this.line++;			
			}
			this.repaint();
			this.revalidate();
		}
	}
	public void removeEqps(){
		synchronized (this.lstPanelEqp){
			for (IPanelAmb eqp : lstPanelEqp){
				eqp.enviaMsgTodos("desconectar", null);		
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

