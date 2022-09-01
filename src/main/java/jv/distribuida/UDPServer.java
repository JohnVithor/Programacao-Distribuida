package jv.distribuida;

import jv.distribuida.database.DatabaseClient;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;
import jv.distribuida.service.BoardHandler;

import java.io.IOException;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection(9001);
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);
        RequestHandler handler = new BoardHandler(databaseClient);
        UDPConnection connection = new UDPConnection(8080);
        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}