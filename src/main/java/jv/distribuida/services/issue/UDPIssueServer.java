package jv.distribuida.services.issue;

import jv.distribuida.database.DatabaseClient;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

import java.io.IOException;
import java.net.InetAddress;

public class UDPIssueServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);
        RequestHandler handler = new IssueHandler(databaseClient);
        UDPConnection connection = new UDPConnection(8081);
        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}