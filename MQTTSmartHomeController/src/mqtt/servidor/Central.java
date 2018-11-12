package mqtt.servidor;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import mqtt.servidor.ambiente.paineis.IPanelAmb;
import mqtt.servidor.ambiente.paineis.PanelArCond;
import mqtt.servidor.ambiente.paineis.PanelIlumComum;
import mqtt.servidor.ambiente.paineis.PanelIlumDecor;
import mqtt.servidor.ambiente.paineis.PanelIrrigacao;
import mqtt.servidor.ambiente.paineis.PanelJanela;
import mqtt.servidor.ambiente.paineis.PanelPorta;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import java.awt.Color;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.regex.Pattern;
import java.awt.Font;

import javax.swing.border.LineBorder;
import javax.swing.UIManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * Controlador de Casa Inteligente - MQTT
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-05
 */

public class Central extends JFrame implements MouseListener, ActionListener, MqttCallback {
	
	private static final long serialVersionUID = -7100231224185129829L;	
	private final ImageIcon addIcon = new ImageIcon("src/mqtt/imagens/new.png");
	private final ImageIcon relogioIcon = new ImageIcon("src/mqtt/imagens/clock.png");
	private final ImageIcon conective = new ImageIcon("src/mqtt/imagens/conective.png");
	private final DateFormat dfCalendario = new  SimpleDateFormat("EEEE', 'd' de 'MMMM' de 'YYYY");
	private final DateFormat dfRelogio = new  SimpleDateFormat("HH:mm");
	private final String brokerHost = "localhost";
	private final String brokerPort = "1883";
	private MqttClient cliente;
	private Vector<Ambiente> lstAmbiente;
	private Timer timer;
	
	private JLabel btnAdd;
	private JPanel contentPane;
	private JPanel panelEsquerda;
	private JPanel panelDireita;
	private JPanel panelCentral;
	private JPanel panel;
	private JLabel lblIp;
	private JLabel lblPorta;
	private JPanel panel_2;
	private JLabel label;
	private JLabel lblCalendario;
	private JLabel lblRelogio;
	private JLabel lblNewLabel;	

	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Central frame = new Central();
					frame.setVisible(true);
					frame.setSize(1000,500);
					frame.setTitle("Central - SmartHome");					
					frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					frame.addWindowListener(new WindowListener() {						
						@Override
						public void windowClosing(WindowEvent arg0) {
							// TODO Auto-generated method stub
							if(JOptionPane.showConfirmDialog(null, "Deseja Fechar Central?")==JOptionPane.OK_OPTION){		
								frame.desconectarCentral();
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
					//Conecta com o Broker
					frame.BrokerConnect();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		});
	}

	public Central() {
		
		setBackground(Color.WHITE);
		lstAmbiente = new Vector<Ambiente> ();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 803, 480);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[100%,grow]", "[50px][grow]"));
		
		JPanel panelSupMenu = new JPanel();
		contentPane.add(panelSupMenu, "cell 0 0,grow");
		panelSupMenu.setLayout(new MigLayout("", "[][20%,grow][][20%,grow][20%][20%]", "[100%,grow]"));
		
		btnAdd = new JLabel(addIcon);
		btnAdd.addMouseListener(this);
		
		label = new JLabel(this.relogioIcon);
		panelSupMenu.add(label, "cell 0 0");
		
		panel_2 = new JPanel();
		panelSupMenu.add(panel_2, "cell 1 0,grow");
		panel_2.setLayout(new MigLayout("", "[left]", "[][]"));
		
		lblCalendario = new JLabel(dfCalendario.format(Calendar.getInstance().getTime()));
		lblCalendario.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel_2.add(lblCalendario, "cell 0 0");
		
		lblRelogio = new JLabel(dfRelogio.format(Calendar.getInstance().getTime()));
		lblRelogio.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel_2.add(lblRelogio, "cell 0 1");
		
		lblNewLabel = new JLabel(this.conective);
		panelSupMenu.add(lblNewLabel, "cell 2 0");
		
		panel = new JPanel();
		panelSupMenu.add(panel, "cell 3 0,grow");
		panel.setLayout(new MigLayout("", "[]", "[][]"));
		
		lblIp = new JLabel("IP: " + this.brokerHost);
		lblIp.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(lblIp, "cell 0 0");
		
		lblPorta = new JLabel("Porta: "  + this.brokerPort);
		lblPorta.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(lblPorta, "cell 0 1");
		panelSupMenu.add(btnAdd, "cell 5 0,alignx right,growy");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new LineBorder(UIManager.getColor("Button.background")));
		contentPane.add(scrollPane, "cell 0 1,grow");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		scrollPane.setViewportView(panel_1);
		panel_1.setLayout(new MigLayout("", "[300,grow][300,grow][300,grow]", "[grow]"));
		
		panelEsquerda = new JPanel();
		panelEsquerda.setBackground(Color.WHITE);
		panel_1.add(panelEsquerda, "cell 0 0,growx,aligny top");
		panelEsquerda.setLayout(new MigLayout("", "[100%]", "[]"));
		
		panelCentral = new JPanel();
		panelCentral.setBackground(Color.WHITE);
		panel_1.add(panelCentral, "cell 1 0,growx,aligny top");
		panelCentral.setLayout(new MigLayout("", "[100%]", "[]"));
		
		panelDireita = new JPanel();
		panelDireita.setBackground(Color.WHITE);
		panel_1.add(panelDireita, "cell 2 0,growx,aligny top");
		panelDireita.setLayout(new MigLayout("", "[100%]", "[]"));
		
		this.timer = new Timer(60000, this);
		timer.start();
	}
	public void BrokerConnect(){
		try {
			//Cria o cliente Mqtt
			this.cliente = new MqttClient("tcp://"+brokerHost+":"+brokerPort,"SmartHome",new MemoryPersistence());
			//Configura as opçoes de cenexão 
			MqttConnectOptions connOpts = new MqttConnectOptions();
			//Mensagem de disconexao
			connOpts.setWill("SHconf/Desconectar","SmartHome".getBytes(),2,true); // mantem a mensagem para outro verem
			//Especifica se a conexão é persistente ou não
			connOpts.setCleanSession(false);
			// Configura chamadas de retorno 
			this.cliente.setCallback(this);
			//Conecta o cliente ao Broker
			cliente.connect(connOpts);
			//Increve-se em todos os topicos dos ambientes
			cliente.subscribe("SmartHome/#",2);
			//Increve-se em todos os topicos de configuração
			cliente.subscribe("SHconf/#",2);
			
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this,  "Não foi possivel conectar-se ao Broker!", "Erro", JOptionPane.ERROR_MESSAGE);
			this.dispose();
		}
	}
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnAdd){
			String ambNome = JOptionPane.showInputDialog(this, "Digite o Nome do Ambiente");
			if (ambNome != null){
				if(!ambNome.matches("[^\\s][\\w|\\s]*[^\\s]")){
					JOptionPane.showMessageDialog(this,  "Nome de Ambiente invalido!\nUtilize apenas letras e números", "Erro", JOptionPane.WARNING_MESSAGE);
				}
				else if(!nameIsOk(ambNome.toUpperCase())){
					JOptionPane.showMessageDialog(this,  "Já Existe um Ambiente com Este Nome!", "Erro", JOptionPane.WARNING_MESSAGE);
				}
				else{
					Ambiente amb = new Ambiente(ambNome.toUpperCase(), this);
					lstAmbiente.add(amb);
					this.repaintAmb();
				}	
			}	
		}
	}
	private void repaintAmb() {
		// TODO Auto-generated method stub
		int c = 1;
		int l = 0;
		panelEsquerda.removeAll();
		panelCentral.removeAll();
		panelDireita.removeAll();
		synchronized (lstAmbiente) {
			for(Ambiente amb : lstAmbiente){
				if(c == 1){
					panelEsquerda.add(amb, "cell 0 "+Integer.toString(l) + ",growx,aligny top");
					c++;
				}
				else if(c == 2){
					panelCentral.add(amb, "cell 0 "+Integer.toString(l) + ",growx,aligny top");
					c++;
				}
				else if(c == 3){
					panelDireita.add(amb, "cell 0 "+Integer.toString(l) + ",growx,aligny top");
					c = 1;
					l++;
				}
			}
		}		
		this.repaint();
		this.revalidate();
	}
	public void removeAmb(Ambiente amb){
		this.lstAmbiente.remove(amb);
		this.repaintAmb();
	}
	
	public boolean nameIsOk (String nome){
		synchronized (lstAmbiente) {
			for(Ambiente amb : lstAmbiente){
				if (nome.equals(amb.getNome()))
				{
					return false;
				}
			}
		}		
		return true;
	}
	public Ambiente getAmbByName (String nome){
		synchronized (lstAmbiente) {
			for(Ambiente amb : lstAmbiente){
				if (nome.equals(amb.getNome()))
				{
					return amb;
				}
			}
		}		
		return null;
	}
	public Boolean escolheAmb(String eqpNome){
		if(lstAmbiente.size() > 0 ){
			String ambNome = (String)JOptionPane.showInputDialog(this, eqpNome + " Tentando se Conectar a Central.\n Escolha um Ambiente:", "Adicionar Equipamento", JOptionPane.INFORMATION_MESSAGE, null, this.nomeAmbientes(), null);
			if(ambNome != null){
				IPanelAmb panel = this.painelParaEqp(ambNome, eqpNome.split("_")[0]);
				panel.addEqp(eqpNome);
				this.publicar("SHconf/Registro/"+eqpNome, ambNome);
				panel.iniciaValores(eqpNome);
				return true;
			}			
			return false;
		}
		return false;
	}
	public String[] nomeAmbientes(){
		String[] n = new String[lstAmbiente.size()];
		int i = 0;
		synchronized (lstAmbiente) {
			for(Ambiente amb : lstAmbiente){
				n[i] = amb.getNome();
				i++;
			}
		}		
		return n;
	}
	//Retorna o painel referente ao tipo do eqp
	public IPanelAmb painelParaEqp(String ambNome, String eqpTipo){
		synchronized (lstAmbiente) {
			for(Ambiente amb : lstAmbiente){
				if(amb.getName().equals(ambNome)){
					return amb.getPanelEqp(eqpTipo);
				}
			}
		}
		return null;
	}
	public void desconectarCentral(){
		this.publicar("SHconf/Desconectar","SmartHome");
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		this.lblCalendario.setText(dfCalendario.format(Calendar.getInstance().getTime()));
		this.lblRelogio.setText(dfRelogio.format(Calendar.getInstance().getTime()));
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(this,  "Conexão com o Broker foi perdida!", "Erro", JOptionPane.ERROR_MESSAGE);
		this.dispose();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}
	public void publicar(String topico, String mensagem){
		synchronized (this.cliente) {
			try {
				this.cliente.publish(topico,new MqttMessage(mensagem.getBytes()));
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
	}
	@Override
	public void messageArrived(String topic, MqttMessage message){
		// Solicitacao do Dispositivo para Central		
		String mensagem = new String(message.getPayload());
		String[] topico = topic.split(Pattern.quote("/"));
		
		// Solicitacao de conexao a central
		if(topic.equals("SHconf/Registro")){
			if(!this.escolheAmb(mensagem)){
				// ID_EQP/Registro
				this.publicar("SHconf/Registro/"+mensagem+"/Erro", "RegErro");
			}
		}
		else if(topic.equals("SHconf/Desconectar")){
			synchronized (lstAmbiente) {
				for(Ambiente amb : lstAmbiente){
					if(amb.descEqp(mensagem)){
						break;
					}
				}
			}
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/Estado")){
			try{
				this.getAmbByName(topico[1]).getPanelByType(topico[2]).mudaEstado(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/Intensidade")){
			try{
				((PanelIlumComum)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaIntensidade(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/Tonalidade")){
			try{
				((PanelIlumDecor)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaTonalidade(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/Modo")){
			try{
				((PanelArCond)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaModo(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/Temp")){
			try{
				((PanelArCond)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaTemperatura(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/TempReal")){
			try{
				((PanelArCond)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaTempReal(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/EstCortina")){
			try{
				((PanelJanela)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaEstCortina(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/Porta/\\w*/Estado")){
			try{
				((PanelPorta)this.getAmbByName(topico[1]).getPanelByType2(topico[3])).mudaEstado(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/ModoIr")){
			try{
				((PanelIrrigacao)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaModo(mensagem);
			}catch (NullPointerException e){ }
		}
		else if (topic.matches("SmartHome/[^\\s][\\w|\\s]*[^\\s]/\\w*/UmidReal/\\w*")){
			try{
				((PanelIrrigacao)this.getAmbByName(topico[1]).getPanelByType(topico[2])).mudaUmidade(mensagem, topico[4]);
			}catch (NullPointerException e){ }
		}
	}
}
