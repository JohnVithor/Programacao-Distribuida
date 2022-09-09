package jv.distribuida.UDPServers;

import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.handlers.AuthHandlerManager;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

import java.io.IOException;
import java.net.InetAddress;

import static jv.distribuida.loadbalancer.ServiceInstance.startHeartBeat;

public class UDPAuthServer {
    public static void main(String[] args) throws IOException {
        UDPConnection dbconnection = new UDPConnection();
        DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), 9000, dbconnection);
        RequestHandler handler = new AuthHandlerManager(databaseClient);
        UDPConnection connection = new UDPConnection(9004);

        UDPConnection hbconnection = new UDPConnection(9104);
        startHeartBeat(hbconnection);

        JsonObject json = new JsonObject();
        json.addProperty("target", "LoadBalancer");
        json.addProperty("service", "Auth");
        json.addProperty("address", "localhost");
        json.addProperty("port", 9004);
        json.addProperty("heartbeat", 9104);
        json.addProperty("auth", false);
        connection.send(new Message(InetAddress.getLocalHost(), 9005, json.toString()));
        Message m = connection.receive();
        System.out.println(m.getText());

        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }


}