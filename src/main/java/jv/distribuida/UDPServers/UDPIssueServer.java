package jv.distribuida.UDPServers;

import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;
import jv.distribuida.handlers.IssueHandlerManager;
import jv.distribuida.network.*;

import java.io.IOException;
import java.net.InetAddress;

import static jv.distribuida.loadbalancer.ServiceInstance.UDPstartHeartBeat;

public class UDPIssueServer {
    public static void main(String[] args) throws IOException {
        try {
            int port = Integer.parseInt(args[0]);
            int hbport = Integer.parseInt(args[1]);
            int dbport = Integer.parseInt(args[2]);
            int lbport = Integer.parseInt(args[3]);

            DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), dbport, ConnectionType.UDP);

            GetClient lbQuery = new GetClient(InetAddress.getLocalHost(), lbport, ConnectionType.UDP);
            RequestHandler handler = new IssueHandlerManager(databaseClient, lbQuery);
            UDPConnection connection = new UDPConnection(port);

            UDPConnection hbconnection = new UDPConnection(hbport);
            UDPstartHeartBeat(hbconnection);

            JsonObject json = new JsonObject();
            json.addProperty("target", "LoadBalancer");
            json.addProperty("service", "Issue");
            json.addProperty("port", port);
            json.addProperty("heartbeat", hbport);
            json.addProperty("auth", true);
            connection.send(new Message(InetAddress.getLocalHost(), lbport, json.toString()));
            Message m = connection.receive();
            System.out.println(m.getText());

            System.err.println("Iniciando IssueService na porta " + args[0]);
            while (true) {
                Message message = connection.receive();
                Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Finalizando execução");
    }
}