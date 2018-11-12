package mqtt.simuladores;
import java.awt.EventQueue;

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
import java.awt.Font;

/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class SimIlumComum extends JFrame implements ChangeListener, MouseListener, ActionListener, MqttCallback{

	private static final long serialVersionUID = 6294195781285642478L;
	private String estado;
	private int intensidade;
	private String dispNome;
	private String ambNome;
	private MqttClient cliente;
	private Timer timer;
	private Boolean descMonitor;
	
	private final ImageIcon ilumicaoOff = new ImageIcon("src/mqtt/imagens/iluminacao_off.png");
	private final ImageIcon ilumicao20 = new ImageIcon("src/mqtt/imagens/iluminacao_20.png");
	private final ImageIcon ilumicao40 = new ImageIcon("src/mqtt/imagens/iluminacao_40.png");
	private final ImageIcon ilumicao60 = new ImageIcon("src/mqtt/imagens/iluminacao_60.png");
	private final ImageIcon ilumicao80 = new ImageIcon("src/mqtt/imagens/iluminacao_80.png");
	private final ImageIcon ilumicao100 = new ImageIcon("src/mqtt/imagens/iluminacao_100.png");
	private final ImageIcon btnOn = new ImageIcon("src/mqtt/imagens/estado_ligada.png");
	private final ImageIcon btnOff = new ImageIcon("src/mqtt/imagens/estado_desligada.png");
	
	private JPanel contentPane;
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JSlider sldIntensi;
	private JLabel lblImagem;
	private JLabel btnLigar;
	private JButton btnConectar;
	private JLabel lblLocal;
	private JPanel panelCentral;
	private JPanel panelFunc;
	private Font fontv11;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SimIlumComum frame = new SimIlumComum();
					frame.setResizable(false);
					frame.setVisible(true);
					frame.setTitle("Simulação - Iluminação Comum");
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

	public SimIlumComum() {		
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "IlumComum_" + n;
		this.descMonitor = false;
		this.estado = "off";
		this.intensidade = 5;
		this.ambNome = null;
		this.cliente = null;
		
		setBounds(100, 100, 691, 450);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[350,fill][300px,grow,center]", "[100%,grow]"));
		fontv11 = new Font("Verdana", Font.PLAIN, 11);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[100%,grow]", "[20%,grow][30%,grow][50%,grow]"));
		
		JPanel panelEquip = new JPanel();
		panelEquip.setBorder(new TitledBorder(null, "Informa\u00E7\u00F5es do Dispositivo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panelEquip, "cell 0 0,grow");
		panelEquip.setLayout(new MigLayout("", "[60%,grow]", "[40%][60%]"));
		
		JLabel lblNewLabel_1 = new JLabel("Nome");
		lblNewLabel_1.setFont(fontv11);
		panelEquip.add(lblNewLabel_1, "cell 0 0");
		
		tfDispName = new JTextField(dispNome);
		tfDispName.setFont(fontv11);
		tfDispName.setEditable(false);
		panelEquip.add(tfDispName, "cell 0 1,growx");
		tfDispName.setColumns(10);
		
		panelCentral = new JPanel();
		panelCentral.setBorder(new TitledBorder(null, "Broker", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(panelCentral, "cell 0 1,grow");
		panelCentral.setLayout(new MigLayout("", "[60%,grow][40%,grow]", "[][][][]"));
		
		JLabel lblEndereoIp = new JLabel("Endere\u00E7o IP");
		lblEndereoIp.setFont(fontv11);
		panelCentral.add(lblEndereoIp, "cell 0 0");
		
		JLabel lblPorta_1 = new JLabel("Porta");
		lblPorta_1.setFont(fontv11);
		panelCentral.add(lblPorta_1, "cell 1 0");
		
		tfEndereco = new JTextField();
		tfEndereco.setFont(fontv11);
		tfEndereco.setText("localhost");
		panelCentral.add(tfEndereco, "cell 0 1,growx");
		tfEndereco.setColumns(10);
		
		tfPorta = new JTextField();
		tfPorta.setFont(fontv11);
		tfPorta.setText("1883");
		panelCentral.add(tfPorta, "cell 1 1,growx");
		tfPorta.setColumns(10);
		
		lblLocal = new JLabel("Ambiente");
		lblLocal.setFont(new Font("Verdana", Font.PLAIN, 9));
		lblLocal.setVisible(false);
		panelCentral.add(lblLocal, "cell 0 2,alignx left,aligny bottom");
		
		btnConectar = new JButton("Conectar");
		btnConectar.setFont(fontv11);
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
		
		JLabel lblIntensidade = new JLabel("Intensidade");
		lblIntensidade.setFont(fontv11);
		panelFunc.add(lblIntensidade, "cell 0 1");
		
		sldIntensi = new JSlider(JSlider.HORIZONTAL,1,5,1);
		sldIntensi.setFont(fontv11);
		sldIntensi.setValue(5);
		sldIntensi.setEnabled(false);
		sldIntensi.addChangeListener(this);
		sldIntensi.setMajorTickSpacing(1);
		sldIntensi.setPaintTicks(true);
		Hashtable<Integer, JLabel> lbltable = new Hashtable<Integer, JLabel>();
		lbltable.put(new Integer(1), new JLabel("20%"));
		lbltable.put(new Integer(2), new JLabel("40%"));
		lbltable.put(new Integer(3), new JLabel("60%"));
		lbltable.put(new Integer(4), new JLabel("80%"));
		lbltable.put(new Integer(5), new JLabel("100%"));
		sldIntensi.setLabelTable(lbltable);
		sldIntensi.setPaintLabels(true);
		sldIntensi.setSnapToTicks(true);
		panelFunc.add(sldIntensi, "cell 0 2,grow");
		lblImagem = new JLabel(ilumicaoOff);
		contentPane.add(lblImagem, "cell 1 0,alignx center,growy");
		
		this.timer = new Timer(500, this);
		timer.setActionCommand("timerDisc");
		timer.start();
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == sldIntensi){			
			if(this.cliente != null && this.intensidade != sldIntensi.getValue()){
				if(this.publicar("Intensidade", Integer.toString(sldIntensi.getValue()), true)){
					this.mudaIntensidade(sldIntensi.getValue());
				}
			}			
		}
	}
	public void mudaIntensidade(int intensidade){
		this.intensidade = intensidade;
		this.sldIntensi.setValue(intensidade);
		if(this.estado.equals("on")){			
			if(intensidade == 1){
				this.lblImagem.setIcon(ilumicao20);
			}
			else if(intensidade == 2){
				this.lblImagem.setIcon(ilumicao40);
			}
			else if(intensidade == 3){
				this.lblImagem.setIcon(ilumicao60);
			}
			else if(intensidade == 4){
				this.lblImagem.setIcon(ilumicao80);
			}
			else if(intensidade == 5){
				this.lblImagem.setIcon(ilumicao100);
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
			this.sldIntensi.setEnabled(false);
			this.lblImagem.setIcon(ilumicaoOff);
		}
		else{
			this.estado = "on";
			this.btnLigar.setIcon(btnOn);
			this.sldIntensi.setEnabled(true);
			this.mudaIntensidade(sldIntensi.getValue());
		}
	}
	@Override
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
		this.sldIntensi.setValue(5);
		this.sldIntensi.setEnabled(false);
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
				cliente.subscribe("SmartHome/"+mensagem+"/IlumComum/#",2);
				
			} catch (MqttException e) { }
		}
		else if(topic.equals("SmartHome/" + this.ambNome + "/IlumComum/Estado") || topic.equals("SHconf/Registro/"+this.dispNome+"/IniEsta")){
			this.mudaEstado(mensagem);
		}
		else if(topic.equals("SmartHome/" + this.ambNome + "/IlumComum/Intensidade") || topic.equals("SHconf/Registro/"+this.dispNome+"/IniInte")){
			this.mudaIntensidade(Integer.parseInt(mensagem));
		}
		else if((topic.equals("SmartHome/"+this.ambNome) || topic.equals("SmartHome/" + this.ambNome + "/IlumComum"))  &&  mensagem.equals("Desconectar")){
			this.descMonitor = true;
		}
	}
	public boolean publicar(String topico, String mensagem, Boolean cabecalho){
		try {
			if(cabecalho){
				this.cliente.publish("SmartHome/"+this.ambNome+"/IlumComum/"+topico,new MqttMessage(mensagem.getBytes()));
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
