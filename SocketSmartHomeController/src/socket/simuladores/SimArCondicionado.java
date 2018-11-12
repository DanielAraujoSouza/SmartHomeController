package socket.simuladores;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Random;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import socket.servidor.Mensagem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Font;
import javax.swing.JSeparator;
import java.awt.Component;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class SimArCondicionado extends JFrame implements MouseListener, ActionListener, ISimulador{

	private static final long serialVersionUID = -7804665814195282594L;
	private String estado;
	private String dispNome;
	private Socket central;
	private String modo;
	private int temperatura;
	private final ImageIcon arCondOFF = new ImageIcon("src/socket/imagens/splitOff.png");
	private final ImageIcon arCondON = new ImageIcon("src/socket/imagens/splitOn.png");
	private final ImageIcon autoOFF = new ImageIcon("src/socket/imagens/autoOFF.png");
	private final ImageIcon autoON = new ImageIcon("src/socket/imagens/autoON.png");
	private final ImageIcon refrigOFF = new ImageIcon("src/socket/imagens/refrigOFF.png");
	private final ImageIcon refrigON = new ImageIcon("src/socket/imagens/refrigON.png");
	private final ImageIcon umidOFF = new ImageIcon("src/socket/imagens/umidOFF.png");
	private final ImageIcon umidON = new ImageIcon("src/socket/imagens/umidON.png");
	private final ImageIcon ventilarOFF = new ImageIcon("src/socket/imagens/ventilarOFF.png");
	private final ImageIcon ventilarON = new ImageIcon("src/socket/imagens/ventilarON.png");
	private final ImageIcon btnOn = new ImageIcon("src/socket/imagens/estado_ligado.png");
	private final ImageIcon btnOff = new ImageIcon("src/socket/imagens/estado_desligado.png");
	private final ImageIcon upTemp = new ImageIcon("src/socket/imagens/up.png");
	private final ImageIcon downTemp = new ImageIcon("src/socket/imagens/down.png");
	private final DecimalFormat df = new DecimalFormat("0.00");
	private JPanel contentPane;
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JLabel lblImagem;
	private JLabel btnLigar;
	private JButton btnConectar;
	private JLabel lblLocal;
	private JPanel panelCentral;
	private JPanel panelFunc;
	private JPanel panel_1;
	private JLabel lblAuto;
	private JLabel lblRefrig;
	private JLabel lblDesum;
	private JLabel lblVent;
	private JLabel lblAutomtico;
	private JLabel lblRefrigerao;
	private JLabel lblDesumidificao;
	private JLabel lblVentilao;
	private Font fontv11;
	private Font fontv9;
	private JLabel lblModo;
	private JPanel panel_2;
	private JPanel panelModo;
	private JSeparator separator;
	private JSeparator separator_1;
	private JLabel lblTemperatura;
	private JPanel panelTemp;
	private JLabel lblDown;
	private JLabel lblTemp;
	private JLabel lblUp;
	private Timer timer;
	private double tempReal;
	private JLabel lblTempReal;
	private JSeparator separator_2;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final SimArCondicionado frame = new SimArCondicionado();
					frame.setResizable(false);
					frame.setVisible(true);
					frame.setTitle("Simulação - Ar-Condicionado");
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

	public SimArCondicionado() {
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "arCond_" + n;
		this.estado = "off";
		this.modo = "auto";
		this.temperatura = 25;
		timer = new Timer(10000, this);
		timer.setActionCommand("timerTemp");
		
		setBounds(100, 100, 691, 450);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[350,fill][300px,grow,center]", "[100%,grow]"));
		this.fontv11 = new Font("Verdana", Font.PLAIN, 11);
		this.fontv9 = new Font("Verdana", Font.PLAIN, 9);
		
		
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
		lblLocal.setFont(fontv9);
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
		panelFunc.setLayout(new MigLayout("", "[100%,grow]", "[][][][]"));
		
		btnLigar = new JLabel(btnOff);	
		btnLigar.addMouseListener(this);
		panelFunc.add(btnLigar, "cell 0 0");
		
		separator_1 = new JSeparator();
		separator_1.setForeground(Color.LIGHT_GRAY);
		panelFunc.add(separator_1, "cell 0 1,grow");
		
		lblTemperatura = new JLabel("Temperatura");
		lblTemperatura.setVisible(false);
		lblTemperatura.setFont(fontv11);
		panelFunc.add(lblTemperatura, "cell 0 2");
		
		panelTemp = new JPanel();
		panelTemp.setVisible(false);
		panelFunc.add(panelTemp, "cell 0 3,grow");
		panelTemp.setLayout(new MigLayout("", "[30%][40%][30%]", "[]"));
		
		lblDown = new JLabel(downTemp);
		lblDown.addMouseListener(this);
		panelTemp.add(lblDown, "cell 0 0,alignx right");
		
		lblTemp = new JLabel("25 °C");
		lblTemp.setForeground(new Color(77,129,204));
		lblTemp.setFont(new Font("Verdana", Font.PLAIN, 28));
		panelTemp.add(lblTemp, "cell 1 0,alignx center");
		
		lblUp = new JLabel(upTemp);
		lblUp.addMouseListener(this);
		panelTemp.add(lblUp, "cell 2 0,alignx left");
		
		
		panel_2 = new JPanel();
		contentPane.add(panel_2, "cell 1 0,grow");
		panel_2.setLayout(new MigLayout("", "[grow]", "[220][][grow]"));
		
		lblImagem = new JLabel(arCondOFF);
		lblImagem.setAlignmentY(Component.TOP_ALIGNMENT);
		panel_2.add(lblImagem, "cell 0 0,alignx center,growy");
		
		separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		panel_2.add(separator, "cell 0 1,grow");
		
		panelModo = new JPanel();
		panelModo.setVisible(false);
		panel_2.add(panelModo, "cell 0 2,grow");
		panelModo.setLayout(new MigLayout("", "[]", "[][][]"));
		
		
		panel_1 = new JPanel();
		panelModo.add(panel_1, "cell 0 0,grow");
		panel_1.setLayout(new MigLayout("", "[25%][25%][25%][25%]", "[][][]"));
		
		lblModo = new JLabel("Modo");
		lblModo.setFont(fontv11);
		panel_1.add(lblModo, "cell 0 0");
		
		lblAuto = new JLabel(autoON);
		lblAuto.addMouseListener(this);
		panel_1.add(lblAuto, "cell 0 1,alignx center");
		
		lblRefrig = new JLabel(refrigOFF);
		lblRefrig.addMouseListener(this);
		panel_1.add(lblRefrig, "cell 1 1,alignx center");
		
		lblDesum = new JLabel(umidOFF);
		lblDesum.addMouseListener(this);
		panel_1.add(lblDesum, "cell 2 1,alignx center");
		
		lblVent = new JLabel(ventilarOFF);
		lblVent.addMouseListener(this);
		panel_1.add(lblVent, "cell 3 1,alignx center");
		
		lblAutomtico = new JLabel("Autom\u00E1tico");
		lblAutomtico.setFont(fontv9);
		panel_1.add(lblAutomtico, "cell 0 2,alignx center");
		
		lblRefrigerao = new JLabel("Refrigera\u00E7\u00E3o");
		lblRefrigerao.setFont(fontv9);
		panel_1.add(lblRefrigerao, "cell 1 2,alignx center");
		
		lblDesumidificao = new JLabel("Desumidifica\u00E7\u00E3o");
		lblDesumidificao.setFont(fontv9);
		panel_1.add(lblDesumidificao, "cell 2 2,alignx center");
		
		lblVentilao = new JLabel("Ventila\u00E7\u00E3o");
		lblVentilao.setFont(fontv9);
		panel_1.add(lblVentilao, "cell 3 2,alignx center");
		
		separator_2 = new JSeparator();
		separator_2.setForeground(Color.LIGHT_GRAY);
		panelModo.add(separator_2, "cell 0 1,growx");
		
		lblTempReal = new JLabel("Termômetro: ");
		lblTempReal.setFont(fontv11);
		panelModo.add(lblTempReal, "cell 0 2");
		
	}
	@Override
	public Socket getCentral() {
		// TODO Auto-generated method stub
		return this.central;
	}
	
	private void mudaEstado(String estado){
		if(estado.equals("off")){
			this.estado = "off";
			this.btnLigar.setIcon(btnOff);
			this.lblImagem.setIcon(arCondOFF);
			this.panelModo.setVisible(false);
			this.lblTemperatura.setVisible(false);
			this.panelTemp.setVisible(false);
			this.timer.stop();
		}
		else {
			this.estado = "on";
			this.btnLigar.setIcon(btnOn);
			this.lblImagem.setIcon(arCondON);
			this.panelModo.setVisible(true);
			this.lblTemperatura.setVisible(true);
			this.panelTemp.setVisible(true);
			
			this.timer.restart();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
		if(e.getSource() == btnLigar){
			if(this.estado == "on"){
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
		else if(e.getSource() == lblUp){
			int ntemp = this.temperatura+1;
			if(ntemp <= 30){
				if(enviaMsg("temp", Integer.toString(ntemp), true)){
					this.temperatura = ntemp;
					this.lblTemp.setText(Integer.toString(ntemp) + " °C");
				}
			}
		} 
		else if(e.getSource() == lblDown){
			int ntemp = this.temperatura-1;
			if(ntemp >= 10){
				if(enviaMsg("temp", Integer.toString(ntemp), true)){
					this.temperatura = ntemp;
					this.lblTemp.setText(Integer.toString(ntemp) + " °C");
				}
			}
		} 
		else if(e.getSource() == lblAuto){
			if (!this.modo.equals("auto")){
				if(enviaMsg("modo", "auto", true)){
					this.mudaModo("auto");
				}
			}
		} 
		else if(e.getSource() == lblRefrig){
			if (!this.modo.equals("refrig")){
				if(enviaMsg("modo", "refrig", true)){
					this.mudaModo("refrig");
				}
			}
		} 
		else if(e.getSource() == lblDesum){
			if (!this.modo.equals("desum")){
				if(enviaMsg("modo", "desum", true)){
					this.mudaModo("desum");
				}
			}
		} 
		else if(e.getSource() == lblVent){
			if (!this.modo.equals("vent")){
				if(enviaMsg("modo", "vent", true)){
					this.mudaModo("vent");
				}
			}
		}
	}
	private void mudaModo (String modo){
		this.modo = modo;
		if (modo.equals("auto")){
			this.lblAuto.setIcon(autoON);
			this.lblRefrig.setIcon(refrigOFF);
			this.lblDesum.setIcon(umidOFF);
			this.lblVent.setIcon(ventilarOFF);
		} else if (modo.equals("refrig")){
			this.lblAuto.setIcon(autoOFF);
			this.lblRefrig.setIcon(refrigON);
			this.lblDesum.setIcon(umidOFF);
			this.lblVent.setIcon(ventilarOFF);
		} else if (modo.equals("desum")){
			this.lblAuto.setIcon(autoOFF);
			this.lblRefrig.setIcon(refrigOFF);
			this.lblDesum.setIcon(umidON);
			this.lblVent.setIcon(ventilarOFF);
		} else if (modo.equals("vent")){
			this.lblAuto.setIcon(autoOFF);
			this.lblRefrig.setIcon(refrigOFF);
			this.lblDesum.setIcon(umidOFF);
			this.lblVent.setIcon(ventilarON);
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
		else if(e.getSource() == btnConectar && btnConectar.getText().equals("Desconectar")){
			this.enviaMsg("desconectar", null, false);
			this.desconectarEqp();
		}
		else if(this.panelModo.isVisible() && e.getActionCommand().equals("timerTemp")){
			Random rd = new Random();
			this.tempReal = this.temperatura + rd.nextGaussian();
			this.lblTempReal.setText("Termômetro: " + this.df.format(this.tempReal) + " °C");
			this.enviaMsg("tempReal", this.df.format(this.tempReal), true);
		}
	}

	@Override
	public void recebeMsg(Mensagem msg) {
		// TODO Auto-generated method stub
		if(msg.getTipo().equals("estado")){
			if(msg.getValor().equals("off")){
				this.mudaEstado("off");	
			}
			else{
				this.mudaEstado("on");
			}
		} 
		else if(msg.getTipo().equals("desconectar")){
			this.desconectarEqp();
		}
		else if(msg.getTipo().equals("modo")){
			if (!this.modo.equals(msg.getTipo())){
				this.mudaModo(msg.getValor());
			}
		}
		else if(msg.getTipo().equals("temp")){
			if (this.temperatura != Integer.parseInt(msg.getValor())){
				this.lblTemp.setText(msg.getValor() + " °C");
				this.temperatura = Integer.parseInt(msg.getValor());
			}			
		}
	}

	@Override
	public void desconectarEqp() {
		// TODO Auto-generated method stub
		try {
			this.central.close();
		} catch (IOException | NullPointerException e) {}
		this.central = null;
		this.timer.stop();
		
		this.estado = "off";
		this.lblLocal.setVisible(false);
		this.mudaModo("auto");
		this.temperatura = 25;
		this.lblTemp.setText("25 °C");
		this.btnLigar.setIcon(btnOff);
		this.lblImagem.setIcon(arCondOFF);
		this.panelModo.setVisible(false);
		this.lblTemperatura.setVisible(false);
		this.panelTemp.setVisible(false);
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
