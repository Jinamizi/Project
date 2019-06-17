package adminserver;
//throw illigelState if db not connected
//add logging
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Database {
    
    private static Connection con ;//
    private PreparedStatement statement ; 
    private ResultSet resultSet;
    public static Admin admin = new Admin();
    
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
               throw new IllegalStateException("Could not connect to the database");
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
    
    static class Customer{
        public static void addCustomer(String id_number,String firstName,String lastName, String password, String accountNumber, ImageIcon fingerprint){
            try(Statement statement = con.createStatement();){
                con.setAutoCommit(false);
                String detailsQuery = "INSERT INTO details (id_number, firstname, lastname) VALUES(" +id_number+","+firstName+","+lastName+")";
                statement.executeUpdate(detailsQuery);
                String accountsQuery = "INSERT INTO accounts (id_number, account_number) VALUES("+id_number+","+accountNumber+")";
                statement.executeUpdate(accountsQuery);
                String passwordQuery = "INSERT into passwords (id_number, password) VALUES ("+id_number+","+password+")";
                statement.executeUpdate(passwordQuery);
                //String fingerprintQuery = "INSERT INTO fingerprints(id_number, print) VALUES("+id_number+","+fingerprintLocation+")";
                //statement.executeUpdate(fingerprintQuery);
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * save the fingerprint on the hard-disk
         * @param print fingerprint to be saved
         * @return return the location where the image was saved
         */
        public static String savePrint(ImageIcon print){
            File file = new File(getLocation());
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));){
                
                objectOutputStream.writeObject(print);
                objectOutputStream.flush();
                
                return file.getPath();
            } catch (IOException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "";
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
    }
    
    public static void main(String[] s){
        //System.out.println(Database.admin.adminExist("Tonny", "19234"));
        ImageIcon
    }
}
