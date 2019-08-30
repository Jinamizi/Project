package adminserver;
//throw illigelState if db not connected
//add logging
//change various data passed to map

import java.sql.*;
import java.util.*;

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
     * @return true if there is a connection otherwise false
     */
    public static boolean checkConnection() {
        try {
            connect(); //try to connect 
            return connection != null && connection.isValid(100);
        } catch (SQLException ex) {
            System.err.println("Error accessing database");
        }
        return false;
    }

    public static boolean connectionExist() {
        try {
            return connection != null && connection.isValid(100);
        } catch (SQLException ex) {
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

        /**
         * Used to retrieve all the accounts of a given individual
         * 
         * @param id_number the id number of the individual whose account need to be retrieved
         * @return all the accounts of an individual
         * @throws SQLException if there was an error retrieving the accounts
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
         * adds a new customer into the database
         * @param details contains the details of the customer
         * @return true if the customer was added successfully else false
         * @throws SQLException if there was an error during addition of a customer
         */
        public static boolean addCustomer(Map<String,String> details) throws SQLException {
                ArrayList<String> queries   = new ArrayList<>();
                String firstName            = details.get("first_name");
                String id_number            = details.get("id_number");
                String lastName             = details.get("last_name");
                String accountNumber        = details.get("account_number");
                String password             = details.get("password");
                String print                = details.get("print");

                String detailsQuery = "INSERT INTO details (id_number, first_name, last_name) VALUES('" + id_number + "','" + firstName + "','" + lastName + "')";
                queries.add(detailsQuery);
                
                String accountsQuery = "INSERT INTO accounts (id_number, account_number) VALUES('" + id_number + "','" + accountNumber + "')";
                queries.add(accountsQuery);
                
                String passwordQuery = "INSERT into passwords (id_number, password) VALUES ('" + id_number + "','" + password + "')";
                queries.add(passwordQuery);
                
                if (minutiaeExist(print).equalsIgnoreCase("EXIST")) throw new SQLException("Print exist");
                String fingerprintQuery = "INSERT INTO fingerprints(id_number, print) VALUES('" + id_number + "','" + print + "')";
                queries.add(fingerprintQuery);
                
                return (executeStatement(queries.toArray(new String[queries.size()])) > 0);
        }
        
        /**
         * Used to execute a group of statements
         * 
         * @param queries a bunch of queries that updates the database
         * @return 1 if the update was successful
         * @throws SQLException if there was an error updating the database
         */
        private static int executeStatement(String[] queries) throws SQLException {
            try (Statement statement = connection.createStatement(); //Statement to execute the queries
                    SQLClosable setCommit = () -> connection.setAutoCommit(true); //reset the autocommit to true
                    SQLClosable finish = connection::rollback;) { //rollback all operation if transaction not successful
                
                connection.setAutoCommit(false);
                
                //execute the statements
                for (String query : queries ) 
                    if (statement.executeUpdate(query) < 1 )
                        throw new SQLException("Error during insertion");
                
                connection.commit();
            }
            return 1;
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
         * @param minutia the minutia to look for
         * @return "EXIST" if it exist in database else DONT EXIST
         * @throws java.sql.SQLException if there is an error communicating with the database
         */
        public static String minutiaeExist(String minutia) throws SQLException {
            MinutiaeFinder finder = new MinutiaeFinder(getMinutiae());
            return (finder.find(minutia).equals("")) ? "DONT EXIST" : "EXIST";
        }

        /**
         * gets the id of a minutia
         * @param minutia the minutia
         * @return the id of the minutia
         * @throws SQLException if there is an error getting the id
         */
        public static String getIDForMinutia(String minutia) throws SQLException {
            return new MinutiaeFinder(getMinutiae()).find(minutia);
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
         * get all the print minutiae stored in the database
         *
         * @return returns minutiae stored as a String array
         * @throws SQLException throws SQLException if there is an error reading
         * the database
         */
        private static Map<String,String> getMinutiae() throws SQLException{
            Map<String, String> minutiaeMap = new HashMap<>();
            String query = "SELECT * FROM fingerprints"; 
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) //iterate the result set
                {
                    minutiaeMap.put(resultSet.getString(1), resultSet.getString(2)); //store print minutiae into the array list
                }
            } 
            return minutiaeMap;

        }

        /**
         * used to get the names of the person with the given id number
         *
         * @param id the id number of the person
         * @return the names of the person
         * @throws SQLException if there was an error reading data from the
         * database
         */
        public static Map<String, String> getNames(String id) throws SQLException {
            Map<String, String> names = new HashMap<>();
            String query = "SELECT first_name, last_name FROM details WHERE id_number = '" + id + "'";
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    names.put("first_name", resultSet.getString("first_name"));
                    names.put("last_name", resultSet.getString("last_name"));
                }
            }

            return names;
        }

        /**
         * used to get the balances of all the accounts registered on a given id
         * number
         *
         * @param idNumber the id number of the customer
         * @return the accounts and their balances
         * @throws java.sql.SQLException
         */
        public static Map<String, String> getAccountBalances(String idNumber) throws SQLException {
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
    }

} //316
