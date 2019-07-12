package clientserver;
//throw illigelState if db not connected
//add logging

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sourcefiles.FingerprintMatcher;
import sourcefiles.FingerprintTemplate;

public class Database {

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
    public static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Could not connect to database", ex);
            System.err.println("Could not connect to database");
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
            System.err.println("Could not close to database");
        }
    }

    /**
     * checks if there is a connection with the database
     */
    public static boolean checkConnection() {
        connect();
        return connectionExist();
    }

    /**
     * Checks if a connection to the database exist
     * @return true if there is a connection otherwise false
     */
    public static boolean connectionExist() {
        boolean exist = false;
        try {
            exist = connection != null && connection.isValid(100);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    /**
     * checks if a customer exists in a database. NB. Checks only customers who
     * have set passwords
     *
     * @param id_number
     * @param password
     * @return return true if a customer with the given id_number exist
     * otherwise false
     */
    public static boolean customerExist(String id_number, String password) {
        boolean found = false;
        String query = "SELECT id_number FROM passwords WHERE id_number = '" + id_number + "' AND password = '" + password + "'";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            found = resultSet.next(); //true if data exist
        } catch (SQLException ex) {
            System.err.println("SQLException:-" + ex.getMessage());
        }
        return found;
    }

    //id not needed here
    /**
     * used to get the balance for a customer
     *
     * @param id_number the id number of the customer
     * @param account_number the account number of the customer
     * @return the balance of the customer in the account number
     */
    public static float getBalance(String id_number, String account_number) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE id_number = '" + id_number + "' AND account_number = '" + account_number + "'";
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getFloat(1);
            }
            throw new SQLException("Account not found");
        }
    }

    
    /**
     * used to fetch all the accounts of a given customer
     *
     * @param id_number the id number of the customer
     * @return all the accounts of a customer in form of a String array
     */
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
     * check if the provided print exists in the database
     *
     * @param print print to check if it exist
     * @return true if print exist otherwise false
     */
    public static String getID(BufferedImage print) {
        String location = new FingerprintFinder(getFingerprintLocations()).find(print);
        return getID(location);
    }

    /**
     * used to get the id of the print stored at a given location
     * @param printLocation the location of the print
     * @return the id of the print
     */
    private static String getID(String printLocation) {
        String query = "SELECT id_number FROM fingerprints WHERE print = '" + printLocation + "'";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * used to get the balances of all the accounts registered on a given id number
     * @param idNumber the id number of the customer
     * @return the accounts and their balances
     * @throws Exception 
     */
    public static Map<String, String> getAccountBalances(String idNumber) throws Exception {
        String query = "SELECT account_number, balance FROM accounts WHERE id_number = '" + idNumber + "'";
        ArrayList<String> accountNumbers = new ArrayList<>(1);
        Map<String, String> accounts = new HashMap<>();

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                accounts.put(resultSet.getString("account_number"), resultSet.getString("balance"));
            }
        }
        return accounts;
    }

    /**
     * get all the print locations stored in the database
     *
     * @return returns locations stored as a String array
     * @throws SQLException throws SQLException if there is an error reading the
     * database
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
            ex.printStackTrace();
        }
        return locations.toArray(new String[locations.size()]);
    }

    /**
     * Deducts the amount of money from the account
     * @param idNumber the id number of the customer
     * @param account the account number of the customer
     * @param amount the amount to withdraw
     * @return response which can be either SUCCESS, UNSUCCESSFULL or error messages
     */
    public static String withdraw(String idNumber, String account, double amount) {
        double balance = 0.0;
        String response = "";
        String query = "update accounts set balance = balance - '" + amount + "' where id_number = '" + idNumber + "' and account_number = '" + account + "'";
        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
            response = result > 0 ? "SUCCESS" : "UNSUCCESSFUL";
        } catch (SQLException ex) {
            ex.printStackTrace();
            response = ex.getMessage();
        }
        return response;
    }

    /**
     * deposits money to the given account of a id number
     * @param idNumber the id number of the customer
     * @param account the account number of the customer
     * @param amount the amount to deposit
     * @return either SUCCESS, UNSUCCESSFUL or the error encountered
     */
    public static String deposit(String idNumber, String account, float amount) {
        double balance = 0.0;
        String response = "";
        String query = "update accounts set balance = balance - '" + amount + "' where id_number = '" + idNumber + "' and account_number = '" + account + "'";
        try (Statement statement = connection.createStatement()) {
            int result = statement.executeUpdate(query);
            response = result > 0 ? "SUCCESS" : "UNSUCCESSFUL";
        } catch (SQLException ex) {
            ex.printStackTrace();
            response = ex.getMessage();
        }
        return response;
    }

    public static void main(String[] s) throws Exception {
        System.out.println(Database.getBalance("342203", "B80"));
        System.out.println(Database.getBalance("342203", "B80"));
        //for( String st : Database.getAccounts("33220326")) System.out.println(st);
        //BufferedImage image = ImageIO.read(new File("C:\\tonny\\fingerprint idea\\samples\\fingerprint.png"));
        //System.out.println(Database.getID(image));

    }
}

