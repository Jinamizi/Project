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
    
    public static String[] getAccount(String idNumber) throws IOException {
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
    
    public static String verifyPrint(BufferedImage image) throws IOException{
        String response = "";
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            
            String request = "verify print";
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, response, baos)
            outputStream.writeUTF(request);
            outputStream.writeObject(data);
            outputStream.flush();
            
            response = inputStream.readUTF();
        }
        
        return response;
    }
    
    public static void main(String [ ] args) {
       
    }
}
