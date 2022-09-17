package jv.distribuida.HTTPServers;

import com.google.gson.JsonObject;
import jv.distribuida.database.Database;
import jv.distribuida.handlers.DatabaseHandlerManager;
import jv.distribuida.network.HTTPConnection;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.TCPConnection;
import jv.distribuida.network.TCPRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class HTTPDatabaseServer {
    public static void main(String[] args) {
        try{
            HashMap<String, Object> collections = new HashMap<>();
            collections.put("Board", new Object());
            collections.put("Issue", new Object());
            collections.put("Comment", new Object());
            collections.put("Auth", new Object());
            Database database = new Database(collections);

            JsonObject request = new JsonObject();
            request.addProperty("username", "jv");
            request.addProperty("password", "123");
            database.save(request, "Auth");

            RequestHandler handler = new DatabaseHandlerManager(database);
            try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {
                System.err.println("Iniciando Database na porta " + args[0]);
                while (true) {
                    Thread.ofVirtual().start(new TCPRequestHandler(new HTTPConnection(serverSocket.accept()), handler));
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