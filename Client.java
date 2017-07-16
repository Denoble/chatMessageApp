/**
 *
 * @author Stanley Uche Godfrey
 */
package chatPkg;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
public class Client  {
	private ObjectInputStream sInput;       // to read from the socket
	private ObjectOutputStream sOutput;     // to write on the socket
	private Socket socket;
	private ClientGUI cg;
	private String server, username;
	private int port;
	public String toClient; // The other client to send message to
	// Constructor without GUI
	Client(String server, int port, String username) {
		this(server, port, username, null);
	}
	//Constructor with GUI
	Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.cg = cg;
	}
	//Connects to the server
	public boolean start() {
		try {
			socket = new Socket(server, port);
		}
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		/* Creating both Data Stream */
		try{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}
		// creates the Thread to listen to the server
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success 
		return true;

	}
	//Displays message in the GUI
	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg + "\n");      // append to the ClientGUI JTextArea (or whatever)
	}
	/*
	 *To send a message to the server forward to 
	 * a client using the client username
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
	/*
	 * When something goes wrong
	 *  Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try {
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
		try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} 
		// inform the GUI
		if(cg != null)
			cg.connectionFailed();
	}
	public static void main(String[] args) {
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Enter Your username";
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test connection to the Server
		// if it failed return
		if(!client.start())
			return;
	}
	class ListenFromServer extends Thread {
		public void run() {
			while(true) {
				try {
					Object msg =  sInput.readObject();
					// if console mode print the message and add back the prompt
					if(cg == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					if(msg instanceof String){
						cg.append((String)msg);	
						cg.append("\n");
					}
					if(msg instanceof Message) {
						Message ms=(Message)msg;
						new BuddyList(username,ms.getConnectedClient());
					}
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					if(cg != null){
						cg.connectionFailed();
					}
					break;

				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
	public String getUsername(){
		return username;
	}
	private class BuddyList extends JFrame  {
		private static final long serialVersionUID = 1L;
		private JList<String> buddyList;
		BuddyList(String name,ArrayList<String>al){
			DefaultListModel<String> listModel = new DefaultListModel<String>();
			for(int i = 0; i < al.size(); ++i){
				String tempName = al.get(i);
				if(!tempName.equals(name)){
					listModel.addElement(tempName); 
				}

			}
			buddyList = new JList<String>(listModel);
			add(buddyList);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setTitle(name+"'s friends online");
			this.setSize(300,400);
			buddyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.setVisible(true);
			buddyList.addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent le) {
					if (!le.getValueIsAdjusting()) {
						toClient=buddyList.getSelectedValue();
					}
				}
			});
		} 
	}
}


