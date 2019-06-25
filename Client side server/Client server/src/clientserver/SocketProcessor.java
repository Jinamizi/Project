package clientserver;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SocketProcessor implements Runnable{
    Socket socket;

    public SocketProcessor(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        
    }
    
    public void readAndVerifyPAssword(){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());){
            Object[] 
        } catch (IOException ex) {
            Logger.getLogger(SocketProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
