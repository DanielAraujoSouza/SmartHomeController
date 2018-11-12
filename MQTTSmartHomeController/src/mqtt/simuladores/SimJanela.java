package mqtt.simuladores;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import net.miginfocom.swing.MigLayout;


/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */
public class SimJanela extends JFrame implements MouseListener, ActionListener, MqttCallback{

	private static final long serialVersionUID = 4779396166107831712L;
	private String estado;
	private String estadoCortina;
	private String dispNome;
	private String ambNome;
	private MqttClient cliente;
	private Timer timer;
	private Boolean descMonitor;
	
	private final ImageIcon janelaTrancada = new ImageIcon("src/mqtt/imagens/janelaTrancada.png");
	private final ImageIcon janelaTrancadaB = new ImageIcon("src/mqtt/imagens/janelaTrancadaB.png");
	private final ImageIcon janelaFechada = new ImageIcon("src/mqtt/imagens/janelaFechada.png");
	private final ImageIcon janelaCortina = new ImageIcon("src/mqtt/imagens/janelaCortina.png");
	private final ImageIcon janelaAberta = new ImageIcon("src/mqtt/imagens/janelaAberta.png");
	private final ImageIcon upJanela = new ImageIcon("src/mqtt/imagens/up.png");
	private final ImageIcon downJanela = new ImageIcon("src/mqtt/imagens/down.png");
	private final ImageIcon cortinaAbrir = new ImageIcon("src/mqtt/imagens/cortinaAbrir.png");
	private final ImageIcon cortinaFechar = new ImageIcon("src/mqtt/imagens/cortinaFechar.png");
	private final ImageIcon trancarOn = new ImageIcon("src/mqtt/imagens/trancarOn.png");
	private final ImageIcon trancarOff = new ImageIcon("src/mqtt/imagens/trancarOff.png");
	
	private Font fontv11;
	private JPanel contentPane;	
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JLabel lblImagem;
	private JButton btnConectar;
	private JPanel panelCentral;
	private JPanel panelFunc;
	private JLabel lblLocal;
	private JLabel lblEstado;
	private JLabel lblTranca;
	private JLabel lblCortina;
	private JLabel lblAbrirfechar;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_4;
	private JLabel lblNewLabel_5;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SimJanela frame = new SimJanela();
					frame.setVisible(true);
					frame.setResizable(false);
					frame.setVisible(true);
					frame.setTitle("Simulação - Janela");
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

	public SimJanela() {
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "Janela_" + n;
		this.descMonitor = false;
		this.estado = "trancada";
		this.estadoCortina = "fechada";
		this.ambNome = null;
		this.cliente = null;
		
		setBounds(100, 100, 691, 400);
		contentPane = new JPanel();		
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[350,fill][300px,grow,center]", "[300px:n]"));
		fontv11 = new Font("Verdana", Font.PLAIN, 11);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[100%,grow]", "[][][50px]"));
		
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
		panelFunc.setLayout(new MigLayout("", "[33%][34%][33%]", "[][][][]"));
		
		lblEstado = new JLabel(upJanela);
		lblEstado.addMouseListener(this);
		panelFunc.add(lblEstado, "cell 0 0,alignx center");
		
		lblTranca = new JLabel(trancarOn);
		lblTranca.addMouseListener(this);
		panelFunc.add(lblTranca, "cell 1 0,alignx center");
		
		lblCortina = new JLabel(cortinaAbrir);
		lblCortina.addMouseListener(this);
		panelFunc.add(lblCortina, "cell 2 0,alignx center");
		
		lblAbrirfechar = new JLabel("Abrir/Fechar");
		lblAbrirfechar.setFont(fontv11);
		panelFunc.add(lblAbrirfechar, "cell 0 1,alignx center");
		
		lblNewLabel = new JLabel("Trancar/Destrancar");
		lblNewLabel.setFont(fontv11);
		panelFunc.add(lblNewLabel, "cell 1 1,alignx center");
		
		lblNewLabel_2 = new JLabel("Abrir/Fechar");
		lblNewLabel_2.setFont(fontv11);
		panelFunc.add(lblNewLabel_2, "cell 2 1,alignx center");
		
		lblNewLabel_3 = new JLabel("Janela");
		lblNewLabel_3.setFont(fontv11);
		panelFunc.add(lblNewLabel_3, "cell 0 2,alignx center");
		
		lblNewLabel_4 = new JLabel("Janela");
		lblNewLabel_4.setFont(fontv11);
		panelFunc.add(lblNewLabel_4, "cell 1 2,alignx center");
		
		lblNewLabel_5 = new JLabel("Cortina");
		lblNewLabel_5.setFont(fontv11);
		panelFunc.add(lblNewLabel_5, "cell 2 2,alignx center");
		
		lblImagem = new JLabel(janelaTrancada);
		contentPane.add(lblImagem, "cell 1 0,alignx center,growy");
		
		this.timer = new Timer(500, this);
		timer.setActionCommand("timerDisc");
		timer.start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == lblEstado){
			if(this.estado.equals("fechada") || this.estado.equals("trancada")){
				if(publicar("Estado", "aberta", true)){
					this.mudaEstado("aberta");					
				}						
			}
			else {
				if(publicar("Estado", "fechada", true)){
					this.mudaEstado("fechada");									
				}					
			}
		}
		else if (e.getSource() == lblTranca){
			if(this.estado.equals("aberta") || this.estado.equals("fechada")){
				if(publicar("Estado", "trancada", true)){
					this.mudaEstado("trancada");					
				}						
			}
			else {
				if(publicar("Estado", "fechada", true)){
					this.mudaEstado("fechada");									
				}					
			}
		}
		else if (e.getSource() == lblCortina){
			if(this.estadoCortina.equals("aberta")){
				if(publicar("EstCortina", "fechada", true)){
					this.mudaEstCortina("fechada");
				}						
			}
			else {
				if(publicar("EstCortina", "aberta", true)){
					this.mudaEstCortina("aberta");					
				}					
			}
		}
	}
	private void mudaEstado(String novo) {
		// TODO Auto-generated method stub
		if(!this.estado.equals(novo)){
			if(novo.equals("aberta")){
				this.lblImagem.setIcon(janelaAberta);			
				this.lblEstado.setIcon(downJanela);
				this.lblTranca.setIcon(trancarOff);
				this.lblCortina.setIcon(cortinaFechar);
				
				this.estado = "aberta";
				this.estadoCortina = "aberta";
			}
			else if(novo.equals("fechada")){
				
				if(this.estadoCortina.equals("aberta")){
					this.lblImagem.setIcon(janelaFechada);
				}
				else{
					this.lblImagem.setIcon(janelaCortina);
				}		
				this.lblEstado.setIcon(upJanela);
				this.lblTranca.setIcon(trancarOff);
				
				this.estado = "fechada";
			}
			else if(novo.equals("trancada")){
				
				if(this.estadoCortina.equals("aberta")){
					this.lblImagem.setIcon(janelaTrancadaB);
				}
				else{
					this.lblImagem.setIcon(janelaTrancada);
				}
				this.lblEstado.setIcon(upJanela);
				this.lblTranca.setIcon(trancarOn);				
				this.estado = "trancada";
			}
		}		
	}
	private void mudaEstCortina (String valor){
		if(!this.estadoCortina.equals(valor)){
			if(valor.equals("fechada")){
				this.lblCortina.setIcon(cortinaAbrir);
				this.estadoCortina = "fechada";
				if(this.estado.equals("trancada")) {						
					this.lblImagem.setIcon(janelaTrancada);
				}
				else if(this.estado.equals("fechada")){
					this.lblImagem.setIcon(janelaCortina);
				}
				else{						
					this.lblImagem.setIcon(janelaCortina);
					this.lblEstado.setIcon(upJanela);						
					this.estado = "fechada";
				}
			}
			else {
				this.estadoCortina = "aberta";
				this.lblCortina.setIcon(cortinaFechar);		
				if(this.estado == "fechada"){
					this.lblImagem.setIcon(janelaFechada);
				}
				else{
					this.lblImagem.setIcon(janelaTrancadaB);
				}
			}
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
		
		this.estado = "trancada";
		this.estadoCortina = "fechada";
		this.lblImagem.setIcon(janelaTrancada);
		this.lblLocal.setVisible(false);
		this.lblEstado.setIcon(upJanela);
		this.lblTranca.setIcon(trancarOn);
		this.lblCortina.setIcon(cortinaAbrir);
		this.panelFunc.setVisible(false);
		this.tfEndereco.setEnabled(true);
		this.tfPorta.setEnabled(true);
		this.btnConectar.setText("Conectar");			
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
				cliente.subscribe("SmartHome/"+mensagem+"/Janela/#",2);
				
			} catch (MqttException e) { }
		}
		else if(topic.equals("SmartHome/" + this.ambNome + "/Janela/Estado") || topic.equals("SHconf/Registro/"+this.dispNome+"/IniEsta")){
			this.mudaEstado(mensagem);
		}
		else if(topic.equals("SmartHome/" + this.ambNome + "/Janela/EstCortina") || topic.equals("SHconf/Registro/"+this.dispNome+"/IniCort")){
			this.mudaEstCortina(mensagem);
		}
		else if((topic.equals("SmartHome/"+this.ambNome) || topic.equals("SmartHome/" + this.ambNome + "/Janela"))  &&  mensagem.equals("Desconectar")){
			this.descMonitor = true;
		}
	}
	public boolean publicar(String topico, String mensagem, Boolean cabecalho){
		try {
			if(cabecalho){
				this.cliente.publish("SmartHome/"+this.ambNome+"/Janela/"+topico,new MqttMessage(mensagem.getBytes()));
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
