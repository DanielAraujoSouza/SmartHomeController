package mqtt.simuladores;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.util.Hashtable;
import java.util.Random;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.Font;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class SimIlumDecor extends JFrame implements ChangeListener, MouseListener, ActionListener, MqttCallback{

	private static final long serialVersionUID = -308387519874440288L;
	private String estado;
	private int tonalidade;
	private String dispNome;
	private String ambNome;
	private MqttClient cliente;
	private Timer timer;
	private Boolean descMonitor;
	
	private ImageIcon ilumicaoOff = new ImageIcon("src/mqtt/imagens/ilumDecor_off.png");
	private ImageIcon ilumicaoVrd = new ImageIcon("src/mqtt/imagens/ilumDecor_vrd.png");
	private ImageIcon ilumicaoAmr = new ImageIcon("src/mqtt/imagens/ilumDecor_amr.png");
	private ImageIcon ilumicaoVrm = new ImageIcon("src/mqtt/imagens/ilumDecor_vrm.png");
	private ImageIcon ilumicaoAzl = new ImageIcon("src/mqtt/imagens/ilumDecor_azl.png");
	private ImageIcon ilumicaoRos = new ImageIcon("src/mqtt/imagens/ilumDecor_ros.png");
	private ImageIcon btnOn = new ImageIcon("src/mqtt/imagens/estado_ligada.png");
	private ImageIcon btnOff = new ImageIcon("src/mqtt/imagens/estado_desligada.png");
	
	private JPanel contentPane;
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JSlider sldTonal;
	private JLabel lblImagem;
	private JLabel btnLigar;
	private JButton btnConectar;
	private JLabel lblLocal;
	private JPanel panelCentral;
	private JPanel panelFunc;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SimIlumDecor frame = new SimIlumDecor();
					frame.setResizable(false);
					frame.setVisible(true);
					frame.setTitle("Simulação - Iluminação Decorativa");
					frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					frame.addWindowListener(new WindowListener() {						
						@Override
						public void windowClosing(WindowEvent arg0) {
							// TODO Auto-generated method stub
							if(JOptionPane.showConfirmDialog(null, "Deseja Fechar?")==JOptionPane.OK_OPTION){		
								System.exit(0);
							}							
						}
						@Override
						public void windowActivated(WindowEvent arg0) {}						
						@Override
						public void windowClosed(WindowEvent arg0) {}
						@Override
						public void windowDeactivated(WindowEvent arg0) {}
						@Override
						public void windowDeiconified(WindowEvent arg0) {}
						@Override
						public void windowIconified(WindowEvent arg0) {}
						@Override
						public void windowOpened(WindowEvent arg0) {}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SimIlumDecor() {
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "IlumDecor_" + n;
		this.descMonitor = false;
		this.estado = "off";
		this.tonalidade = 5;
		this.ambNome = null;
		this.cliente = null;
		
		setBounds(100, 100, 691, 450);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[350,fill][300px,grow,center]", "[100%,grow]"));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[100%,grow]", "[20%,grow][30%,grow][50%,grow]"));
		
		JPanel panelEquip = new JPanel();
		panelEquip.setBorder(new TitledBorder(null, "Informa\u00E7\u00F5es do Dispositivo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panelEquip, "cell 0 0,grow");
		panelEquip.setLayout(new MigLayout("", "[60%,grow]", "[40%][60%]"));
		
		JLabel lblNewLabel_1 = new JLabel("Nome");
		panelEquip.add(lblNewLabel_1, "cell 0 0");
		
		tfDispName = new JTextField(dispNome);
		tfDispName.setEditable(false);
		panelEquip.add(tfDispName, "cell 0 1,growx");
		tfDispName.setColumns(10);
		
		panelCentral = new JPanel();
		panelCentral.setBorder(new TitledBorder(null, "Broker", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panelCentral, "cell 0 1,grow");
		panelCentral.setLayout(new MigLayout("", "[60%,grow][40%,grow]", "[][][][]"));
		
		JLabel lblEndereoIp = new JLabel("Endere\u00E7o IP");
		panelCentral.add(lblEndereoIp, "cell 0 0");
		
		JLabel lblPorta_1 = new JLabel("Porta");
		panelCentral.add(lblPorta_1, "cell 1 0");
		
		tfEndereco = new JTextField();
		tfEndereco.setText("localhost");
		panelCentral.add(tfEndereco, "cell 0 1,growx");
		tfEndereco.setColumns(10);
		
		tfPorta = new JTextField();
		tfPorta.setText("1883");
		panelCentral.add(tfPorta, "cell 1 1,growx");
		tfPorta.setColumns(10);
		
		lblLocal = new JLabel("Ambiente");
		lblLocal.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblLocal.setVisible(false);
		panelCentral.add(lblLocal, "cell 0 2,alignx left,aligny bottom");
		
		btnConectar = new JButton("Conectar");
		btnConectar.addActionListener(this);
		panelCentral.add(btnConectar, "cell 1 2,grow");
		
		panelFunc = new JPanel();
		panelFunc.setVisible(false);
		panelFunc.setBorder(new TitledBorder(null, "Fun\u00E7\u00F5es", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panelFunc, "cell 0 2,grow");
		panelFunc.setLayout(new MigLayout("", "[100%]", "[][][]"));
		
		btnLigar = new JLabel(btnOff);		
		panelFunc.add(btnLigar, "cell 0 0");
		btnLigar.addMouseListener(this);
		
		JLabel lblTonalidade = new JLabel("Tonalidade");
		panelFunc.add(lblTonalidade, "cell 0 1");
		
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
		panelFunc.add(sldTonal, "cell 0 2,grow");
		lblImagem = new JLabel(ilumicaoOff);
		contentPane.add(lblImagem, "cell 1 0,alignx center,growy");
		
		this.timer = new Timer(500, this);
		timer.setActionCommand("timerDisc");
		timer.start();
	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sldTonal){			
			if(this.cliente != null && this.tonalidade != sldTonal.getValue()){
				if(this.publicar("Tonalidade", Integer.toString(sldTonal.getValue()), true)){
					this.mudaTonalidade(sldTonal.getValue());
				}
			}			
		}
	}
	public void mudaTonalidade(int tonalidade){
		this.tonalidade = tonalidade;
		this.sldTonal.setValue(tonalidade);
		if(this.estado.equals("on")){			
			if(tonalidade == 1){
				this.lblImagem.setIcon(ilumicaoVrd);
			}
			else if(tonalidade == 2){
				this.lblImagem.setIcon(ilumicaoAmr);
			}
			else if(tonalidade == 3){
				this.lblImagem.setIcon(ilumicaoVrm);
			}
			else if(tonalidade == 4){
				this.lblImagem.setIcon(ilumicaoAzl);
			}
			else if(tonalidade == 5){
				this.lblImagem.setIcon(ilumicaoRos);
			}
		}		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
		if(e.getSource() == btnLigar){
			if(this.estado == "on"){
				if(publicar("Estado", "off", true)){
					this.mudaEstado("off");
				}			
			}
			else {
				if(publicar("Estado", "on", true)){
					this.mudaEstado("on");	
				}
			}
		}
	}
	public void mudaEstado (String estado){
		this.estado = estado;
		if(estado.equals("off")){
			this.estado = "off";
			this.btnLigar.setIcon(btnOff);
			this.sldTonal.setEnabled(false);
			this.lblImagem.setIcon(ilumicaoOff);
		}
		else{
			this.estado = "on";
			this.btnLigar.setIcon(btnOn);
			this.sldTonal.setEnabled(true);
			this.mudaTonalidade(sldTonal.getValue());
		}
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnConectar && btnConectar.getText().equals("Conectar")){
			if(this.tfEndereco.getText().equals("")){
				JOptionPane.showMessageDialog(this,  "Endereço Invalido!", "Erro", JOptionPane.ERROR_MESSAGE);
				this.tfEndereco.setText("");
			}
			else if(this.tfPorta.getText().equals("")){
				JOptionPane.showMessageDialog(this,  "Porta Invalida!", "Erro", JOptionPane.ERROR_MESSAGE);
				this.tfPorta.setText("");
			}
			else{
				this.tfEndereco.setEnabled(false);
				this.tfPorta.setEnabled(false);
				this.btnConectar.setEnabled(false);
				
				if(!this.BrokerConnect()){
					this.tfEndereco.setEnabled(true);
					this.tfPorta.setEnabled(true);
					this.btnConectar.setEnabled(true);
					this.cliente = null;
				}
				else{
					this.publicar("SHconf/Registro",this.dispNome, false);
				}
			}
		}
		else if(e.getSource() == btnConectar && btnConectar.getText().equals("Desconectar")){
			this.publicar("SHconf/Desconectar", this.dispNome, false);
			this.desconectarEqp();
		}
		else if(e.getActionCommand().equals("timerDisc")){
			if (descMonitor){
				descMonitor = false;
				this.desconectarEqp();
			}
		}
	}
	public void desconectarEqp() {
		// TODO Auto-generated method stub
		if(this.cliente != null && this.cliente.isConnected()){
			try{
				this.cliente.disconnect();
			}catch(MqttException e){}	
		}		
		
		this.estado = "off";
		this.sldTonal.setValue(5);
		this.sldTonal.setEnabled(false);
		this.lblLocal.setVisible(false);
		this.btnLigar.setIcon(btnOff);		
		this.lblImagem.setIcon(ilumicaoOff);
		this.panelFunc.setVisible(false);
		this.tfEndereco.setEnabled(true);
		this.tfPorta.setEnabled(true);
		this.btnConectar.setText("Conectar");		
		this.btnConectar.setEnabled(true);
	}	
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		this.descMonitor = true;
	}
	@Override
	public void messageArrived(String topic, MqttMessage message){
		// TODO Auto-generated method stub
		String mensagem = new String(message.getPayload());
		
		//Topico de erros
		if(topic.equals("SHconf/Registro/"+this.dispNome+"/Erro") && mensagem.equals("RegErro")){
			JOptionPane.showMessageDialog(this,  "ERRO\nCancelado na Central!", "Erro", JOptionPane.ERROR_MESSAGE);
			this.descMonitor = true;
		}
		else if(topic.equals("SHconf/Desconectar") &&  mensagem.equals("SmartHome")){
			JOptionPane.showMessageDialog(this,  "Central Desconectada", "Erro", JOptionPane.ERROR_MESSAGE);
			this.descMonitor = true;
		}	
		else if(topic.equals("SHconf/Registro/"+this.dispNome)){
			this.ambNome = mensagem;
			this.lblLocal.setText("Ambiente: " + mensagem);
			this.lblLocal.setVisible(true);
			this.btnConectar.setText("Desconectar");
			this.tfEndereco.setEnabled(false);
			this.tfPorta.setEnabled(false);
			this.btnConectar.setEnabled(true);
			this.panelFunc.setVisible(true);
			
			try {
				//Increve-se no topico desse ambiente
				cliente.subscribe("SmartHome/"+mensagem,2);
				//Increve-se nos topicos de estado para este tipo de equipamento neste ambiente
				cliente.subscribe("SmartHome/"+mensagem+"/IlumDecor/#",2);
				
			} catch (MqttException e) { }
		}
		else if(topic.equals("SmartHome/" + this.ambNome + "/IlumDecor/Estado") || topic.equals("SHconf/Registro/"+this.dispNome+"/IniEsta")){
			this.mudaEstado(mensagem);
		}
		else if(topic.equals("SmartHome/" + this.ambNome + "/IlumDecor/Tonalidade") || topic.equals("SHconf/Registro/"+this.dispNome+"/IniTonal")){
			this.mudaTonalidade(Integer.parseInt(mensagem));
		}
		else if((topic.equals("SmartHome/"+this.ambNome) || topic.equals("SmartHome/" + this.ambNome + "/IlumDecor"))  &&  mensagem.equals("Desconectar")){
			this.descMonitor = true;
		}
	}
	public boolean publicar(String topico, String mensagem, Boolean cabecalho){
		try {
			if(cabecalho){
				this.cliente.publish("SmartHome/"+this.ambNome+"/IlumDecor/"+topico,new MqttMessage(mensagem.getBytes()));
			}
			else{
				this.cliente.publish(topico,new MqttMessage(mensagem.getBytes()));
			}
			return true;
		} catch (MqttException e) {
			return false;
		}
	}
	public Boolean BrokerConnect(){
		try {
			//Cria o cliente Mqtt
			this.cliente = new MqttClient("tcp://"+this.tfEndereco.getText()+":"+this.tfPorta.getText(),this.dispNome,new MemoryPersistence());
			//Configura as opçoes de cenexão 
			MqttConnectOptions connOpts = new MqttConnectOptions();
			//Mensagem de disconexao
			connOpts.setWill("SHconf/Desconectar",this.dispNome.getBytes(),2,false);
			//Especifica se a conexão é persistente ou não
			connOpts.setCleanSession(false);
			// Configura chamadas de retorno 
			this.cliente.setCallback(this);
			//Conecta o cliente ao Broker
			cliente.connect(connOpts);
			//Increve-se em todos os deste dispositivo
			cliente.subscribe("SHconf/Registro/"+this.dispNome+"/#",2);			
			//Increve-se no topico de lastWill da central
			cliente.subscribe("SHconf/Desconectar",2);

			return true;
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			this.cliente = null;		
			JOptionPane.showMessageDialog(this,  "Não foi possivel conectar-se ao Broker!", "Erro", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	private ImageIcon criarIconeColorido(int R, int G, int B){
		int comprimento = 38;
		int altura = 20;
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
	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {}
}
