//make sure to make it a must to pass credentials for each request
package clientserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
            switch (request.toLowerCase()) {
                case Constants.GET_ID_REQUEST:
                    readPrintReturnId();
                    break;
                case Constants.GET_ACCOUNT_REQUEST:
                    getAccounts();
                    break;
                case Constants.VERIFY_PASSWORD_REQUEST:
                    verifyPassword();
                    break;
                case Constants.GET_ACCOUNT_BALANCES_REQUEST:
                    getAccountBalances();
                    break;
                case Constants.WITHDRAW_REQUEST:
                    withdraw();
                    break;
                default: //do nothing
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * perform the withdraw action
     */
    private void withdraw() throws Exception {
        String response = "";
        try {
            Map<String, String> data = (Map<String, String>) inputStream.readObject();
            response = Database.withdraw(data.get(Constants.ID_NUMBER), data.get(Constants.ACCOUNT_NUMBER), Double.parseDouble(data.get("amount")));
        } catch (IOException | ClassNotFoundException | NumberFormatException | SQLException ex) {
            response = "Error withdrawing cash. Try again later";
            throw ex;
        } finally {
            sendString(response);
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
    private void verifyPassword() {
        String result;
        try {
            Map<String, String> map = (Map<String, String>) inputStream.readObject();
            result = (verifyCustomer(map.get("id_number"), map.get("password"))) ? Constants.ACCOUNT_NUMBER : "NOT FOUND";
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
     * gets the accounts of particular id read from sockets. sends result back
     * as a string array
     */
    private void getAccounts() throws SQLException, IOException {
        String[] accounts = new String[1];
        try {
            String id = inputStream.readUTF();
            accounts = Database.getAccounts(id);
        } catch (IOException ex) {
            accounts[0] = "An error occured getting the accounts. Try again later or check with the bank"; 
            throw ex;
        } finally{
            outputStream.writeObject(accounts);
        }
    }

    /**
     * reads a print from the socket and sends back the id number of the print
     * stored in the database
     */
    private void readPrintReturnId() throws Exception {
        String result = "";
        try {
            String minutiae = inputStream.readUTF();
            result = getIDForMinutiae(minutiae);
        } catch (IOException | SQLException ex) {
            result = "Error processing print. Try again later or check with the bank";
            throw ex;
        } finally {
            sendString(result);
        }
    }

    /**
     * used to retrieve the ID of the customer with the given finger-print
     * minutiae
     *
     * @param minutiae the minutiae of the customer
     * @return id number
     * @throws SQLException
     */
    private String getIDForMinutiae(String minutiae) throws SQLException {
        return Database.getMinutiaeID(minutiae);
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
//201
