package adminserver;
//throw illigelState if db not connected
//add logging
//change various data passed to map

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sourcefiles.FingerprintMatcher;
import sourcefiles.FingerprintTemplate;

/**
 * used to connect to the database and process database requests
 *
 * @author DEGUZMAN
 */
public class Database {

    public static Admin admin = new Admin();
    public static Customer customer = new Customer();
    private static Connection connection;

    static { //executedd once to setup the database
        connect();
    }

    Database() {
        connect();
    }

    /**
     * setup connection to the database
     */
    synchronized public static void connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
            }
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Could not connect to database", ex);
            System.err.println("Could not connect to database");
            ex.printStackTrace();
        }
    }

    /**
     * closes connection to the database
     */
    public static void disconnect() {
        try {

            connection.close();
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Could not connect to database", ex);
            System.err.println("Could not close  database");
            ex.printStackTrace();
        }
    }

    /**
     * checks if there is a connection with the database and tries to connect if
     * there is no connection
     */
    public static boolean checkConnection() {
        try {
            connect(); //try to connect 
            return connection != null && connection.isValid(100);
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error accessing database", ex);
            System.err.println("Error accessing database");
        }
        return false;
    }

    public static boolean connectionExist() {
        try {
            return connection != null && connection.isValid(100);
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * used to process admin username and password
     */
    public static class Admin {

        /**
         * check if admin exist in database
         *
         * @param username username of the administrator
         * @param password password of the administrator
         * @return true if admin exist else return false
         */
        public static boolean adminExist(String username, String password) {
            checkConnection();
            boolean adminFound = false;
            String query = "SELECT username FROM admin WHERE username = '" + username + "' AND password = '" + password + "'";

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {
                adminFound = resultSet.next(); //true if data exist
            } catch (SQLException ex) {
                System.err.println("SQLException:-" + ex.getMessage());
            }
            return adminFound;
        }
    }

    /**
     * used to get and manipulate customer details in the database
     */
    public static class Customer {

        /**
         * adds another account for a customer with given id number
         *
         * @param id_number the id number of the customer
         * @param account_number the new account number to be added
         * @return true if addition successful otherwise false
         * @throws java.sql.SQLException
         */
        public static boolean addAccount(String id_number, String account_number) throws SQLException {
            String query = "INSERT INTO accounts (id_number, account_number) VALUES('" + id_number + "','" + account_number + "')";
            try (Statement statement = connection.createStatement();
                    SQLClosable finish = connection::rollback) {
                return (statement.executeUpdate(query) > 0);
            }
        }

        public static String[] getAccounts(String id_number) throws SQLException {
        String query = "SELECT account_number FROM accounts WHERE id_number = '" + id_number + "'";
        ArrayList<String> accountNumbers = new ArrayList<>(1);

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                accountNumbers.add(resultSet.getString(1));
            }
        }
        return accountNumbers.toArray(new String[accountNumbers.size()]);
    }
        
        /**
         * add a new customer into the database
         *
         * @param id_number id number of the customer
         * @param firstName first name of the customer
         * @param lastName last name of the customer
         * @param password password of the customer
         * @param accountNumber account number of the customer
         * @param fingerprint fingerprint of the customer
         * @return return true if customer was added successfully otherwise
         * return false
         */
        public static boolean addCustomer(String id_number, String firstName, String lastName, String password, String accountNumber, BufferedImage fingerprint) throws SQLException, IOException {
            //checkConnection();
            try (Statement statement = connection.createStatement();
                    SQLClosable finish = connection::rollback;) { //force rollback
                connection.setAutoCommit(false);

                String detailsQuery = "INSERT INTO details (id_number, first_name, last_name) VALUES('" + id_number + "','" + firstName + "','" + lastName + "')";
                statement.executeUpdate(detailsQuery);

                String accountsQuery = "INSERT INTO accounts (id_number, account_number) VALUES('" + id_number + "','" + accountNumber + "')";
                statement.executeUpdate(accountsQuery);

                String passwordQuery = "INSERT into passwords (id_number, password) VALUES ('" + id_number + "','" + password + "')";
                statement.executeUpdate(passwordQuery);

                if (!fingerprintExist(fingerprint).equals("")) {
                    throw new IOException("print already exist in the database");
                }

                String fingerprintLocation = savePrint(fingerprint);

                String fingerprintQuery = "INSERT INTO fingerprints(id_number, print) VALUES('" + id_number + "','" + fingerprintLocation + "')";
                statement.executeUpdate(fingerprintQuery);

                connection.commit();

                return true;

            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw ex;
            } catch (IOException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                throw ex;
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("SQLException:-" + ex.getMessage());
                }
            }
        }

        /**
         * save the fingerprint on the hard-disk
         *
         * @param print fingerprint to be saved
         * @return return the location where the image was saved
         */
        private static String savePrint(BufferedImage print) throws IOException {
            File file = new File(getLocation());
            try {
                ImageIO.write(print, "jpg", file);
                return file.getPath();
            } catch (IOException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException(ex.getMessage());
            }
        }

        /**
         * find the correct location on the hard-disk to save the print
         *
         * @return the correct location
         */
        private static String getLocation() {
            String imageName = "print";
            String storageName = imageName;
            int fileCount = 0;
            while (new File(storageName + ".jpg").exists()) {
                storageName = imageName + ++fileCount;
            }
            return new File(storageName + ".jpg").getPath();
        }

        /**
         * checks if a customer exists in a database. NB. Checks only customers
         * who have set passwords
         *
         * @param id_number the id number of the customer
         * @param password the password of the customer
         * @return true if the customer exists otherwise false
         * @throws java.sql.SQLException
         */
        public static boolean customerExist(String id_number, String password) throws SQLException {
            String query = "SELECT id_number FROM passwords WHERE id_number = '" + id_number + "' AND password = '" + password + "'";

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next(); //true if data exist
            }
        }

        /**
         * check if the provided print exists in the database
         *
         * @param print print to check if
         * @return "EXIST" if it exist in database else DONT EXIST
         */
        public static String fingerprintExist(BufferedImage print) {
            FingerprintFinder finder = new FingerprintFinder(getFingerprintLocations());
            return (finder.find(print).equals("")) ? "EXIST" : "DONT EXIST";
        }

        /**
         * locates the id number for the finger print stored in the given
         * location
         *
         * @param location the location of the print
         * @return the id_number of the fingerprint
         * @throws SQLException
         */
        public static String getID(String location)  {
            String query = "SELECT id_number FROM fingerprints WHERE print = '" + location + "'";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) 
                    return resultSet.getString(1);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return "";
        }

        /**
         * Get the id of the person with the give print
         *
         * @param print the print to check id for
         * @return the id of the print
         */
        public static String getID(BufferedImage print) {
            String location = new FingerprintFinder(getFingerprintLocations()).find(print);
            return getID(location);
        }

        /**
         * checks if the given account number exist in the database
         *
         * @param account the account number to check
         * @return returns true if the account number is in the database
         * otherwise false
         * @throws Exception
         */
        public static boolean accountExist(String account) throws Exception {
            String query = "Select account_number FROM accounts WHERE account_number = '" + account + "'";
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {
                return resultSet.next();
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }

        /**
         * get all the print locations stored in the database
         *
         * @return returns locations stored as a String array
         * @throws SQLException throws SQLException if there is an error reading
         * the database
         */
        private static String[] getFingerprintLocations() {
            ArrayList<String> locations = new ArrayList<>(1);
            String query = "SELECT print FROM fingerprints"; //get all print locations stored in the database
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) //iterate the result set
                {
                    locations.add(resultSet.getString(1)); //store print location into the array list
                }
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            return locations.toArray(new String[locations.size()]);

        }
    }

    public static void main(String[] s) throws IOException, SQLException {
        //System.out.println(Database.customer.savePrint(ImageIO.read(new File("C:\\tonny\\fingerprint idea\\samples\\fingerprint.png"))));
        //String id_number = "330978", firstname = "tonzh",lastname = "ochieng",acccountnumber = "A0008209", password = "123";
        //BufferedImage image = ImageIO.read(new File("C:\\tonny\\fingerprint idea\\samples\\fingerprint.png"));
        //System.out.println(Database.customer.addCustomer(id_number, firstname, lastname, password, acccountnumber, image));
        //System.out.println(Database.customer.customerExist("333", "123"));
        //File file = new File("print.jpg");
        //System.out.println("Probe; - "+file.getPath());
        //BufferedImage bufferedImage = ImageIO.read(file);
        //System.out.println(Database.customer.fingerprintExist(bufferedImage));

        //for( String id : Database.Customer.getFingerprintLocations())
        //System.out.println(id + " " +Database.customer.getID(id));
        //System.out.println(Database.Customer.addAccount("333", "A100021"));
        //System.out.println(Database.Customer.addAccount("333", "A10000"));
        //System.out.println(Database.Customer.addAccount("33345", "A10000"));
    }

}
