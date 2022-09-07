package jv.distribuida.UDPServers;

import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;
import jv.distribuida.handlers.CommentHandlerManager;

import java.io.IOException;
import java.net.InetAddress;

public class UDPCommentServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);

        UDPConnection issueConnection = new UDPConnection();
        GetClient getClient = new GetClient(InetAddress.getLocalHost(), 9002, issueConnection);

        RequestHandler handler = new CommentHandlerManager(databaseClient, getClient);
        UDPConnection connection = new UDPConnection(9003);

        JsonObject json = new JsonObject();
        json.addProperty("target", "LoadBalancer");
        json.addProperty("service", "Comment");
        json.addProperty("address", "localhost");
        json.addProperty("port", 9003);
        json.addProperty("auth", true);
        connection.send(new Message(InetAddress.getLocalHost(), 9005, json.toString()));
        Message m = connection.receive();
        System.out.println(m.getText());

        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}