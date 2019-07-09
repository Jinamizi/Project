package adminserver;

import java.net.ServerSocket;

public interface SocketFactory {
    public ServerSocket createSocketFor(int port);
}
