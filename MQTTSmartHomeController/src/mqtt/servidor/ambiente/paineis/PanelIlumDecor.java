package mqtt.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mqtt.servidor.Ambiente;
import net.miginfocom.swing.MigLayout;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class PanelIlumDecor extends JPanel implements ChangeListener,MouseListener,IPanelAmb{
	
	private static final long serialVersionUID = 219121901941984460L;
	private final ImageIcon IlumDecorOff = new ImageIcon("src/mqtt/imagens/minIlumDecorOff.png");
	private final ImageIcon IlumDecorOn = new ImageIcon("src/mqtt/imagens/minIlumDecorOn.png");
	
	private Ambiente amb;
	private String estado;
	private int tonalidade;
	private Vector <String> lstEqp;	
	
	private JLabel lblIcon;
	private JSlider sldTonal;
	private JLabel lblRemover;
	private JLabel lblTotal;

	public PanelIlumDecor(Ambiente amb) {
		this.amb = amb;
		this.estado = "off";
		this.tonalidade = 5;
		this.lstEqp = new Vector<String>();
		setLayout(new MigLayout("", "[30px][100%]", "[][]"));
		
		lblIcon = new JLabel(IlumDecorOff);
		lblIcon.addMouseListener(this);
		add(lblIcon, "cell 0 0,alignx left,aligny top");
		
		sldTonal = new JSlider(JSlider.HORIZONTAL,1,5,1);
		sldTonal.setValue(5);
		sldTonal.setEnabled(false);
		sldTonal.addChangeListener(this);
		sldTonal.setMajorTickSpacing(1);
		sldTonal.setPaintTicks(true);
		Hashtable<Integer, JLabel> lbltable = new Hashtable<Integer, JLabel>();
		lbltable.put(new Integer(1), new JLabel(criarIconeColorido(0,255,0)));
		lbltable.put(new Integer(2), new JLabel(criarIconeColorido(255,255,0)));
		lbltable.put(new Integer(3), new JLabel(criarIconeColorido(152,60,25)));
		lbltable.put(new Integer(4), new JLabel(criarIconeColorido(77,129,204)));
		lbltable.put(new Integer(5), new JLabel(criarIconeColorido(153,56,103)));
		sldTonal.setLabelTable(lbltable);
		sldTonal.setPaintLabels(true);
		sldTonal.setSnapToTicks(true);
		add(sldTonal, "cell 1 0,growx,aligny top");
		
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
				this.publicar("Estado", "on");	
				
			}
			else{
				this.mudaEstado("off");
				this.publicar("Estado", "off");	
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todas as iluminações decorativas deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/IlumDecor", "Desconectar");
				this.amb.removePanelEqp(this);
			}			
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("off")){
				this.estado = valor;						
				this.lblIcon.setIcon(IlumDecorOff);
				this.sldTonal.setEnabled(false);
			}
			else{
				this.estado = valor;
				this.lblIcon.setIcon(IlumDecorOn);
				this.sldTonal.setEnabled(true);
			}
		}		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sldTonal){
			if(this.sldTonal.getValue() != this.tonalidade){
				this.tonalidade = this.sldTonal.getValue();
				publicar("Tonalidade", Integer.toString(sldTonal.getValue()));			
			}
		}		
	}
	public void mudaTonalidade(String valor){
		int v = Integer.parseInt(valor);
		if(this.tonalidade != v){			
			this.tonalidade = v;		
			this.sldTonal.setValue(v);			
		}
	}
	@Override
	public void addEqp(String eqpNome){
		synchronized (this.lstEqp) {
			this.lstEqp.add(eqpNome);
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	public void iniciaValores(String eqpNome){
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniEsta", this.estado);
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniTonal", Integer.toString(this.tonalidade));
	}
	@Override
	public boolean descEqp(String eqpNome){
		if(!eqpNome.split("_")[0].equals("IlumDecor")){
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
		this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/IlumDecor/"+topico, mensagem);
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
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
