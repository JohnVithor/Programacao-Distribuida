package jv.distribuida.TCPServers;

import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;
import jv.distribuida.handlers.CommentHandlerManager;
import jv.distribuida.network.*;

import java.net.InetAddress;

import static jv.distribuida.loadbalancer.ServiceInstance.startHeartBeat;

public class TCPCommentServer {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(args[0]);
            int hbport = Integer.parseInt(args[1]);
            int dbport = Integer.parseInt(args[2]);
            int lbport = Integer.parseInt(args[3]);

            DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), dbport, ConnectionType.UDP);

            GetClient lbQuery = new GetClient(InetAddress.getLocalHost(), lbport, ConnectionType.UDP);
            RequestHandler handler = new CommentHandlerManager(databaseClient, lbQuery);

            UDPConnection hbconnection = new UDPConnection(hbport);
            startHeartBeat(hbconnection);

            JsonObject json = new JsonObject();
            json.addProperty("target", "LoadBalancer");
            json.addProperty("service", "Comment");
            json.addProperty("address", "localhost");
            json.addProperty("port", port);
            json.addProperty("heartbeat", hbport);
            json.addProperty("auth", true);
            UDPConnection connection = new UDPConnection(port);
            connection.send(new Message(InetAddress.getLocalHost(), lbport, json.toString()));
            Message m = connection.receive();
            System.out.println(m.getText());

            System.err.println("Iniciando CommentService na porta " + args[0]);
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