/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author DEGUZMAN
 */
public class SocketHandler implements Runnable {

    Socket socket;
    InputStream in;
    OutputStream out;
    ObjectInputStream inputStream; 
    ObjectOutputStream outputStream;

    /**
     * initializes the SocketHandler
     *
     * @param socket the socket to process
     */
    public SocketHandler(Socket socket) {
        this.socket = socket;
        setStreams();
    }

    private void setStreams() {
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            inputStream = new ObjectInputStream(in);
            outputStream = new ObjectOutputStream(out);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * reads the request and call the appropriate method to process the request
     */
    @Override
    public void run() {
        try (IOClosable closer = socket::close) {
            String request = inputStream.readUTF();
            System.out.println("Request received: "+request);
            switch (request.toLowerCase()) {
                case "verify admin":
                    readAndVerifyAdmin();
                    break;
                case "generate account":
                    generateAccountNumber();
                    break;
                case "add customer":
                    readAndAddCustomer();
                    break;
                case "verify customer":
                    readAndVerifyCustomer();
                    break;
                case "check if fingerprint exist":
                    readAndCheckIfPrintExist();
                    break;
                case "add account":
                    readAndAddAccount();
                    break;
                case "get id":
                    readPrintAndReturnId();
                    break;
                default: //do nothing
            }
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
}
