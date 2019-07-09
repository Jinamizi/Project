package adminserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SocketFactoryImpl implements SocketFactory{

    @Override
    public ServerSocket createSocketFor(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(SocketFactoryImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
