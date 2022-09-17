package jv.distribuida.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionCreator {

    public static Connection createConnection(ConnectionType type, InetAddress address, int port) throws IOException {
        return switch (type) {
            case UDP -> new UDPConnection();
            case TCP -> new TCPConnection(new Socket(address, port));
            case HTTP -> new HTTPConnection(new Socket(address, port));
        };
    }
}
