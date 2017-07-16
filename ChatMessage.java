/**
 *Written by Stanley Uche Godfrey
 * This is the message object which
 * is the message sent from one client to 
 * server and received by another client
 */
package chatPkg;
import java.io.Serializable;
public class ChatMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	/*The different types of message sent by the Client
	WHOISIN to receive the list of the users connected
	MESSAGE an ordinary message
	LOGOUT to disconnect from the Server*/
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String message;
	private String toClient,fromClient;
	// constructor
	ChatMessage(int type, String message,String fromClient,String toClient) {
		this.type = type;
		this.message = message;
		this.toClient=toClient;
		this.fromClient=fromClient;
	}
	int getType() {
		return type;
	}
	String getMessage() {
		return message;
	}
	String getToClient() {
		return toClient;
	}
	String getFromClient() {
		return fromClient;
	}
}



