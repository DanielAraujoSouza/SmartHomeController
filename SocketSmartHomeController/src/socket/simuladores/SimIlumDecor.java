package socket.simuladores;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import java.awt.image.BufferedImage;
import java.awt.Font;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class SimIlumDecor extends JFrame implements ChangeListener, MouseListener, ActionListener, ISimulador{

	private static final long serialVersionUID = -308387519874440288L;
	private String estado;
	private String dispNome;
	private Socket central;
	private ImageIcon ilumicaoOff = new ImageIcon("src/socket/imagens/ilumDecor_off.png");
	private ImageIcon ilumicaoVrd = new ImageIcon("src/socket/imagens/ilumDecor_vrd.png");
	private ImageIcon ilumicaoAmr = new ImageIcon("src/socket/imagens/ilumDecor_amr.png");
	private ImageIcon ilumicaoVrm = new ImageIcon("src/socket/imagens/ilumDecor_vrm.png");
	private ImageIcon ilumicaoAzl = new ImageIcon("src/socket/imagens/ilumDecor_azl.png");
	private ImageIcon ilumicaoRos = new ImageIcon("src/socket/imagens/ilumDecor_ros.png");
	private ImageIcon btnOn = new ImageIcon("src/socket/imagens/estado_ligada.png");
	private ImageIcon btnOff = new ImageIcon("src/socket/imagens/estado_desligada.png");
	private JPanel contentPane;
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JSlider tonalidade;
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

	public SimIlumDecor() {
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "ilumDecor_" + n;
		this.estado = "off";
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
		panelCentral.setBorder(new TitledBorder(null, "SmartHome Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		tfPorta.setText("12345");
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
		
		tonalidade = new JSlider(JSlider.HORIZONTAL,1,5,1);
		tonalidade.setValue(5);
		tonalidade.setEnabled(false);
		tonalidade.addChangeListener(this);
		tonalidade.setMajorTickSpacing(1);
		tonalidade.setPaintTicks(true);
		Hashtable<Integer, JLabel> lbltable = new Hashtable<Integer, JLabel>();
		lbltable.put(new Integer(1), new JLabel(criarIconeColorido(0,255,0)));
		lbltable.put(new Integer(2), new JLabel(criarIconeColorido(255,255,0)));
		lbltable.put(new Integer(3), new JLabel(criarIconeColorido(152,60,25)));
		lbltable.put(new Integer(4), new JLabel(criarIconeColorido(77,129,204)));
		lbltable.put(new Integer(5), new JLabel(criarIconeColorido(153,56,103)));
		tonalidade.setLabelTable(lbltable);
		tonalidade.setPaintLabels(true);
		tonalidade.setSnapToTicks(true);
		panelFunc.add(tonalidade, "cell 0 2,grow");
		lblImagem = new JLabel(ilumicaoOff);
		contentPane.add(lblImagem, "cell 1 0,alignx center,growy");
	}
	@Override
	public Socket getCentral() {
		// TODO Auto-generated method stub
		return this.central;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == tonalidade){
			if(tonalidade.getValue() == 1){
				lblImagem.setIcon(ilumicaoVrd);
			}
			else if(tonalidade.getValue() == 2){
				lblImagem.setIcon(ilumicaoAmr);
			}
			else if(tonalidade.getValue() == 3){
				lblImagem.setIcon(ilumicaoVrm);
			}
			else if(tonalidade.getValue() == 4){
				lblImagem.setIcon(ilumicaoAzl);
			}
			else if(tonalidade.getValue() == 5){
				lblImagem.setIcon(ilumicaoRos);
			}
			if(this.central != null){
				enviaMsg("tonalidade", Integer.toString(tonalidade.getValue()), true);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub		
		if(e.getSource() == btnLigar){
			if(this.estado == "on"){
				if(enviaMsg("estado", "off", true)){
					this.estado = "off";
					this.btnLigar.setIcon(btnOff);
					this.tonalidade.setEnabled(false);
					this.lblImagem.setIcon(ilumicaoOff);
				}			
			}
			else {
				if(enviaMsg("estado", "on", true)){
					this.estado = "on";
					this.btnLigar.setIcon(btnOn);
					this.tonalidade.setEnabled(true);
					if(tonalidade.getValue() == 1){
						lblImagem.setIcon(ilumicaoVrd);
					}
					else if(tonalidade.getValue() == 2){
						lblImagem.setIcon(ilumicaoAmr);
					}
					else if(tonalidade.getValue() == 3){
						lblImagem.setIcon(ilumicaoVrm);
					}
					else if(tonalidade.getValue() == 4){
						lblImagem.setIcon(ilumicaoAzl);
					}
					else if(tonalidade.getValue() == 5){
						lblImagem.setIcon(ilumicaoRos);
					}
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
	}

	@Override
	public void recebeMsg(Mensagem msg) {
		// TODO Auto-generated method stub
		if(msg.getTipo().equals("estado")){
			if(msg.getValor().equals("off")){
				this.estado = "off";
				this.btnLigar.setIcon(btnOff);
				this.tonalidade.setEnabled(false);
				this.lblImagem.setIcon(ilumicaoOff);
			}
			else {
				this.estado = "on";
				this.estado = "on";
				this.btnLigar.setIcon(btnOn);
				this.tonalidade.setEnabled(true);
				if(tonalidade.getValue() == 1){
					lblImagem.setIcon(ilumicaoVrd);
				}
				else if(tonalidade.getValue() == 2){
					lblImagem.setIcon(ilumicaoAmr);
				}
				else if(tonalidade.getValue() == 3){
					lblImagem.setIcon(ilumicaoVrm);
				}
				else if(tonalidade.getValue() == 4){
					lblImagem.setIcon(ilumicaoAzl);
				}
				else if(tonalidade.getValue() == 5){
					lblImagem.setIcon(ilumicaoRos);
				}
			}
		} else if(msg.getTipo().equals("desconectar")){
			this.desconectarEqp();
		}
		else if(msg.getTipo().equals("tonalidade") && tonalidade.getValue() != Integer.parseInt(msg.getValor())){
			this.tonalidade.setValue(Integer.parseInt(msg.getValor()));
		}
	}

	@Override
	public void desconectarEqp() {
		try {
			central.close();
		} catch (IOException e) {}
		central = null;
		// TODO Auto-generated method stub
		this.estado = "off";
		this.tonalidade.setValue(5);
		this.lblLocal.setVisible(false);
		this.btnLigar.setIcon(btnOff);
		this.tonalidade.setEnabled(false);
		this.lblImagem.setIcon(ilumicaoOff);
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
