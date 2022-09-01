package jv.distribuida.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Connection;
import jv.distribuida.network.Message;

import java.io.IOException;
import java.net.InetAddress;

public class BoardClient {
    private final Connection connection;
    private final InetAddress address;
    private final int port;

    public BoardClient(InetAddress address, int port, Connection connection) {
        this.connection = connection;
        this.address = address;
        this.port = port;
    }

    JsonElement handleResponse(JsonObject request) {
        try {
            connection.send(new Message(address, port, request.toString()));
            Message message = connection.receive();
            JsonObject response = JsonParser.parseString(message.getText()).getAsJsonObject();
            if (response.get("status").getAsString().equals("Success")) {
                return response.get("data");
            } else {
                throw new RuntimeException(response.get("message").getAsString());
            }
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonElement get(int id, String user) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "GET");
        json.addProperty("token", user);
        json.addProperty("id", id);
        return handleResponse(json);
    }
}
