package adminserver;

import java.io.IOException;
import java.net.*;

/**
 * Used to run the ServerSocket
 *
 * @author DEGUZMAN
 */
public class ServerThread extends Thread {

    ServerSocket port;

    /**
     * initializes the server thread
     *
     * @param serverSocket the server socket to process
     */
    public ServerThread(ServerSocket serverSocket) {
        if (serverSocket == null || serverSocket.isClosed()) //check if serversocket is in correct state
        {
            throw new IllegalStateException("ServerSocket not in correct state");
        }
        port = serverSocket;
    }
    
    public ServerSocket getPort(){
        return port;
    }
    
    /**
     * wait for connections and pass them to the {@link SocketHandler}
     */
    @Override
    public void run() {
        while (!port.isClosed()) { //while port is not closed
            try { //wait for connections
                Socket socket = port.accept();
                System.out.println("Received: "+ socket);
                new Thread(new SocketHandler(socket)).start(); //process the socket in another thread. use SocketHandler object or this
            } catch (IOException ex) {
                //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            }
        }
    }
}
