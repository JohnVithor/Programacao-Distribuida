package jv.distribuida.UDPServers;

import com.google.gson.JsonObject;
import jv.distribuida.database.Database;
import jv.distribuida.handlers.DatabaseHandlerManager;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;
import jv.distribuida.network.UDPConnection;
import jv.distribuida.network.UDPRequestHandler;

import java.io.IOException;
import java.util.HashMap;

public class UDPDatabaseServer {
    public static void main(String[] args) throws IOException {
        HashMap<String, Object> collections = new HashMap<>();
        collections.put("Board", new Object());
        collections.put("Issue", new Object());
        collections.put("Comment", new Object());
        collections.put("Auth", new Object());
        Database database = new Database(collections);
        RequestHandler handler = new DatabaseHandlerManager(database);
        UDPConnection connection = new UDPConnection(Integer.parseInt(args[0]));

        JsonObject request = new JsonObject();
        request.addProperty("username", "jv");
        request.addProperty("password", "123");
        database.save(request, "Auth");

        while (true) {
            Message message = connection.receive();
            Thread.ofVirtual().start(new UDPRequestHandler(connection, message, handler));
        }
    }
}