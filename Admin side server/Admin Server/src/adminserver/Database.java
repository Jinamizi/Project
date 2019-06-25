package adminserver;
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
    
    public static Admin admin = new Admin();
    public static Customer customer = new Customer();
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
     * used to process admin username and password
     */
    static class Admin{ 
        /**
         * check if admin exist in database
         * @param username username of the administrator
         * @param password password of the administrator
         * @return true if admin exist else return false
         */
        public static boolean adminExist(String username, String password) {
            checkConnection();
            boolean adminFound = false;
            String query = "SELECT username FROM admin WHERE username = '"+username+"' AND password = '"+password+"'";
            
            try (Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(query)){
                    adminFound =  resultSet.next(); //true if data exist
            } catch (SQLException ex) {
                System.err.println("SQLException:-"+ex.getMessage());
            }
            return adminFound;
        }
    }
    
    /**
     * used to get and manipulate customer details in the database
     */
    private static class Customer{
        /**
         * add a new customer into the database
         * @param id_number id number of the customer
         * @param firstName first name of the customer
         * @param lastName last name of the customer
         * @param password password of the customer
         * @param accountNumber account number of the customer
         * @param fingerprint fingerprint of the customer
         * @return return true if customer was added successfully otherwise return false
         */
        public static boolean addCustomer(String id_number,String firstName,String lastName, String password, String accountNumber, BufferedImage fingerprint){
            //checkConnection();
            try(Statement statement = con.createStatement();
                    SQLClosable finish = con::rollback;){ //force rollback
                con.setAutoCommit(false);
                
                String detailsQuery = "INSERT INTO details (id_number, first_name, last_name) VALUES('" +id_number+"','"+firstName+"','"+lastName+"')";
                statement.executeUpdate(detailsQuery);
                
                String accountsQuery = "INSERT INTO accounts (id_number, account_number) VALUES('"+id_number+"','"+accountNumber+"')";
                statement.executeUpdate(accountsQuery);
                
                String passwordQuery = "INSERT into passwords (id_number, password) VALUES ('"+id_number+"','"+password+"')";
                statement.executeUpdate(passwordQuery);
                
                String fingerprintLocation =  savePrint(fingerprint);
                
                String fingerprintQuery = "INSERT INTO fingerprints(id_number, print) VALUES('"+id_number+"','"+fingerprintLocation+"')";
                statement.executeUpdate(fingerprintQuery);
                
                con.commit();
                
                return true;
                
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("SQLException:-"+ex.getMessage());
            } catch (IOException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("IOException:-"+ex.getMessage());
            } finally{
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println("SQLException:-"+ex.getMessage());
                }
            }
            return false;
        }
        
        /**
         * save the fingerprint on the hard-disk
         * @param print fingerprint to be saved
         * @return return the location where the image was saved
         */
        public static String savePrint(BufferedImage print) throws IOException{
            File file = new File(getLocation()) ;
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
         * @return the correct location
         */
        private static String getLocation(){
            String imageName = "print";
            String storageName = imageName;
            int fileCount = 0;
            while(new File(storageName+".jpg").exists())
                storageName = imageName+ ++fileCount;
            return new File(storageName+".jpg").getPath();
        }
        
        /**
         * checks if a customer exists in a database.
         * NB. Checks only customers who have set passwords
         * @param id_number
         * @param password
         * @return 
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
        * check if the provided print exists in the database
        * @param print print to check if it exist
        * @return true if print exist otherwise false
        */
        public static boolean fingerprintExist(BufferedImage print){
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
                        return true;
                }
            } catch (SQLException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("SQLException:-"+ex.getMessage());
            } catch (IOException ex) {
                //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("IOException:-"+ex.getMessage());
            }
            
            return false;
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
    }
    
    
    public static void main(String[] s) throws IOException{
        //System.out.println(Database.admin.adminExist("Tonny", "19234"));
        //System.out.println(Database.customer.savePrint(ImageIO.read(new File("C:\\tonny\\fingerprint idea\\samples\\fingerprint.png"))));
        //String id_number = "330", firstname = "ton",lastname = "ochi",acccountnumber = "A000002", password = "123";
        //BufferedImage image = ImageIO.read(new File("C:\\tonny\\fingerprint idea\\samples\\fingerprint.png"));
        //System.out.println(Database.customer.addCustomer(id_number, firstname, lastname, password, acccountnumber, image));
        //System.out.println(Database.customer.customerExist("333", "123"));
        File file = new File("print.jpg");
        System.out.println("Probe; - "+file.getPath());
        BufferedImage bufferedImage = ImageIO.read(file);
        System.out.println(Database.customer.fingerprintExist(bufferedImage));
    }
    
    
}

interface SQLClosable extends AutoCloseable {
    public void close() throws SQLException;
}

