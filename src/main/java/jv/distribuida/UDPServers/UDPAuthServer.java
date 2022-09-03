package jv.distribuida.UDPServers;

import jv.distribuida.client.DatabaseClient;
import jv.distribuida.handlers.AuthHandlerManager;
import jv.distribuida.handlers.BoardHandlerManager;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

import java.io.IOException;
import java.net.InetAddress;

public class UDPAuthServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);
        RequestHandler handler = new AuthHandlerManager(databaseClient);
        UDPConnection connection = new UDPConnection(9004);
        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}