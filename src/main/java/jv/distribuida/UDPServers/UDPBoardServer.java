package jv.distribuida.UDPServers;

import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.handlers.BoardHandlerManager;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

import java.io.IOException;
import java.net.InetAddress;

import static jv.distribuida.loadbalancer.ServiceInstance.startHeartBeat;

public class UDPBoardServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);
        RequestHandler handler = new BoardHandlerManager(databaseClient);
        UDPConnection connection = new UDPConnection(9001);

        UDPConnection hbconnection = new UDPConnection(9101);
        startHeartBeat(hbconnection);

        JsonObject json = new JsonObject();
        json.addProperty("target", "LoadBalancer");
        json.addProperty("service", "Board");
        json.addProperty("address", "localhost");
        json.addProperty("port", 9001);
        json.addProperty("heartbeat", 9101);
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