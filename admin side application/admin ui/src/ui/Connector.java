package ui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

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
        //if(!connect()) return "Could not connect"; //try to connect

        String response = "";
        try (Socket socket = new Socket("127.0.0.1", PORT);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            System.out.println("Verifying admin");
            String request = "verify admin";
            Map<String, String> adminData = new HashMap<>();
            adminData.put("username", username);
            adminData.put("password", password);

            //send data across the socket
            outputStream.writeUTF(request);
            outputStream.writeObject(adminData);
            outputStream.flush();

            //receive response
            response = inputStream.readUTF();
        }
        return response;
    }

    /**
     * used to check the customer with the given credentials exist.
     *
     * @param idNumber the id number of the customer
     * @param password the password of the customer
     * @return returns response from the server either the customer exist or not
     * or if an error occur
     * @throws IOException if there is an error communicating with the server
     */
    public static String verifyCustomer(String idNumber, String password) throws IOException {
        String response = "";
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
            response = inputStream.readUTF();
        }
        return response;
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
        String response;
        System.out.println("Check if print exist");
        try (Socket socket = new Socket("127.0.0.1", 13085);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

            String request = "check if fingerprint exist";

            System.out.println("Sending request");

            outputStream.writeUTF(request); //send request
            //outputStream.flush();
            System.out.println("Request sent");

            //sendImage(out, image);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);

            //send the size of the image first
            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            outputStream.write(size);
            //out.write(size);
            //out.write(byteArrayOutputStream.toByteArray());
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.flush();
            
            System.out.println("Image sent");
            response = inputStream.readUTF();
        }
        return response;
    }

    /**
     * used to send an image on an output stream. the size of the image is sent
     * first the the image
     *
     * @param stream the output stream send the image through;
     * @param image the image to be sent
     * @return true if the image was sent successfully otherwise false
     */
    private static boolean sendImage(OutputStream outputStream, BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);

        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
        outputStream.write(size);
        outputStream.write(byteArrayOutputStream.toByteArray());
        outputStream.flush();

        return true;
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
        String response;
        try (Socket socket = new Socket("127.0.0.1", PORT);
                OutputStream out = socket.getOutputStream();
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream outputStream = new ObjectOutputStream(out)) {

            String request = "get id";

            outputStream.writeUTF(request); //send request
            sendImage(out, print);

            response = inputStream.readUTF();
        }
        return response;
    }

    /**
     * add the given account to the available account of the person of the given
     * id number
     *
     * @param idNumber the id Number of the customer
     * @param accountNumber the new account number of the customer
     * @return the response of the server
     * @throws IOException if there was an error communicating with the server
     */
    public static String addAccount(String idNumber, String accountNumber) throws IOException {
        String response = "";
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

            //receive response
            response = inputStream.readUTF();
        }
        return response;
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
        String response = "";
        try (Socket socket = new Socket("127.0.0.1", 8888);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
            String request = "add customer";
            //send data across the socket
            outputStream.writeUTF(request);
            outputStream.writeObject(customerData);

            //receive response
            response = inputStream.readUTF();
        }
        return response;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("connector");
        //System.out.println(Connector.generateAccountNumber());
        //System.out.println(Connector.verifyAdmin("Tonny", "1234"));
        //System.out.println(Connector.verifyAdmin("Tonn", "1234"));
        //System.out.println(Connector.verifyCustomer("333", "123"));
        //System.out.println(Connector.verifyCustomer("3333", "1234"));
        BufferedImage image = ImageIO.read(new File("print.jpg"));
        //System.out.println(Connector.checkIfPrintExist(image));
        //BufferedImage image1 = ImageIO.read(new File("non_existing.jpg"));
        System.out.println(Connector.checkIfPrintExist(image));
        //System.out.println(Connector.addAccount("333", "A200"));
        //System.out.println(Connector.addAccount("33344", "A200"));
        //System.out.println(Connector.addAccount("333", "A60"));
        //System.out.println(Connector.getID(image));
        //System.out.println(Connector.getID(image1));
    }
}
