package socket.servidor;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;

import socket.servidor.ambiente.paineis.IPanelAmb;

/**
 * Controlador de Casa Inteligente - Socket
 * @author Daniel Araújo Chaves Souza
 * @version 1.0
 * @since 2018-11-02
 */

public class Central extends JFrame implements MouseListener, ActionListener {
	private class StartServer implements Runnable{
		
		private Central central;
		private ServerSocket server;
		
		public StartServer(Central central) {
			super();
			this.central = central;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				//Inicia o servidor
				int porta = 12345;			
				server = new ServerSocket(porta);			
				String IP = server.getInetAddress().toString();
				this.central.setEndereco(IP, porta);
				
				while(true){			
					//Cria uma Thread para cada conexão com o servidor
					Socket cliente = server.accept();
					(new Thread(new Server(central, cliente))).start();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(central, "Já Existe uma Central Ativa!", "Erro", JOptionPane.ERROR_MESSAGE);
				this.central.dispose();
			}
		}	
	}

	private static final long serialVersionUID = -7100231224185129829L;
	private JPanel contentPane;
	private final ImageIcon addIcon = new ImageIcon("src/socket/imagens/new.png");
	private final ImageIcon relogioIcon = new ImageIcon("src/socket/imagens/clock.png");
	private final ImageIcon conective = new ImageIcon("src/socket/imagens/conective.png");
	private JLabel btnAdd;
	private Vector<Ambiente> lstAmbiente;
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
	private Timer timer;
	private DateFormat dfCalendario = new  SimpleDateFormat("EEEE', 'd' de 'MMMM' de 'YYYY");
	private DateFormat dfRelogio = new  SimpleDateFormat("HH:mm");

	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Central frame = new Central();
					frame.setVisible(true);
					frame.setSize(1000,500);
					frame.setTitle("Central - SmartHome");
					(new Thread(frame.new StartServer(frame))).start();
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
		
		lblIp = new JLabel("IP: ");
		lblIp.setFont(new Font("Verdana", Font.PLAIN, 11));
		panel.add(lblIp, "cell 0 0");
		
		lblPorta = new JLabel("Porta: ");
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

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnAdd){
			String ambNome = JOptionPane.showInputDialog(this, "Digite o Nome do Ambiente");
			if (ambNome != null){
				if(ambNome.equals("")){
					JOptionPane.showMessageDialog(this,  "Nome de Ambiente Vazio!", "Erro", JOptionPane.WARNING_MESSAGE);
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
		this.repaint();
		this.revalidate();
	}
	public void removeAmb(Ambiente amb){
		this.lstAmbiente.remove(amb);
		this.repaintAmb();
	}
	
	public boolean nameIsOk (String nome){
		for(Ambiente amb : lstAmbiente){
			if (nome.equals(amb.getNome()))
			{
				return false;
			}
		}
		return true;
	}
	public IPanelAmb getEqpAmb(String eqpNome){
		if(lstAmbiente.size() > 0 ){
			String ambNome = (String)JOptionPane.showInputDialog(this, eqpNome + " Tentando se Conectar a Central.\n Escolha um Ambiente:", "Adicionar Equipamento", JOptionPane.INFORMATION_MESSAGE, null, this.nomeAmbientes(), null);
			return this.painelParaEqp(ambNome, eqpNome.split("_")[0]);
		}
		return null;
	}
	public String[] nomeAmbientes(){
		String[] n = new String[lstAmbiente.size()];
		int i = 0;
		for(Ambiente amb : lstAmbiente){
			n[i] = amb.getNome();
			i++;
		}
		return n;
	}
	//Retorna o painel referente ao tipo do eqp
	public IPanelAmb painelParaEqp(String ambNome, String eqpTipo){
		for(Ambiente amb : lstAmbiente){
			if(amb.getName().equals(ambNome)){
				return amb.getPanelEqp(eqpTipo);
			}
		}
		return null;
	}
	public void setEndereco(String ip, int porta){
		this.lblIp.setText("IP: " + ip);
		this.lblPorta.setText("Porta: " + Integer.toString(porta));
	}
	public void desconectarCentral(){
		for(Ambiente amb : lstAmbiente){
			amb.removeEqps();
		}
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		this.lblCalendario.setText(dfCalendario.format(Calendar.getInstance().getTime()));
		this.lblRelogio.setText(dfRelogio.format(Calendar.getInstance().getTime()));
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
