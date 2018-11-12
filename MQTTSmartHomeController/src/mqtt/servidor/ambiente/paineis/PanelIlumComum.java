package mqtt.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import mqtt.servidor.Ambiente;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;
import java.awt.Color;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class PanelIlumComum extends JPanel implements ChangeListener, MouseListener, IPanelAmb{

	private static final long serialVersionUID = -4332586714695973563L;
	private final ImageIcon IlumComumOff = new ImageIcon("src/mqtt/imagens/minIlumComumOff.png");
	private final ImageIcon IlumComumOn = new ImageIcon("src/mqtt/imagens/minIlumComumOn.png");

	private Ambiente amb;
	private String estado;
	private int intensidade;
	private Vector <String> lstEqp;
	
	private JLabel lblIcon;
	private JSlider sldIntens;
	private JLabel lblRemover;
	private JLabel lblTotal;

	public PanelIlumComum(Ambiente amb) {
		this.amb = amb;
		this.estado = "off";
		this.intensidade = 5;
		this.lstEqp = new Vector<String>();
		setLayout(new MigLayout("", "[30px][][100%]", "[][]"));

		lblIcon = new JLabel(IlumComumOff);
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
				this.publicar("Estado", "on");	
				
			}
			else{
				this.mudaEstado("off");
				this.publicar("Estado", "off");	
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todas as iluminações comuns deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/IlumComum", "Desconectar");
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
				this.lblIcon.setIcon(IlumComumOff);
				this.sldIntens.setEnabled(false);
			}
			else{
				this.estado = valor;
				this.lblIcon.setIcon(IlumComumOn);
				this.sldIntens.setEnabled(true);
			}
		}		
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sldIntens){
			if(this.sldIntens.getValue() != this.intensidade){
				this.intensidade = this.sldIntens.getValue();
				publicar("Intensidade", Integer.toString(sldIntens.getValue()));				
			}	
		}		
	}	
	public void mudaIntensidade(String valor){
		int v = Integer.parseInt(valor);
		if(this.intensidade != v){			
			this.intensidade = v;		
			this.sldIntens.setValue(v);			
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
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniInte", Integer.toString(this.intensidade));
	}
	
	@Override
	public boolean descEqp(String eqpNome){
		if(!eqpNome.split("_")[0].equals("IlumComum")){
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
		this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/IlumComum/"+topico, mensagem);
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
