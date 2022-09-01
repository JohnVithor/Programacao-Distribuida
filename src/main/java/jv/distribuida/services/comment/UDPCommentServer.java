package jv.distribuida.services.comment;

import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

import java.io.IOException;
import java.net.InetAddress;

public class UDPCommentServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);

        UDPConnection issueConnection = new UDPConnection();
        GetClient getClient = new GetClient(InetAddress.getLocalHost(), 8081, issueConnection);

        RequestHandler handler = new CommentHandler(databaseClient, getClient);
        UDPConnection connection = new UDPConnection(8082);
        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}