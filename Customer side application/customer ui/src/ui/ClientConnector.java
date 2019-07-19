/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.*;
import javax.imageio.ImageIO;

/**
 *
 * @author DEGUZMAN
 */
public class ClientConnector {
    public static int PORT = 8889;
    
    /**
     * deducts some cash from a customer's account
     * @param idNumber the id number of the customer
     * @param account the account of the customer
     * @param amount the amount to withdraw
     * @return Response from the server which can either be SUCCESS if the withdrawing was successful, UNSUCCESSFULL if withdrawal was unsuccessful, or an error message from the server
     * @throws IOException 
     */
    public static String withdraw(String idNumber, String account, double amount) throws IOException{
        String response = "";
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            
            String request = "withdraw";
            
            Map<String, String> data = new HashMap<>();
            data.put("id_number", idNumber);
            data.put("account_number", account);
            data.put("amount", String.valueOf(amount));
            
            outputStream.writeUTF(request);
            outputStream.writeObject(data);
            outputStream.flush();
            
            response = inputStream.readUTF();
        }
        
        return response;
    }
    
    /**
     * checks if a customer exist in the database
     * @param idNumber the id number of the customer
     * @param password the password of the customer
     * @return "EXIST" the customer exist "NOT FOUND" if customer not found else an error is returned
     * @throws IOException 
     */
    public static String verifycustomer(String idNumber, String password) throws IOException {
        String response = "";
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            
            String request = "verify password";
            
            Map<String, String> data = new HashMap<>();
            data.put("id_number", idNumber);
            data.put("password", password);
            
            outputStream.writeUTF(request);
            outputStream.writeObject(data);
            outputStream.flush();
            
            response = inputStream.readUTF();
        }
        
        return response;
    }
    
    /**
     * gets all the accounts of a customer and the balances in the accounts
     * @param idNumber id number of customer
     * @return a map of accounts and balances
     * @throws IOException if there was an error communicating with the server
     */
    public static Map<String, String> getAccountBalances(String idNumber) throws IOException{
        Map<String,String> result = new HashMap<>();
        
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            
            String request = "get account balances";
            
            outputStream.writeUTF(request);
            outputStream.writeUTF(idNumber);
            outputStream.flush();
            
            result= (Map<String, String>) inputStream.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * gets all the accounts of a particular customer
     * @param idNumber the id number of the customer
     * @return all the accounts of the customer
     * @throws IOException if there was an error communicating with the server
     */
    public static String[] getAccounts(String idNumber) throws IOException {
        String [] result;
        
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            
            String request = "get accounts";
            
            outputStream.writeUTF(request);
            outputStream.writeUTF(idNumber);
            outputStream.flush();
            
            return result= (String[]) inputStream.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * get the id number of a print
     * @param image the print
     * @return the id number or "" if does not exist or an ERROR if there was an error
     * @throws IOException 
     */
    public static String verifyPrint(BufferedImage image) throws IOException{
        String response = "";
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            
            String request = "verify print";
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, response, baos);
            outputStream.writeUTF(request);
            outputStream.write(baos.toByteArray());
            outputStream.flush();
            
            response = inputStream.readUTF();
        }
        
        return response;
    }
    
    public static void main(String [ ] args) {
       
    }
}
