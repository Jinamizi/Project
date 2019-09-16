package adminserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
            System.out.println("Request received: " + request);
            switch (request.toLowerCase()) {
                case Constants.VERIFY_ADMIN_REQUEST:
                    readAndVerifyAdmin();
                    break;
                case Constants.GENERATE_ACCOUNT_REQUEST:
                    generateAccountNumber();
                    break;
                case Constants.ADD_CUSTOMER_REQUEST:
                    readAddCustomer();
                    break;
                case Constants.VERIFY_CUSTOMER_REQUEST:
                    readAndVerifyCustomer();
                    break;
                case Constants.CHECK_FINGERPRINT_REQUEST:
                    readAndCheckIfMinutiaeExist();
                    break;
                case Constants.ADD_ACCOUNT_REQUEST:
                    readAndAddAccount();
                    break;
                case Constants.GET_ID_REQUEST:
                    readPrintReturnId();
                    break;
                case Constants.GET_ACCOUNTS_REQUEST:
                    getAccounts();
                    break;
                case Constants.GET_ACCOUNT_BALANCES_REQUEST:
                    returnAccountsAndBalances();
                    break;
                case Constants.GET_NAMES_REQUEST:
                    returnNames();
                    break;
                default: //do nothing
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * reads id from the socket and returns all accounts and balances in the
     * accounts
     */
    private void returnAccountsAndBalances() throws Exception{
        Map<String, String> accounts = new HashMap<>();
        try {
            String id = inputStream.readUTF();
            accounts = Database.Customer.getAccountBalances(id);
        } catch (IOException ex) { //occurs during reading of socket
            accounts.put(Constants.ERROR, "Error reading the accounts. connsult developer");
            throw ex;
        }finally{
            outputStream.writeObject(accounts);
        }
    }

    /**
     * read an id number from the socket and return the names of the customer
     */
    private void returnNames() throws Exception {
        Map<String, String> names = new HashMap<>();
        try {
            String id = inputStream.readUTF();
            names = Database.Customer.getNames(id);
        } catch (IOException ex) { //occurs during reading of socket
            names.put(Constants.ERROR, "Error reading the names. connsult developer");
            throw ex;
        }finally{
            outputStream.writeObject(names);
        }
    }

    /**
     * reads id number from the socket and return the accounts registered under
     * it
     */
    private void getAccounts() throws Exception {
        String[] accounts = new String[1];
        try {
            String id = inputStream.readUTF();
            accounts = Database.Customer.getAccounts(id);
        } catch (IOException ex) {
            accounts[0] = "Could not get accounts. Consult the developer";
            throw ex;
        } finally {
            outputStream.writeObject(accounts);
        }
    }

    /**
     * reads a minutia from the socket and sends back the id number of the
     * minutia
     */
    private void readPrintReturnId() throws Exception {
        String result = "";
        try {
            String minutia = inputStream.readUTF();
            result = Database.Customer.getIDForMinutia(minutia);
        } catch (IOException | SQLException ex) {
            result = "Error reading print data. Consult the developer";
            throw ex;
        } finally {
            sendString(result);
        }
    }

    /**
     * used to add account for a given id number
     */
    private void readAndAddAccount() throws Exception {
        String result = "";
        try {
            Map<String, String> customerDetails = (Map<String, String>) inputStream.readObject();
            boolean additionResult = Database.Customer.addAccount(customerDetails.get(Constants.ID_NUMBER), customerDetails.get(Constants.ACCOUNT_NUMBER));
            result = (additionResult) ? Constants.ACTION_SUCCESSFUL : Constants.ACTION_UNSUCCESSFUL;
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            result = "Error occured while adding account. Consult the developer";
            throw ex;
        } finally {
            sendString(result);
        }
    }

    /**
     * reads a fingerprint minutia from the socket and check if it exist in the
     * database. sends {@link Constants.EXIST} if it exist,
     * {@link Constants.DONT_EXIST} if it does not exist or an error message if
     * there was an error.
     */
    private void readAndCheckIfMinutiaeExist() throws Exception {
        String result = "";
        try {
            String minutiae = inputStream.readUTF();
            result = Database.Customer.minutiaeExist(minutiae);
        } catch (IOException | SQLException ex) {
            result = "Error checking if the print exist. Consult the Developer";
            throw ex;
        } finally {
            sendString(result);
        }
    }

    /**
     * reads customer details from the socket and check if the customer exist in
     * the database.
     */
    private void readAndVerifyCustomer() throws Exception {
        String result = "";
        try {
            Map<String, String> map = (Map<String, String>) inputStream.readObject();
            Database.Customer.customerExist(map.get(Constants.ID_NUMBER), map.get(Constants.PASSWORD));
            boolean customerExist = Database.Customer.customerExist(map.get(Constants.ID_NUMBER), map.get(Constants.PASSWORD));
            result = (customerExist) ? Constants.EXIST : Constants.DONT_EXIST;
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            result = "Error verifying the customer. Consult the developer";
        } finally {
            sendString(result);
        }
    }

    /**
     * reads customer details from the socket and adds them into the database.
     * sends {@link Constants.ACTION_SUCCESSFUL } if addition was successful
     * otherwise {@link Constants.ACTION_UNSUCCESSFUL }
     */
    private void readAddCustomer() throws Exception {
        String result = "";
        try {
            Map<String, String> customerDetails = (Map<String, String>) inputStream.readObject();
            result = (Database.Customer.addCustomer(customerDetails)) ? Constants.ACTION_SUCCESSFUL : Constants.ACTION_UNSUCCESSFUL;
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            result = "Error occured while trying to add customer data: Consult developer";
            throw ex;
        } finally {
            sendString(result);
        }
    }

    /**
     * generates a random account number and sends it through the socket
     */
    private void generateAccountNumber() throws Exception {
        String account = "";
        try {
            //Iterate until an account that is not in the database is found 
            do {
                account = getAccountNumber();
            } while (Database.Customer.accountExist(account));
        } catch (Exception e) {
            account = "Error generating the account number : Consult the Developer";
            throw e;
        } finally {
            sendString(account);
        }
    }

    /**
     * generates a valid account number. account numbers are random characters
     *
     * @return the account number generated
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
    private void readAndVerifyAdmin() throws Exception {
        String result = "";
        try {
            Map<String, String> adminDetails = (Map<String, String>) inputStream.readObject();
            boolean verified = Database.Admin.adminExist(adminDetails.get(Constants.USERNAME), adminDetails.get(Constants.PASSWORD));
            result = (verified) ? Constants.EXIST : Constants.DONT_EXIST;
        } catch (IOException | ClassNotFoundException ex) {
            result = "An Error occurred while reading request: Consult the Developers";
            throw ex;
        } finally {
            sendString(result);
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

}//288
