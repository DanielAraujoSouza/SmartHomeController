package socket.simuladores;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import socket.servidor.Mensagem;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class SimIrrigacao extends JFrame implements MouseListener, ActionListener, ISimulador{

	private static final long serialVersionUID = 8051303800895447344L;
	private String estado;
	private String modo;
	private int nivelUmidade;
	private String dispNome;
	private Socket central;
	private final ImageIcon irrigacaoOn = new ImageIcon("src/socket/imagens/irrigacaoOn.png");
	private final ImageIcon irrigacaoOff = new ImageIcon("src/socket/imagens/irrigacaoOff.png");
	private final ImageIcon UmidadeSeco = new ImageIcon("src/socket/imagens/UmidadeSeco.png");	
	private final ImageIcon UmidadeUmido = new ImageIcon("src/socket/imagens/UmidadeUmido.png");
	private final ImageIcon UmidadeMolhado = new ImageIcon("src/socket/imagens/UmidadeMolhado.png");
	private final ImageIcon UmidadeEnxarcado = new ImageIcon("src/socket/imagens/UmidadeEnxarcado.png");
	private final ImageIcon irrigacaoAuto = new ImageIcon("src/socket/imagens/irrigacaoAuto.png");
	private final ImageIcon irrigacaoAutoOff = new ImageIcon("src/socket/imagens/irrigacaoAutoOff.png");
	private final ImageIcon btnOn = new ImageIcon("src/socket/imagens/estado_ligada.png");
	private final ImageIcon btnOff = new ImageIcon("src/socket/imagens/estado_desligada.png");
	
	private JPanel contentPane;
	private Font fontv11;
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JLabel lblImagem;
	private JButton btnConectar;
	private JPanel panelCentral;
	private JPanel panelFunc;
	private JLabel lblLocal;
	private JPanel panelInfo;
	private JLabel btnLigar;
	private JLabel lblNivelUmidade;
	private JLabel lblValorUmidade;
	private JLabel btnMode;
	private JSeparator separator;
	private JLabel label;
	private JLabel lblAutomtico;
	private Timer timer;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SimIrrigacao frame = new SimIrrigacao();
					frame.setVisible(true);
					frame.setResizable(false);
					frame.setVisible(true);
					frame.setTitle("Simulação - Irrigação de Jardim");
					frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					frame.addWindowListener(new WindowListener() {						
						@Override
						public void windowClosing(WindowEvent arg0) {
							// TODO Auto-generated method stub
							if(JOptionPane.showConfirmDialog(null, "Deseja Fechar?")==JOptionPane.OK_OPTION){		
								frame.enviaMsg("desconectar", null, false);
								System.exit(0);
							}							
						}

						@Override
						public void windowActivated(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void windowClosed(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowDeactivated(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowDeiconified(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowIconified(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void windowOpened(WindowEvent arg0) {
							// TODO Auto-generated method stub
							
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SimIrrigacao() {
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "irrigJrdm_" + n;
		this.estado = "off";
		this.modo = "manual";
		this.nivelUmidade = 60;
		timer = new Timer(5000, this);
		timer.setActionCommand("timerUmid");
		
		setBounds(100, 100, 691, 458);
		contentPane = new JPanel();		
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[350,fill][300px,grow,center]", "[300px:n]"));
		fontv11 = new Font("Verdana", Font.PLAIN, 11);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panel, "cell 0 0,grow");
		panel.setLayout(new MigLayout("", "[100%,grow]", "[][][50%,grow]"));
		
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
		panelCentral.setBorder(new TitledBorder(null, "SmartHome Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		tfPorta.setText("12345");
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
		panelFunc.setLayout(new MigLayout("", "[100%,grow]", "[][][grow]"));
		
		btnLigar = new JLabel(btnOff);
		btnLigar.addMouseListener(this);
		panelFunc.add(btnLigar, "cell 0 0");
		
		separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		panelFunc.add(separator, "cell 0 1,growx");
		
		panelInfo = new JPanel();
		panelFunc.add(panelInfo, "cell 0 2,grow");
		panelInfo.setLayout(new MigLayout("", "[50px:50px][100%][70px:70px]", "[][]"));
		
		lblNivelUmidade = new JLabel(UmidadeMolhado);
		panelInfo.add(lblNivelUmidade, "cell 0 0,alignx left");
		
		lblValorUmidade = new JLabel(Integer.toString(this.nivelUmidade)+"%");
		lblValorUmidade.setForeground(new Color(77,129,204));
		lblValorUmidade.setFont(new Font("Verdana", Font.PLAIN, 28));
		panelInfo.add(lblValorUmidade, "flowy,cell 1 0,alignx left");
		
		btnMode = new JLabel(irrigacaoAutoOff);
		btnMode.addMouseListener(this);
		panelInfo.add(btnMode, "cell 2 0,alignx center");
		
		label = new JLabel("Umidade do Solo");
		label.setFont(fontv11);
		panelInfo.add(label, "cell 1 1");
		
		lblAutomtico = new JLabel("Autom\u00E1tico");
		lblAutomtico.setFont(fontv11);
		panelInfo.add(lblAutomtico, "cell 2 1");
		
		lblImagem = new JLabel(irrigacaoOff);
		contentPane.add(lblImagem, "cell 1 0,alignx center,growy");
	}
	
	@Override
	public Socket getCentral() {
		// TODO Auto-generated method stub
		return this.central;
	}
	
	@Override
	public void recebeMsg(Mensagem msg) {
		// TODO Auto-generated method stub
		if(msg.getTipo().equals("estado")){
			this.mudaEstado(msg.getValor());
		}
		if(msg.getTipo().equals("modoIr")){
			this.mudaModo(msg.getValor());
		}
		else if(msg.getTipo().equals("desconectar")){
			this.desconectarEqp();
		}
	}

	@Override
	public void desconectarEqp() {
		// TODO Auto-generated method stub
		try {
			this.central.close();
		} catch (IOException e) {}
		this.central = null;
		timer.stop();
		
		this.estado = "fechada";
		this.modo = "manual";
		this.btnMode.setIcon(irrigacaoAutoOff);
		this.lblImagem.setIcon(this.irrigacaoOff);
		this.lblLocal.setVisible(false);
		this.btnLigar.setIcon(this.btnOff);
		this.panelFunc.setVisible(false);
		this.tfEndereco.setEnabled(true);
		this.tfPorta.setEnabled(true);
		this.btnConectar.setText("Conectar");			
	}

	@Override
	public Boolean enviaMsg(String tipo, String valor, Boolean infoErro){
		try{
			ObjectOutputStream envia = new ObjectOutputStream(central.getOutputStream());
			envia.flush();
			envia.writeObject(new Mensagem(tipo, valor));
			
			return true;			
		}catch(IOException | NullPointerException e1){
			if(infoErro){
				JOptionPane.showMessageDialog(this,  "Erro de Comunicação com a Central!", "Erro", JOptionPane.ERROR_MESSAGE);
				this.desconectarEqp();
			}
			return false;
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
				try{
					int porta = Integer.parseInt(this.tfPorta.getText());
					this.central = new Socket(this.tfEndereco.getText(), porta);
					ObjectOutputStream envia = new ObjectOutputStream(central.getOutputStream());
					envia.flush();
					envia.writeObject(new Mensagem("registrar", this.dispNome));					
					ObjectInputStream recebe = new ObjectInputStream(central.getInputStream());
					Mensagem msg = (Mensagem)recebe.readObject();
					
					if(msg.getTipo().equals("ERRO")){
						JOptionPane.showMessageDialog(this,  "ERRO\n" + msg.getValor(), "Erro", JOptionPane.ERROR_MESSAGE);
						envia.close();
						recebe.close();
						central.close();
						central = null;
					}
					else if(msg.getTipo().equals("registrado")){
						this.lblLocal.setText("Ambiente: " + msg.getValor());
						this.lblLocal.setVisible(true);
						this.btnConectar.setText("Desconectar");
						this.tfEndereco.setEnabled(false);
						this.tfPorta.setEnabled(false);
						this.panelFunc.setVisible(true);
						new Thread(new MonitorMsg(this)).start();
						this.timer.start();
					}
					
				}catch(NumberFormatException e1){
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "Digite um Número Válido!", "Erro", JOptionPane.ERROR_MESSAGE);
					tfPorta.setText("");
				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(this, "Erro na Conexão com SmartHome!", "Erro", JOptionPane.ERROR_MESSAGE);
					this.tfEndereco.setText("");
					this.tfPorta.setText("");
				}
			}
		}
		else if(e.getActionCommand().equals("timerUmid")){
			//Simula perda de umidade do solo
			int novoUmd = this.nivelUmidade;
			if(this.nivelUmidade > 0){
				novoUmd--;
			}
			
			//Controle do modo automatico
			if(this.modo.equals("auto")){
				if(this.nivelUmidade <= 20){
					this.mudaEstado("on");
				}
				else if(this.nivelUmidade >= 60){
					this.mudaEstado("off");
				}
			}			
			//Simula a irrigação	
			if(this.estado == "on" && this.nivelUmidade < 100){
				novoUmd += 5;
			}
			if(this.nivelUmidade != novoUmd){
				//envia valor da umidade a central
				this.enviaMsg("umidReal", Integer.toString(novoUmd), true);
				//Atualiza na tela
				this.mudaUmidade(novoUmd);
			}			
		}
		else if(e.getSource() == btnConectar && btnConectar.getText().equals("Desconectar")){
			this.enviaMsg("desconectar", null, false);
			this.desconectarEqp();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == btnLigar){
			if(this.estado.equals("on")){
				// Se ja estiver ligado desliga
				if(enviaMsg("estado", "off", true)){
					this.mudaEstado("off");					
				}						
			}
			else {
				if(enviaMsg("estado", "on", true)){
					this.mudaEstado("on");									
				}					
			}
		}
		else if(e.getSource() == btnMode){
			if(this.modo.equals("manual")){
				if(enviaMsg("modoIr", "auto", true)){
					this.mudaModo("auto");					
				}
			}
			else {
				if(enviaMsg("modoIr", "manual", true)){
					this.mudaModo("manual");					
				}
			}
		}
	}
	private void mudaEstado(String novo) {
		// TODO Auto-generated method stub
		if(novo.equals("on")){
			this.lblImagem.setIcon(irrigacaoOn);			
			this.btnLigar.setIcon(btnOn);
			
			this.estado = "on";
		}
		else if(novo.equals("off")){
			this.lblImagem.setIcon(irrigacaoOff);
			this.btnLigar.setIcon(btnOff);
			
			this.estado = "off";
		}
	}
	
	private void mudaModo(String novo) {
		// TODO Auto-generated method stub
		if(novo.equals("auto")){
			this.btnMode.setIcon(irrigacaoAuto);
			this.modo = "auto";
		}
		else if(novo.equals("manual")){
			this.btnMode.setIcon(irrigacaoAutoOff);			
			this.modo = "manual";
		}
	}
	
	private void mudaUmidade (int valor){
		this.nivelUmidade = valor;
		this.lblValorUmidade.setText(Integer.toString(valor)+"%");
		if(valor <= 20){
			this.lblNivelUmidade.setIcon(UmidadeSeco);
		}
		else if (valor <= 40){
			this.lblNivelUmidade.setIcon(UmidadeUmido);
		}
		else if (valor <= 60){
			this.lblNivelUmidade.setIcon(UmidadeMolhado);
		}
		else{
			this.lblNivelUmidade.setIcon(UmidadeEnxarcado);
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
