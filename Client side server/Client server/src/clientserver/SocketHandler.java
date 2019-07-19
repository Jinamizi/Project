/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//make sure to make it a must to pass credentials for each request
package clientserver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                case "verify print":
                    readPrintAndReturnId();
                    break;
                case "get accounts":
                    getAccounts();
                    break;
                case "verify password":
                    veryfyPassword();
                    break;
                case "get account balances":
                    getAccountBalances();
                    break;
                case "withdraw":
                    withdraw();
                    break;
                default: //do nothing
            }
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * perform the withdraw action
     */
    private void withdraw(){
        String response ;
        try {
            Map<String,String> data = (Map<String,String>) inputStream.readObject();
            response = Database.withdraw(data.get("id_number"), data.get("account_number"), Double.parseDouble( data.get("amount")));
        } catch (IOException ex) {
            ex.printStackTrace();
            response = "ERROR: " +ex.getMessage();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            response = "ERROR";
        }
        
        try {
            sendString(response);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * process getting of customers accounts and balances in the accounts
     */
    private void getAccountBalances() {
        String response = "";
        try {
            String id = inputStream.readUTF();
            Map<String, String> result;
            try {
                result = Database.getAccountBalances(id);
            } catch (Exception ex) {
                result = new HashMap<>();
                result.put("Error", ex.getMessage());
            }
            outputStream.writeObject(result);
            return;
        } catch (IOException ex) {
            ex.printStackTrace();
            response = ex.getMessage();
        } 
    }
    
    /**
     * used to verify customer credentials
     */
    private void veryfyPassword() {
       String result;
        try {
            Map<String, String> map = (Map<String, String>) inputStream.readObject();
            result = (verifyCustomer(map.get("id_number"), map.get("password"))) ? "EXIST" : "NOT FOUND"; 
        } catch (Exception ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            result = ex.getMessage();
        }

        try {
            sendString(result);
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * verify if a customer with the given id_number and password exist
     *
     * @param id_number the id number of the customer
     * @param password the password of the customer
     * @return true if the customer exist otherwise false
     * @throws SQLException
     */
    private boolean verifyCustomer(String id_number, String password) throws SQLException {
        return Database.customerExist(id_number, password);
    }
    
    /**
     * gets the accounts of particular id read from sockets. sends result back as a string array
     */
    private void getAccounts() {
        try {
            String id = inputStream.readUTF();
            String[] accounts;
            try {
                accounts = Database.getAccounts(id);
                outputStream.writeObject(accounts);
            } catch (SQLException ex) {
                outputStream.writeObject(ex.getMessage());
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * reads a print from the socket and sends back the id number of the print stored in the database
     */
    private void readPrintAndReturnId(){
        String result;
        try {
            BufferedImage image = readImage();
            result = getIDForPrint(image);
        } catch (Exception ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            result = "ERROR" + ex.getMessage();
            ex.printStackTrace();
        }
        try {
            sendString(result);
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * Asks the database for the id of the given print
     * @param image the print
     * @return the id of the print
     */
    private String getIDForPrint(BufferedImage image) {
        return Database.getID(image);
    }
    
    /**
     * reads the image from the socket
     *
     * @return returns the image read as a buffered image
     * @throws IOException
     */
    private BufferedImage readImage() throws IOException {
            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);

            return ImageIO.read(new ByteArrayInputStream(imageAr));
    }
    
    /**
     * used to send a string across the socket
     *
     * @param value the string to send
     * @throws IOException throws an exception if an error occurs during sending
     */
    private void sendString(String value) throws IOException {
        outputStream.writeUTF(value);
        outputStream.flush();
    }
}
