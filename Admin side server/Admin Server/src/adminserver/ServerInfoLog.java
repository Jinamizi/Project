/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adminserver;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *used to log information about the server e.g when it started or stopped and any errors that occur on the server
 * @author DEGUZMAN
 */
public class ServerInfoLog {
    private static Logger logger;
    static{ initialize(); }
    
    private static void initialize()  {
        try {
            String file_name = "server_info_log.txt";
            File f = new File(file_name);
            if(!f.exists()){
                f.createNewFile();
            }
            FileHandler fh = new FileHandler(file_name, true);
            logger = Logger.getLogger("server_info_log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException ex) {
            Logger.getLogger(ServerInfoLog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ServerInfoLog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void log(Level level, String message){
        logger.log(level, message + "\n");
    }
}
