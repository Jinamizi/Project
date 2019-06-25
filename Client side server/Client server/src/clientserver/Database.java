package clientserver;
//throw illigelState if db not connected
//add logging
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import sourcefiles.FingerprintMatcher;
import sourcefiles.FingerprintTemplate;

public class Database {
    private static Connection con ;
    static{ //executedd once to setup the database
        connect();
    }
    
    Database(){
        connect();
    }
    /**
     * setup connection to the database
     */
    public static void connect(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
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
            con.close();
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Could not connect to database", ex);
            System.err.println("Could not close to database");
        }
    }
    /**
     * checks if there is a connection with the database
     */
    public static void checkConnection(){
        try {
            if(!con.isValid(100)) //check if there is a connection
               throw new IllegalStateException("Could not connect to the database"); //change to connect
        } catch (SQLException ex) {
            //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Error accessing database", ex);
            System.err.println("Error accessing database");
        }
    }
    
    /**
     * checks if a customer exists in a database.
     * NB. Checks only customers who have set passwords
     * @param id_number
     * @param password
     * @return return true if a customer with the given id_number exist otherwise false
    */
    public static boolean customerExist(String id_number, String password){
            boolean found = false;
            String query = "SELECT id_number FROM passwords WHERE id_number = '"+id_number+"' AND password = '"+password+"'";
            
            try (Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(query)){
                    found =  resultSet.next(); //true if data exist
            } catch (SQLException ex) {
                System.err.println("SQLException:-"+ex.getMessage());
            }
            return found;
        }
    
    /**
     * used to get the balance for a customer
     * @param id_number the id number of the customer
     * @param account_number the account number of the customer
     * @return the balance of the customer in the account number
     */
    public static float getBalance(String id_number, String account_number) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE id_number = '"+id_number+"' AND account_number = '"+account_number+"'";
        try(Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(query)){
            if (resultSet.next())
                return resultSet.getFloat(1);
            throw new SQLException("Account not found");
        }
    }
    
    
    /**
     * used to fetch all the accounts of a given customer
     * @param id_number the id number of the customer
     * @return all the accounts of a customer in form of a String array
     */
    public static String[] getAccounts(String id_number) throws SQLException{
        String query = "SELECT account_number FROM accounts WHERE id_number = '"+id_number+"'";
        ArrayList<String> accountNumbers = new ArrayList<>(1);
        
        try(Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(query)){
            while(resultSet.next())
                accountNumbers.add(resultSet.getString(1));
        }
        return accountNumbers.toArray(new String[accountNumbers.size()]);
    }
        
        /**
        * check if the provided print exists in the database
        * @param print print to check if it exist
        * @return true if print exist otherwise false
        */
        public static String getID(BufferedImage print){
            try {
                String[] printLocations = getFingerprintLocations();
                System.out.println("Locations: "+printLocations);
                
                FingerprintTemplate probe = new FingerprintTemplate().create(loadImage(print));
                FingerprintMatcher matcher = new FingerprintMatcher().index(probe);
                for(String location : printLocations){
                    System.out.println("Currently being matched: "+location);
                    BufferedImage bufferedImage = ImageIO.read(new File(location));
                    FingerprintTemplate matching = new FingerprintTemplate().create(loadImage(bufferedImage));
                    double value = matcher.match(matching);
                    System.out.println("Value returned: "+value);
                    if (value < 40 )
                        return getID(location);
                }
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("SQLException:-"+ex.getMessage());
            } catch (IOException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("IOException:-"+ex.getMessage());
            }
            
            return "";
        }
        
        private static String getID(String printLocation) throws SQLException{
            String query = "SELECT id_number FROM fingerprints WHERE print = '"+printLocation+"'";
            try(Statement statement = con.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)){
                if( resultSet.next()) return resultSet.getString(1);
                else return "";
            }
        }
        
        /**
         * converts a buffered image into array
         * @param bImage the image to be converted
         * @return the byte form of the converted image
         */
        private static byte[] loadImage(BufferedImage bImage){
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bImage, "jpg", bos );
            
                byte [] data = bos.toByteArray();
                return data;
            }catch(Exception e){}
            return new byte[0];   
        }
        
        
    /**
    * get all the print locations stored in the database
    * @return returns locations stored as a String array
    * @throws SQLException throws SQLException if there is an error reading the database
    */
    private static String[] getFingerprintLocations() throws SQLException{
            ArrayList<String> locations = new ArrayList<>(1);
            String query = "SELECT print FROM fingerprints"; //get all print locations stored in the database
            try(Statement statement = con.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)){
            
                while(resultSet.next()) //iterate the result set
                    locations.add(resultSet.getString(1)); //store print location into the array list
            
                return locations.toArray(new String[locations.size()]);
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
    
    
    public static void main(String[] s) throws Exception{
        //System.out.println(Database.getBalance("342203", "B80"));
        //for( String st : Database.getAccounts("33220326")) System.out.println(st);
        BufferedImage image = ImageIO.read(new File("C:\\tonny\\fingerprint idea\\samples\\fingerprint.png"));
        System.out.println(Database.getID(image));
            
    }
}

interface SQLClosable extends AutoCloseable {
    public void close() throws SQLException;
}

