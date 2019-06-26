package adminserver;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * A class used to handle and process a socket on an independent thread
 *
 * @author DEGUZMAN
 * @version 1.0
 */
public class SocketHandler implements Runnable {

    private Socket socket;

    /**
     * initializes the SocketHandler
     *
     * @param socket the socket to process
     */
    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * reads the request and call the appropriate method to process the request
     */
    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            String request = inputStream.readObject().toString();
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
                default: //do nothing
            }
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    /**
     * used to add account for a given id number
     */
    private void readAndAddAccount() {
        String result;
        try {
            Map<String, String> customerDetails = readCustomer();
            if (addAccount(customerDetails.get("id_number"), customerDetails.get("Account_number"))) {
                result = "SUCCESSFUL";
            } else {
                result = "UNSUCCESSFUL";
            }
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
     * add a new account to the customer with a given id number
     *
     * @param id_number the id number of the customer
     * @param newAccount the new account number for the customer
     * @return true if account was added successfully otherwise false
     * @throws SQLException
     */
    private boolean addAccount(String id_number, String newAccount) throws SQLException {
        return Database.Customer.addAccount(id_number, newAccount);
    }

    /**
     * reads a fingerprint image from the socket and check if it exist in the
     * database. sends the id number of the fingerprint accross the socket
     */
    private void readAndCheckIfPrintExist() {
        String result;
        try {
            BufferedImage image = readImage();
            result = printExist(image);
        } catch (Exception ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            result = ex.getMessage();
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
     * checks if the fingerprint exist in the database
     *
     * @param image the fingerprint to check if it exists
     * @return the id number of the customer with the given finger print.
     * Returns "" if fingerprint not found
     * @throws SQLException
     * @throws IOException
     */
    private String printExist(BufferedImage image) throws SQLException, IOException {
        return Database.Customer.fingerprintExist(image);
    }

    /**
     * reads customer details from the socket and check if the customer exist in
     * the database.
     */
    private void readAndVerifyCustomer() {
        String result;
        try {
            Map<String, String> map = readCustomer();
            if (verifyCustomer(map.getOrDefault("id_number", null), map.getOrDefault("password", null))) {
                result = "EXIST";
            } else {
                result = "NOT FOUND";
            }
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
     * reads customer details from the socket
     *
     * @return customer details
     * @throws IOException
     * @throws Exception
     */
    private Map<String, String> readCustomer() throws IOException, Exception {
        Map<String, String> customerCredentials = new HashMap<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            return (Map<String, String>) inputStream.readObject();
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Could not read data");
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
        return Database.Customer.customerExist(id_number, password);
    }

    /**
     * reads customer details from the socket and adds them into the database.
     * sends "SUCCESS" if addition was successful otherwise "UNSUCCESSFUL"
     */
    private void readAndAddCustomer() {
        String result;
        try {
            Map<String, String> map = readCustomer();
            BufferedImage image = readImage();
            if (addCustomer(map, image)) //if addition of customer was successful
            {
                result = "SUCCESS";
            } else {
                result = "UNSUCCESSFUL";
            }
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
     * add a customer into the database
     *
     * @param details the details of the customer
     * @param fingerPrint the fingerprint of the customer
     * @return true if customer was successfully added otherwise false
     * @throws SQLException
     * @throws IOException
     */
    private boolean addCustomer(Map<String, String> details, BufferedImage fingerPrint) throws SQLException, IOException {
        return Database.Customer.addCustomer(details.getOrDefault("id_number", null), details.getOrDefault("first name", null),
                details.getOrDefault("last name", null), details.getOrDefault("password", null), details.getOrDefault("account_number", null), fingerPrint);
    }

    /**
     * reads the image from the socket
     *
     * @return returns the image read as a buffered image
     * @throws IOException
     */
    private BufferedImage readImage() throws IOException {
        try (InputStream inputStream = socket.getInputStream()) {

            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);

            return ImageIO.read(new ByteArrayInputStream(imageAr));
        }
    }

    {
        /**
         * reads the customer details from the socket
         *
         * @return returns the customer details in Map form
         * @throws IOException throws IOException if an error occurred during
         * reading
         */
        /*private Map<String, String> readCustomer() throws IOException, Exception{
         //ArrayList<String> customerData = new ArrayList<>(1);
         Map<String,String> customerData = new HashMap<>();
         try(ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())){
         customerData = (Map<String, String>) inputStream.readObject();
         return customerData;
         } catch (ClassNotFoundException ex) {
         //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
         ex.printStackTrace();
         throw new Exception("Could not read data");
         }

         }*/
    }

    /**
     * generates a random account number and sends it through the socket
     */
    private void generateAccountNumber() {
        String account;
        try {
            do {
                account = getAccountNumber();
            } while (Database.Customer.accountExist(account));
        } catch (Exception e) {
            account = e.getMessage();
            e.printStackTrace();
        }

        try {
            sendString(account);
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    /**
     * generates a valid account number. account numbers are random characters
     *
     * @return
     */
    private String getAccountNumber() {
        Random generator = new Random();
        char precedingCharacter = (char) (generator.nextInt(26) + 65); //get a value in the range A - Z
        String account = precedingCharacter + String.valueOf(generator.nextInt(10)) + String.valueOf(generator.nextInt(10))
                + String.valueOf(generator.nextInt(10)) + String.valueOf(generator.nextInt(10)) + String.valueOf(generator.nextInt(10));
        return account;
    }

    /**
     * reads admin data from the socket and verify it. it sends the result
     * across the socket
     */
    private void readAndVerifyAdmin() {
        boolean verified = false;
        String result;
        try {
            String[] adminDetails = readAdmin();
            verified = verifyAdmin(adminDetails[0], adminDetails[1]);
            if (verified) {
                result = "EXIST";
            } else {
                result = "NOT FOUND";
            }
        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            result = ex.getMessage();
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
     * use to send a string across the socket
     *
     * @param value the string to send
     * @throws IOException throws an exception if an error occurs during sending
     */
    private void sendString(String value) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            outputStream.writeObject(value);
        }
    }

    /**
     * reads admin data from the socket.
     *
     * @return returns admin data in form of an array
     * @throws IOException
     */
    private String[] readAdmin() throws IOException {
        ArrayList<String> adminData = new ArrayList<>(1);
        try (ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {
            adminData.add(inputStream.readObject().toString()); //read user name
            adminData.add(inputStream.readObject().toString()); //read password
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return adminData.toArray(new String[adminData.size()]);
    }

    /**
     * check if the admin with the given credentials exist in the database
     *
     * @param username the username of the admin
     * @param password the password of the admin
     * @return
     */
    private boolean verifyAdmin(String username, String password) {
        return (Database.Admin.adminExist(username, password));
    }

}
