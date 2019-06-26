package adminserver;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private static ServerSocket serverSocket;
    public static final int PORT_NUMBER = 8888;
    private static ServerThread serverThread;
    
    /**
     * starts the server. Initializes the server socket and make {@ServerThread} to wait for connections on a new thread.
     * @return returns true if the server thread is running otherwise false
     * @throws IOException 
     */
    public static boolean start() throws IOException {
        if(serverSocket == null || serverSocket.isClosed())
            serverSocket = new ServerSocket(PORT_NUMBER);
        if(serverThread == null || !serverThread.isAlive()){
            serverThread = new ServerThread(serverSocket);
            serverThread.start();
        }
        
        return serverThread.isAlive();
    }
    
    public static boolean stop() throws IOException {
        if (serverSocket != null || !serverSocket.isClosed())
            serverSocket.close();
        return serverSocket.isClosed();
    }
}
