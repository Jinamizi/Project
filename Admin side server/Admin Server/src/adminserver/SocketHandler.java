package adminserver;

//put the sendstring method on the try blocks
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

/**
 * A class used to handle and process a socket on an independent thread
 *
 * @author DEGUZMAN
 * @version 1.0
 */
public class SocketHandler implements Runnable {

    Socket socket;
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
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
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
                    readAddCustomer();
                    break;
                case "verify customer":
                    readAndVerifyCustomer();
                    break;
                case "check if fingerprint exist":
                    readAndCheckIfMinutiaeExist();
                    break;
                case "add account":
                    readAndAddAccount();
                    break;
                case "get id":
                    readPrintReturnId();
                    break;
                case "get accounts":
                    getAccounts();
                    break;
                case "get account balances":
                    returnAccountsAndBalances();
                    break;
                case "get names":
                    returnNames();
                    break;
                default: //do nothing
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * reads id from the socket and returns all accounts and balances in the accounts
     */
    private void returnAccountsAndBalances() {
        try {
            String id = inputStream.readUTF();
            Map<String, String> accounts ;
            try {
                accounts = Database.Customer.getAccountBalances(id);
                outputStream.writeObject(accounts);
            } catch (SQLException ex) {
                outputStream.writeObject(ex.getMessage());
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * read an id number from the socket and return the names of the customer
     */
    private void returnNames(){
        try {
            String id = inputStream.readUTF();
            Map<String, String> names;
            try {
                names = Database.Customer.getNames(id);
                outputStream.writeObject(names);
            } catch (SQLException ex) {
                outputStream.writeObject(ex.getMessage());
            }
            
        } catch (IOException ex) { //occurs during reading of socket
            ex.printStackTrace();
        }
    }
    
    /**
     * reads id number from the socket and return the accounts registered under it
     */
    private void getAccounts() {
        try {
            String id = inputStream.readUTF();
            String[] accounts;
            try {
                accounts = Database.Customer.getAccounts(id);
                outputStream.writeObject(accounts);
            } catch (SQLException ex) {
                outputStream.writeObject(ex.getMessage());
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * reads a minutia from the socket and sends back the id number of the minutia 
     */
    private void readPrintReturnId(){
        String result;
        try {
            String minutia = inputStream.readUTF();
            result = getIDForMinutia(minutia);
        } catch (Exception ex) {
            result = "ERROR" + ex.getMessage();
            ex.printStackTrace();
        }
        try {
            sendString(result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String getIDForMinutia(String minutia) throws SQLException {
        return Database.Customer.getIDForMinutia(minutia);
    }
    
    /**
     * used to add account for a given id number
     */
    private void readAndAddAccount() {
        String result;
        try {
            Map<String, String> customerDetails = (Map<String, String>) inputStream.readObject();
            result = (addAccount(customerDetails.get("id_number"), customerDetails.get("account_number"))) ? "SUCCESSFUL" : "UNSUCCESSFUL";
        } catch (Exception ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            result = "ERROR"+ex.getMessage();
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
     * reads a fingerprint minutia from the socket and check if it exist in the
     * database. sends the id number of the fingerprint across the socket
     */
    private void readAndCheckIfMinutiaeExist() {
        String result;
        try {
            String minutiae = inputStream.readUTF();
            result = minutiaeExist(minutiae);
        } catch (IOException | SQLException ex) {
            result = "ERROR:" + ex.getMessage();
            ex.printStackTrace();
        }
        try {
            sendString(result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * checks if the minutia exist in the database
     *
     * @param minutia the minutia to check if it exists
     * @return  "EXIST" if it exist in database else DONT EXIST
     * @throws SQLException if there was error communicating with the database
     */
    private String minutiaeExist(String minutia) throws SQLException {
        return Database.Customer.minutiaeExist(minutia);
    }

    /**
     * reads customer details from the socket and check if the customer exist in
     * the database.
     */
    private void readAndVerifyCustomer() {
        String result;
        try {
            Map<String, String> map = (Map<String, String>) inputStream.readObject();
            result = (verifyCustomer(map.get("id_number"), map.get("password"))) ? "EXIST" : "DONT EXIST"; 
        } catch (Exception ex) {
            ex.printStackTrace();
            result = "ERROR" + ex.getMessage();
        }

        try {
            sendString(result);
        } catch (IOException ex) {
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
        return Database.Customer.customerExist(id_number, password);
    }
    
    /**
     * reads customer details from the socket and adds them into the database.
     * sends "SUCCESS" if addition was successful otherwise "UNSUCCESSFUL"
     */
    private void readAddCustomer() {
        String result;
        try {
            Map<String, String> map = (Map<String, String>) inputStream.readObject();
            result = (addCustomer(map)) ? "SUCCESS" : "UNSUCCESSFULL";
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            result = "ERROR"+ex.getMessage();
        }

        try {
            sendString(result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * adds a customer into the database
     * @param details the details of the customer
     * @return true if customer was added successfully otherwise false
     * @throws SQLException if there was an error adding the customer
     */
    private boolean addCustomer(Map<String, String> details) throws SQLException {
        return Database.Customer.addCustomer(details);
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
            //Map<String, String> adminDetails = readAdmin();
            Map<String, String> adminDetails = (Map<String, String>) inputStream.readObject();
            verified = verifyAdmin(adminDetails.get("username"), adminDetails.get("password"));
            result = (verified) ? "EXIST" : "NOT FOUND";

        } catch (IOException ex) {
            //Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            result = ex.getMessage();
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
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
     * used to send a string across the socket
     *
     * @param value the string to send
     * @throws IOException throws an exception if an error occurs during sending
     */
    private void sendString(String value) throws IOException {
        outputStream.writeUTF(value);
        outputStream.flush();
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

}//461
