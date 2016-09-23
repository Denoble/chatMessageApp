/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*You can start the Server by typing 
> java Server
at the console prompt. That will execute it in console mode and the server will wait for connection on port 1500. To use another port pass the port number to use as first parameter to the command
> java Server 1200
will ask the Server to listen on port 1200.
You can use <CTRL>C to stop the server.*/
package chatmessageapplication;

/**
 *
 * @author Ujay
 */
import java.io.*;

import java.net.*;

import java.text.SimpleDateFormat;

import java.util.*;

public class Server {
       // a unique ID for each connection

    public static int uniqueId;

    // an ArrayList to keep the list of the Client

    public ArrayList<ClientThread> al;

    // if I am in a GUI

    public ServerGUI sg;
	
    // to display time

    public SimpleDateFormat sdf;

    // the port number to listen for connection

    public int port;

    // the boolean that will be turned off to stop the server

    public boolean keepGoing;

     

 

    /*

     *  server constructor that receive the port to listen to for connection as parameter

     *  in console

     */

    public Server(int port) {

        this(port, null);

    }

     

    public Server(int port, ServerGUI sg) {

        // GUI or not

        this.sg = sg;

        // the port

        this.port = port;

        // to display hh:mm:ss

        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list

        al = new ArrayList<ClientThread>();

    }

    
    public void start() {

        keepGoing = true;

        /* create socket server and wait for connection requests */

        try

        {

            // the socket used by the server

            ServerSocket serverSocket = new ServerSocket(port);

 

            // infinite loop to wait for connections

            while(keepGoing)

            {

                // format message saying we are waiting

                display("Server waiting for Clients on port " + port + ".");

                 

                Socket socket = serverSocket.accept();      // accept connection

                // if I was asked to stop

                if(!keepGoing)

                    break;

                ClientThread t = new ClientThread(socket);  // make a thread of it

                al.add(t);                                  // save it in the ArrayList

                t.start();

            }

            // I was asked to stop

            try {

                serverSocket.close();

                for(int i = 0; i < al.size(); ++i) {

                    ClientThread tc = al.get(i);

                    try {

                    tc.sInput.close();

                    tc.sOutput.close();

                    tc.socket.close();

                    }

                    catch(IOException ioE) {

                        // not much I can do

                    
                }

            }
          }

            catch(Exception e) {

                display("Exception closing the server and clients: " + e);

            }

        }

        // something went bad

        catch (IOException e) {

            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";

            display(msg);

        }

     }
    /*

     * For the GUI to stop the server

     */

    public void stop() {

        keepGoing = false;

        // connect to myself as Client to exit statement

        // Socket socket = serverSocket.accept();

        try {

            new Socket("localhost", port);
        }

        catch(Exception e) {

            // nothing I can really do

        }

    }
    /*

     * Display an event (not a message) to the console or the GUI

     */

    public void display(String msg) {

        String time = sdf.format(new Date()) + " " + msg;

        if(sg == null)

            System.out.println(time);

        else

            sg.appendEvent(time + "\n");

    }
  /*
115
     *  to broadcast a message to all Clients
116
     */

    public synchronized void broadcast(String message) {

        // add HH:mm:ss and \n to the message
        String time = sdf.format(new Date());

        String messageLf = time + " " + message + "\n";

        // display message on console or GUI

        if(sg == null)

            System.out.print(messageLf);
        else

            sg.appendRoom(messageLf);     // append in the room window

         

        // we loop in reverse order in case we would have to remove a Client

        // because it has disconnected
        for(int i = al.size(); --i >= 0;) {

            ClientThread ct = al.get(i);

            // try to write to the Client if it fails remove it from the list

            if(!ct.writeMsg(messageLf)) {

                al.remove(i);

                display("Disconnected Client " + ct.username + " removed from list.");

            }

        }

    }

  // for a client who logoff using the LOGOUT message

    synchronized void remove(int id) {

        // scan the array list until we found the Id

        for(int i = 0; i < al.size(); ++i) {

            ClientThread ct = al.get(i);

            // found it

            if(ct.id == id) {

                al.remove(i);

                return;

            }

        }

    }

     

 }
