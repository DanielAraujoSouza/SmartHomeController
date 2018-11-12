package mqtt.servidor.ambiente.paineis;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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

public class PanelIrrigacao extends JPanel implements MouseListener, IPanelAmb{
	
	private static final long serialVersionUID = 872981041170115154L;
	private final ImageIcon irrigacaoOn = new ImageIcon("src/mqtt/imagens/irrigacaoMinOn.png");
	private final ImageIcon irrigacaoOff = new ImageIcon("src/mqtt/imagens/irrigacaoMinOff.png");
	private final ImageIcon UmidadeSeco = new ImageIcon("src/mqtt/imagens/UmidadeMinSeco.png");	
	private final ImageIcon UmidadeUmido = new ImageIcon("src/mqtt/imagens/UmidadeMinUmido.png");
	private final ImageIcon UmidadeMolhado = new ImageIcon("src/mqtt/imagens/UmidadeMinMolhado.png");
	private final ImageIcon UmidadeEnxarcado = new ImageIcon("src/mqtt/imagens/UmidadeMinEnxarcado.png");	
	private final ImageIcon irrigacaoAuto = new ImageIcon("src/mqtt/imagens/irrigacaoMinAuto.png");
	private final ImageIcon irrigacaoAutoOff = new ImageIcon("src/mqtt/imagens/irrigacaoMinAutoOff.png");
	private final DecimalFormat df = new DecimalFormat("0");
	
	private Ambiente amb;
	private Map <String,Integer> lstEqp; // eqpNome, NivelUmidade
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
		this.lstEqp = new HashMap<String,Integer>();
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
				this.publicar("Estado", "on");
			}
			else{
				this.mudaEstado("off");
				this.publicar("Estado", "off");
			}
		}
		else if(e.getSource() == lblModo){
			if(this.modo.equals("auto")){
				this.mudaModo("manual");
				this.publicar("ModoIr", "manual");
			}
			else{
				this.mudaModo("auto");
				this.publicar("ModoIr", "auto");
			}
		}
		else if(e.getSource() == lblRemover){
			if(JOptionPane.showConfirmDialog(this, "Desconectar todos irrigadores deste ambiente?", amb.getNome(),JOptionPane.YES_NO_OPTION) == 0){
				this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/IrrigJrdm", "Desconectar");
				this.amb.removePanelEqp(this);
			}			
		}
	}	
	@Override
	public void mudaEstado(String valor) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(valor)){
			if(valor.equals("off")){			
				this.lblIcon.setIcon(irrigacaoOff);
				this.estado = "off";
			}
			else{
				this.lblIcon.setIcon(irrigacaoOn);
				this.estado = "on";
			}
		}		
	}
	public void mudaModo(String valor) {
		// TODO Auto-generated method stub
		if(!this.modo.equals(valor)){
			if(valor.equals("auto")){	
				this.lblModo.setIcon(irrigacaoAuto);
				this.modo = "auto";
			}
			else{	
				this.lblModo.setIcon(irrigacaoAutoOff);
				this.modo = "manual";
			}
		}		
	}
	@Override
	public void addEqp(String eqpNome){
		synchronized (this.lstEqp) {
			this.lstEqp.put(eqpNome, 60);
			this.lblTotal.setText("["+lstEqp.size()+"]");
		}	
	}
	@Override
	public void iniciaValores(String eqpNome){
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniEsta", this.estado);
		this.amb.getCentral().publicar("SHconf/Registro/"+eqpNome+"/IniMIr", this.modo);
	}
	@Override
	public boolean descEqp(String eqpNome){
		if(!eqpNome.split("_")[0].equals("IlumComum")){
			return false;
		}
		synchronized (this.lstEqp) {
			if(this.lstEqp.containsKey(eqpNome)){			
				this.lstEqp.remove(eqpNome);
				this.lblTotal.setText("["+this.lstEqp.size()+"]");
				if(this.lstEqp.size() == 0){
					this.amb.removePanelEqp(this);
				}
				return true;
			}
		}
		return false;
	}
	public void mudaUmidade(String valor, String eqpNome) {
		double media = 0;
		int umd = Integer.parseInt(valor);
		
		synchronized (this.lstEqp) {		
			for (String key : this.lstEqp.keySet()){
				if (key.equals(eqpNome)){
					this.lstEqp.replace(key, umd);
				}
				media += this.lstEqp.get(key);
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
		}
	}
	@Override
	public void publicar(String topico, String mensagem) {
		// TODO Auto-generated method stub
		this.amb.getCentral().publicar("SmartHome/"+amb.getNome()+"/IrrigJrdm/"+topico, mensagem);
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
