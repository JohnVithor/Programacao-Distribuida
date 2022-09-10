package jv.distribuida.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Connection;
import jv.distribuida.network.ConnectionCreator;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.Message;

import java.io.IOException;
import java.net.InetAddress;

public class GetClient {
    private final ConnectionType type;
    private final InetAddress address;
    private final int port;

    public GetClient(InetAddress address, int port, ConnectionType type) {
        this.type = type;
        this.address = address;
        this.port = port;
    }

    JsonObject handleResponse(JsonObject request) {
        Connection connection = null;
        try {
            connection = ConnectionCreator.createConnection(type, address, port);
            connection.setTimeout(1000);
            connection.send(new Message(address, port, request.toString()));
            Message message = connection.receive();
            return JsonParser.parseString(message.getText()).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", e.getMessage());
            return response;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public JsonObject get(String service, int id, String token) {
        JsonObject json = new JsonObject();
        json.addProperty("target", service);
        json.addProperty("action", "GET");
        json.addProperty("token", token);
        json.addProperty("id", id);
        return handleResponse(json);
    }
}
