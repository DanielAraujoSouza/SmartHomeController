package socket.simuladores;
import java.awt.EventQueue;

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
import java.awt.Font;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class SimIlumComum extends JFrame implements ChangeListener, MouseListener, ActionListener, ISimulador{

	private static final long serialVersionUID = 6294195781285642478L;
	private String estado;
	private String dispNome;	
	private Socket central;
	private final ImageIcon ilumicaoOff = new ImageIcon("src/socket/imagens/iluminacao_off.png");
	private final ImageIcon ilumicao20 = new ImageIcon("src/socket/imagens/iluminacao_20.png");
	private final ImageIcon ilumicao40 = new ImageIcon("src/socket/imagens/iluminacao_40.png");
	private final ImageIcon ilumicao60 = new ImageIcon("src/socket/imagens/iluminacao_60.png");
	private final ImageIcon ilumicao80 = new ImageIcon("src/socket/imagens/iluminacao_80.png");
	private final ImageIcon ilumicao100 = new ImageIcon("src/socket/imagens/iluminacao_100.png");
	private final ImageIcon btnOn = new ImageIcon("src/socket/imagens/estado_ligada.png");
	private final ImageIcon btnOff = new ImageIcon("src/socket/imagens/estado_desligada.png");
	private JPanel contentPane;
	private JTextField tfDispName;
	private JTextField tfEndereco;
	private JTextField tfPorta;
	private JSlider intensidade;
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

	public SimIlumComum() {
		int n;
		while ((n = new Random().nextInt()) <= 10);
		this.dispNome = "ilumComum_" + n;
		this.estado = "off";
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
		panelFunc.setLayout(new MigLayout("", "[100%]", "[][][]"));
		
		btnLigar = new JLabel(btnOff);		
		panelFunc.add(btnLigar, "cell 0 0");
		btnLigar.addMouseListener(this);
		
		JLabel lblIntensidade = new JLabel("Intensidade");
		lblIntensidade.setFont(fontv11);
		panelFunc.add(lblIntensidade, "cell 0 1");
		
		intensidade = new JSlider(JSlider.HORIZONTAL,1,5,1);
		intensidade.setFont(fontv11);
		intensidade.setValue(5);
		intensidade.setEnabled(false);
		intensidade.addChangeListener(this);
		intensidade.setMajorTickSpacing(1);
		intensidade.setPaintTicks(true);
		Hashtable<Integer, JLabel> lbltable = new Hashtable<Integer, JLabel>();
		lbltable.put(new Integer(1), new JLabel("20%"));
		lbltable.put(new Integer(2), new JLabel("40%"));
		lbltable.put(new Integer(3), new JLabel("60%"));
		lbltable.put(new Integer(4), new JLabel("80%"));
		lbltable.put(new Integer(5), new JLabel("100%"));
		intensidade.setLabelTable(lbltable);
		intensidade.setPaintLabels(true);
		intensidade.setSnapToTicks(true);
		panelFunc.add(intensidade, "cell 0 2,grow");
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
		if(e.getSource() == intensidade){
			if(intensidade.getValue() == 1){
				lblImagem.setIcon(ilumicao20);
			}
			else if(intensidade.getValue() == 2){
				lblImagem.setIcon(ilumicao40);
			}
			else if(intensidade.getValue() == 3){
				lblImagem.setIcon(ilumicao60);
			}
			else if(intensidade.getValue() == 4){
				lblImagem.setIcon(ilumicao80);
			}
			else if(intensidade.getValue() == 5){
				lblImagem.setIcon(ilumicao100);
			}
			if(this.central != null){
				enviaMsg("intensidade", Integer.toString(intensidade.getValue()), true);
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
					this.intensidade.setEnabled(false);
					this.lblImagem.setIcon(ilumicaoOff);
				}			
			}
			else {
				if(enviaMsg("estado", "on", true)){
					this.estado = "on";
					this.btnLigar.setIcon(btnOn);
					this.intensidade.setEnabled(true);
					if(intensidade.getValue() == 1){
						lblImagem.setIcon(ilumicao20);
					}
					else if(intensidade.getValue() == 2){
						lblImagem.setIcon(ilumicao40);
					}
					else if(intensidade.getValue() == 3){
						lblImagem.setIcon(ilumicao60);
					}
					else if(intensidade.getValue() == 4){
						lblImagem.setIcon(ilumicao80);
					}
					else if(intensidade.getValue() == 5){
						lblImagem.setIcon(ilumicao100);
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
		if(msg.getTipo().equals("estado") && !msg.getValor().equals(this.estado)){
			if(msg.getValor().equals("off")){
				this.estado = "off";
				this.btnLigar.setIcon(btnOff);
				this.intensidade.setEnabled(false);
				this.lblImagem.setIcon(ilumicaoOff);
			}
			else if(msg.getValor().equals("on")){
				this.estado = "on";
				this.estado = "on";
				this.btnLigar.setIcon(btnOn);
				this.intensidade.setEnabled(true);
				if(intensidade.getValue() == 1){
					lblImagem.setIcon(ilumicao20);
				}
				else if(intensidade.getValue() == 2){
					lblImagem.setIcon(ilumicao40);
				}
				else if(intensidade.getValue() == 3){
					lblImagem.setIcon(ilumicao60);
				}
				else if(intensidade.getValue() == 4){
					lblImagem.setIcon(ilumicao80);
				}
				else if(intensidade.getValue() == 5){
					lblImagem.setIcon(ilumicao100);
				}
			}
		} else if(msg.getTipo().equals("desconectar")){
			this.desconectarEqp();
		}
		else if(msg.getTipo().equals("intensidade") && intensidade.getValue() != Integer.parseInt(msg.getValor())){
			this.intensidade.setValue(Integer.parseInt(msg.getValor()));
		}
	}

	@Override
	public void desconectarEqp() {
		// TODO Auto-generated method stub
		try {
			central.close();
		} catch (IOException e) {}
		central = null;
		
		this.estado = "off";
		this.intensidade.setValue(5);
		this.lblLocal.setVisible(false);
		this.btnLigar.setIcon(btnOff);
		this.intensidade.setEnabled(false);
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
