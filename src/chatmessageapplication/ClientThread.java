/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatmessageapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Date;

/**
 *
 * @author Ujay
 */
public class ClientThread extends Thread {
      // the socket where to listen/talk

        Socket socket;
        public int port;
        ObjectInputStream sInput;

        ObjectOutputStream sOutput;

        // my unique id (easier for deconnection)

        int id;

        // the Username of the Client

        String username;

        // the only type of message a will receive

        ChatMessage cm;

        // the date I connect

        String date;

 

        // Constructore
        Server server;
       public ClientThread(Socket socket) {

            // a unique id
            server=new Server(port);
            id = ++Server.uniqueId;

            this.socket = socket;

            /* Creating both Data Stream */
            

            System.out.println("Thread trying to create Object Input/Output Streams");

            try

            {

                // create output first
               
                sOutput = new ObjectOutputStream(socket.getOutputStream());

                sInput  = new ObjectInputStream(socket.getInputStream());

                // read the username

                username = (String) sInput.readObject();

               server.display(username + " just connected.");

            }

            catch (IOException e) {
                server=new Server(port);
                server.display("Exception creating new Input/output Streams: " + e);

                return;

            }

            // have to catch ClassNotFoundException

            // but I read a String, I am sure it will work

            catch (ClassNotFoundException e) {

            }

            date = new Date().toString() + "\n";

        }

 

        // what will run forever

        public void run() {
            server=new Server(port);
            // to loop until LOGOUT

            boolean keepGoing = true;

            while(keepGoing) {

                // read a String (which is an object)

                try {

                    cm = (ChatMessage) sInput.readObject();

                }

                catch (IOException e) {
                    
                    server.display(username + " Exception reading Streams: " + e);

                    break;             

                }

                catch(ClassNotFoundException e2) {

                    break;

                }

                // the messaage part of the ChatMessage

                String message = cm.getMessage();

 

                // Switch on the type of message receive

                switch(cm.getType()) {

 

                case ChatMessage.MESSAGE:

                    server.broadcast(username + ": " + message);

                    break;

                case ChatMessage.LOGOUT:

                    server.display(username + " disconnected with a LOGOUT message.");

                    keepGoing = false;

                    break;

                case ChatMessage.WHOISIN:

                    writeMsg("List of the users connected at " + server.sdf.format(new Date()) + "\n");

                    // scan al the users connected

                    for(int i = 0; i < server.al.size(); ++i) {

                        ClientThread ct = server.al.get(i);

                        writeMsg((i+1) + ") " + ct.username + " since " + ct.date);

                    }

                    break;

                }

            }

            // remove myself from the arrayList containing the list of the
            // connected Clients

            server.remove(id);

            close();

        }

         

        // try to close everything

        private void close() {

            // try to close the connection

            try {

                if(sOutput != null) sOutput.close();

            }

            catch(Exception e) {}

            try {

                if(sInput != null) sInput.close();

            }

            catch(Exception e) {};
            try {

                if(socket != null) socket.close();

            }

            catch (Exception e) {}

        }

 

        /*

         * Write a String to the Client output stream

         */

        public boolean writeMsg(String msg) {

            // if Client is still connected send the message to it
            server=new Server(port);
            if(!socket.isConnected()) {

                close();

                return false;

            }

            // write the message to the stream

            try {

                sOutput.writeObject(msg);

            }

            // if an error occurs, do not abort just inform the user

            catch(IOException e) {

                server.display("Error sending message to " + username);

                server.display(e.toString());

            }

            return true;

        }

    }

  

