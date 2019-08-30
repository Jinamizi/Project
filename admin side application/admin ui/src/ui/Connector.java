package ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sourcefiles.FingerprintTemplate;

/**
 * This class is used by the admin UI to connect, send request and receive
 * responses from the server.
 *
 * @since FBA 1.0
 * @author DEGUZMAN
 */
public class Connector {

    private static final int PORT = 8888;

    /**
     * used to check if there is a connection with the server
     *
     * @return true if there is a connection otherwise false
     */
    public static boolean connectionExist() {
        try (Socket socket = new Socket("127.0.0.1", PORT)) {
            return socket.isConnected();
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * used to verify credentials of an admin.
     *
     * the method sends the credentials as a {@link Map} containing the
     * credentials to the server. It reads the response from the server and
     * return the response to the caller.
     *
     * @param username the username of the admin
     * @param password the password of the admin
     * @return the response from the server or the error message
     * @throws IOException if an error occurred during sending or receiving of
     * data
     */
    public static String verifyAdmin(String username, String password) throws IOException {
        String response;
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "verify admin";
            Map<String, String> adminData = new HashMap<>();
            adminData.put("username", username);
            adminData.put("password", password);

            //send data across the socket
            outputStream.writeUTF(request);
            outputStream.writeObject(adminData);
            outputStream.flush();

            //receive response and return 
            return inputStream.readUTF();
        }
    }

    /**
     * used to check the customer with the given credentials exist.
     *
     * @param idNumber the id number of the customer
     * @param password the password of the customer
     * @return returns response from the server either "EXIST" if customer exist
     * or "NOT EXIST" if customer does not exist or an error message starting
     * with "ERROR" if an error occur
     * @throws IOException if there is an error communicating with the server
     */
    public static String verifyCustomer(String idNumber, String password) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "verify customer";
            Map<String, String> adminData = new HashMap<>();
            adminData.put("id_number", idNumber);
            adminData.put("password", password);

            //send data across the socket
            outputStream.writeUTF(request);
            outputStream.writeObject(adminData);

            //receive response
            return inputStream.readUTF();
        }
    }

    /**
     * used to send print image to the server to check if the print exist in the
     * database
     *
     * @param image the print to check if it exist
     * @return the response of the server
     * @throws IOException if an error occurs during sending or retrieving of
     * the print
     */
    public static String checkIfPrintExist(BufferedImage image) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "check if fingerprint exist";

            String minutiae = new FingerprintTemplate().create(convert(image)).serialize();

            outputStream.writeUTF(request);
            outputStream.writeUTF(minutiae);
            outputStream.flush();

            return inputStream.readUTF();
        }
    }

    /**
     * covert a buffered image to a byte array.
     *
     * @param image the image to be converted to a byte array
     * @return byte of the image
     * @throws IOException if there was an error with the conversion
     */
    private static byte[] convert(BufferedImage image) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", bos);
        return bos.toByteArray();
    }

    /**
     * Requests the server to generate an account number
     *
     * @return the response from the server
     * @throws IOException if there was an error communicating with the server
     */
    public static String generateAccountNumber() throws Exception {
        String response = "";
        String request = "generate account";

        try (Socket socket = new Socket("127.0.0.1", 8888);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            outputStream.writeUTF(request);
            System.out.println("Request sent");
            outputStream.flush();
            response = inputStream.readUTF();
        }
        return response;
    }

    /**
     * used to ask the server for the id number of the person with the given
     * print
     *
     * @param print the print to enquire id number for
     * @return the id number of the print or "" if print doesn't exist or error
     * message
     * @throws IOException if there was an error communicating with the server
     */
    public static String getID(BufferedImage print) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {

            String request = "get id";

            String minutiae = new FingerprintTemplate().create(convert(print)).serialize();

            outputStream.writeUTF(request);
            outputStream.writeUTF(minutiae);
            outputStream.flush();

            return inputStream.readUTF();
        }
    }

    /**
     * add the given account to the available account of the person of the given
     * id number
     *
     * @param idNumber the id Number of the customer
     * @param accountNumber the new account number of the customer
     * @return "SUCCESSFUL" if addition was successful , "UNSUCCESSFUL" if
     * addition was unsuccessful or an error message starting with "ERROR" if an
     * error was encountered during addition
     * @throws IOException if there was an error communicating with the server
     */
    public static String addAccount(String idNumber, String accountNumber) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 8888);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "add account";
            Map<String, String> customerData = new HashMap<>();
            customerData.put("id_number", idNumber);
            customerData.put("account_number", accountNumber);

            //send data across the socket
            outputStream.writeUTF(request);
            outputStream.writeObject(customerData);
            outputStream.flush();

            return inputStream.readUTF();
        }
    }

    /**
     * asks the server to add a customer with the data passed and the given
     * print
     *
     * @param customerData the data of the customer
     * @param print the fingerprint of the customer
     * @return response from the server either the customer was added
     * successfully
     * @throws IOException
     */
    public static String addCustomer(Map<String, String> customerData, BufferedImage print) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", 8888);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "add customer";

            String minutia = new FingerprintTemplate().create(convert(print)).serialize();
            customerData.put("print", minutia);

            outputStream.writeUTF(request);
            outputStream.writeObject(customerData);
            outputStream.flush();

            return inputStream.readUTF();
        }
    }

    /**
     * used to get names of the customer with the given id number
     *
     * @param idNumber the id number of the customer
     * @return the names of the customer
     * @throws IOException if there was an error communicating with the server
     */
    public static Map<String, String> getNames(String idNumber) throws IOException {
        Map<String, String> result = new HashMap<>();

        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "get names";

            outputStream.writeUTF(request);
            outputStream.writeUTF(idNumber);
            outputStream.flush();

            result = (Map<String, String>) inputStream.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * gets all the accounts of a customer and the balances in the accounts
     *
     * @param idNumber id number of customer
     * @return a map of accounts and balances
     * @throws IOException if there was an error communicating with the server
     */
    public static Map<String, String> getAccountBalances(String idNumber) throws IOException {
        Map<String, String> result = new HashMap<>();

        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "get account balances";

            outputStream.writeUTF(request);
            outputStream.writeUTF(idNumber);
            outputStream.flush();

            result = (Map<String, String>) inputStream.readObject();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return result;
    }

}//334
