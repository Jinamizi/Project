package adminserver;

import java.io.IOException;
import java.net.ServerSocket;

public  class Server {
    private static ServerSocket serverSocket;
    public static final int PORT_NUMBER = 8888;
    private static ServerThread serverThread;
    //private ServerInfoLog serverLog = new ServerInfoLog();
    
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
        
        Database.connect();
        
        return serverThread.isAlive();
    }
    
    /**
     * checks if the server is running
     * @return 
     */
    public static boolean isRunning(){
        return serverThread != null && serverThread.isAlive();
    }
    
    /**
     * stops the server.
     * @return returns the status of the server socket
     * @throws IOException if an error occurred during closing of the server
     */
    public static boolean stop() throws IOException {
        //if (serverSocket != null || !serverSocket.isClosed())
            serverSocket.close();
        Database.disconnect();
        return serverSocket.isClosed();
    }
    public static void main(String [] arg) throws Exception{
        Server.start();
        Thread runnable = new Thread() {
            @Override
            public void run() {
                while(Server.isRunning()){
                    System.out.println("server running");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                    System.out.println("Database running: " +Database.connectionExist());
                }
                System.out.println("server stoped running"); 
                System.out.println("Database running: " +Database.connectionExist());
            }
        };
        
        runnable.start();
        runnable.join(5000);
        Server.stop();
       
    }
}
