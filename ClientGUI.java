/**
 *
 * @author Stanley Uche Godfrey
 */
package chatPkg;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class ClientGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;
	// Constructor
	ClientGUI(String host, int port) {
		super("");
		defaultPort = port;
		defaultHost = host;
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);
		// the Label and the TextField
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);
		// The CenterPanel which is the chat room
		ta = new JTextArea("", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);
		// the buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);       // you have to login before being able to logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);      // you have to login before being able to Who is in
		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(300, 400);
		setVisible(true);
		tf.requestFocus();
	}
	// called by the Client to append text in the TextArea
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	//If connection fails, reset button text fields
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("");
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tf.removeActionListener(this);
		connected = false;
	}
	//Button or JTextField action events
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new ChatMessage
					(ChatMessage.LOGOUT, "",client.getUsername(),tf.getName()));
			this.dispose();
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, "",
					client.getUsername(),tf.getText()));
			return;
		}
		// ok it is coming from the JTextField
		if(connected) {
			// forward message to server
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE,
					tf.getText()+"\n",
					client.getUsername(),client.toClient)); 
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String time = sdf.format(new Date());
			String msg=time+"\n"+"To "+client.toClient+"\n";
			append(msg+" "+tf.getText()+"\n");
			tf.setFont(tf.getFont().deriveFont(12f));
			tf.setText("");
			append("\n");
			return;
		}
		if(o == login) {
			// ok it is a connection request
			String username = tf.getText().trim();
			this.setTitle(username +" Connected");
			if(username.length() == 0){
				Label msg = new Label("Must supply username ");
				msg.setForeground(Color.red);
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			// Ensures that server ID is supplied
			String server = tfServer.getText().trim();
			if(server.length() == 0){
				Label msg = new Label("Must supply server id");
				msg.setForeground(Color.red);
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0){
				return;
			}
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   //return if port is not valid
			}
			// try creating a new Client with GUI
			client = new Client(server, port, username, this);
			// test if we can start the Client
			if(!client.start())
				return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
		}
	}
	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 1500);

	}
}
