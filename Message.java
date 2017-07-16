/**
 *
 * @author Stanley Uche Godfrey
 */
package chatPkg;
import java.io.Serializable;
import java.util.ArrayList;
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	//protected static final long serialVersionUID = 1212122200L;
	private ArrayList<String> al;
	private ChatMessage cm;
	Message( ArrayList<String>al,ChatMessage cm){
		this.cm=cm;
		this.al=al;
	}
	public ArrayList<String> getConnectedClient(){
		return al;

	}
	public ChatMessage getMessage(){
		return cm;
	}

}
