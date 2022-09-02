package jv.distribuida.UDPServers;

import jv.distribuida.client.GetClient;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;
import jv.distribuida.handlers.IssueHandlerManager;

import java.io.IOException;
import java.net.InetAddress;

public class UDPIssueServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbConnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbConnection);

        UDPConnection boardConnection = new UDPConnection();
        GetClient getClient = new GetClient(InetAddress.getLocalHost(), 9001, boardConnection);

        RequestHandler handler = new IssueHandlerManager(databaseClient, getClient);
        UDPConnection connection = new UDPConnection(9002);
        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}