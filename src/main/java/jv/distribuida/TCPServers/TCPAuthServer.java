package jv.distribuida.TCPServers;

import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.handlers.AuthHandlerManager;
import jv.distribuida.handlers.BoardHandlerManager;
import jv.distribuida.network.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static jv.distribuida.loadbalancer.ServiceInstance.startHeartBeat;

public class TCPAuthServer {
    public static void main(String[] args) {
        try{
            int port = Integer.parseInt(args[0]);
            int hbport = Integer.parseInt(args[1]);
            int dbport = Integer.parseInt(args[2]);
            int lbport = Integer.parseInt(args[3]);

            DatabaseClient databaseClient = new DatabaseClient(InetAddress.getLocalHost(), dbport, ConnectionType.TCP);
            RequestHandler handler = new AuthHandlerManager(databaseClient);

            TCPConnection hbconnection = new TCPConnection(new Socket(InetAddress.getLocalHost(), hbport));
            startHeartBeat(hbconnection);

            JsonObject json = new JsonObject();
            json.addProperty("target", "LoadBalancer");
            json.addProperty("service", "Auth");
            json.addProperty("address", "localhost");
            json.addProperty("port", port);
            json.addProperty("heartbeat", hbport);
            json.addProperty("auth", false);
            TCPConnection connection = new TCPConnection(new Socket(InetAddress.getLocalHost(), lbport));
            connection.send(new Message(InetAddress.getLocalHost(), lbport, json.toString()));
            Message m = connection.receive();
            System.out.println(m.getText());
            connection.close();

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.err.println("Iniciando AuthService na porta " + args[0]);
                while (true) {
                    Thread.ofVirtual().start(new TCPRequestHandler(new TCPConnection(serverSocket.accept()), handler));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Finalizando execução");
    }
}