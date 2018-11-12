package mqtt.servidor.ambiente.paineis;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JLabel;


import mqtt.servidor.Ambiente;
import net.miginfocom.swing.MigLayout;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class PanelArCond extends JPanel implements MouseListener,IPanelAmb{

	private static final long serialVersionUID = -1757430387839481275L;
	private final ImageIcon arCondOFF = new ImageIcon("src/mqtt/imagens/minIlumSplitOff.png");
	private final ImageIcon arCondON = new ImageIcon("src/mqtt/imagens/minIlumSplitOn.png");
	private final ImageIcon autoMode = new ImageIcon("src/mqtt/imagens/autoMode.png");
	private final ImageIcon refrigMode = new ImageIcon("src/mqtt/imagens/refrig.png");
	private final ImageIcon desumMode = new ImageIcon("src/mqtt/imagens/umid.png");
	private final ImageIcon ventMode = new ImageIcon("src/mqtt/imagens/ventilar.png");
	private final ImageIcon upMin = new ImageIcon("src/mqtt/imagens/upMin.png");
	private final ImageIcon downMin = new ImageIcon("src/mqtt/imagens/downMin.png");
	
	private Ambiente amb;
	private Vector <String> lstEqp;
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
		this.lstEqp = new Vector<String>();
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
				this.publicar("Estado", "on");	
				
			}
			else{
				this.mudaEstado("off");
				this.publicar("Estado", "off");	
			}
		}
		else if(e.getSource() == lblDown){
			int ntemp = this.temperatura-1;
			if(ntemp >= 10){
				this.mudaTemperatura(Integer.toString(ntemp));
				publicar("Temp", Integer.toString(ntemp));
			}			
		} 
		else if(e.getSource() == lblUp){
			int ntemp = this.temperatura+1;
			if(ntemp <= 30){
				this.mudaTemperatura(Integer.toString(ntemp));
				publicar("Temp", Integer.toString(ntemp));
			}			
		}
		else if(e.getSource() == lblModo){
			if(this.modo.equals("auto")){
				this.mudaModo("refrig");
				publicar("Modo", "refrig");
			}
			else if(this.modo.equals("refrig")){
				this.mudaModo("desum");
				publicar("Modo", "desum");
			}
			else if(this.modo.equals("desum")){
				this.mudaModo("vent");
				publicar("Modo", "vent");
			}
			else if(this.modo.equals("vent")){
				this.mudaModo("auto");
				publicar("Modo", "auto");
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todos os Ar-Condicionados deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/ArCond", "Desconectar");
				this.amb.removePanelEqp(this);
			}			
		}
	}
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("on")){
				this.estado = "on";
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
				this.estado = "off";
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
	public void mudaTemperatura(String valor) {
		// TODO Auto-generated method stub
		int ntemp = Integer.parseInt(valor);
		if(this.temperatura != ntemp){
			this.temperatura = ntemp;
			this.lblTemp.setText(ntemp + " °C");
		}
	}
	public void mudaModo(String valor) {
		// TODO Auto-generated method stub
		if(!this.modo.equals(valor)){
			this.modo = valor;
			if (valor.equals("auto")){
				this.lblModo.setIcon(autoMode);
			}
			else if (valor.equals("refrig")){
				this.lblModo.setIcon(refrigMode);
			}
			else if (valor.equals("desum")){
				this.lblModo.setIcon(desumMode);
			}
			else if (valor.equals("vent")){
				this.lblModo.setIcon(ventMode);
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
	public void iniciaValores(String eqpNome){
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniEsta", this.estado);
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniModo", this.modo);
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniTemp", Integer.toString(this.temperatura));
	}
	@Override
	public boolean descEqp(String eqpNome){
		if(!eqpNome.split("_")[0].equals("ArCond")){
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
	//Informa a medicao periodica de temperatura
	public void mudaTempReal (String valor){
		this.lblTmpAmbiente.setText("Temp. Real: " + valor + " °C");
	}
	@Override
	public void publicar(String topico, String mensagem) {
		// TODO Auto-generated method stub
		this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/ArCond/"+topico, mensagem);
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
