package jv.distribuida.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Connection;
import jv.distribuida.network.ConnectionCreator;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.Message;

import java.io.IOException;
import java.net.InetAddress;

public class DatabaseClient {
    private final ConnectionType type;
    private final InetAddress address;
    private final int port;

    public DatabaseClient(InetAddress address, int port, ConnectionType type) {
        this.type = type;
        this.address = address;
        this.port = port;
    }

    JsonElement handleResponse(JsonObject request) {
        Connection connection = null;
        try {
            connection = ConnectionCreator.createConnection(type, address, port);
            connection.setTimeout(1000);
            connection.send(new Message(address, port, request.toString()));
            connection.setTimeout(100);
            Message message = connection.receive();
            JsonObject response = JsonParser.parseString(message.getText()).getAsJsonObject();
            if (response.get("status").getAsString().equals("Success")) {
                return response.get("data");
            } else {
                throw new RuntimeException(response.get("message").getAsString());
            }
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            throw new RuntimeException(e);
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

    public JsonElement save(JsonObject data, String collection, String token) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "CREATE");
        json.addProperty("token", token);
        json.addProperty("collection", collection);
        json.add("data", data);
        return handleResponse(json);
    }

    public JsonElement get(int id, String collection, String token) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "GET");
        json.addProperty("token", token);
        json.addProperty("collection", collection);
        json.addProperty("id", id);
        return handleResponse(json);
    }

    public JsonElement update(JsonObject new_data, String collection, String token) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "UPDATE");
        json.addProperty("token", token);
        json.addProperty("collection", collection);
        json.add("data", new_data);
        return handleResponse(json);
    }

    public JsonElement find(String field, JsonElement value, long page, long limit, String collection, String token) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "FIND");
        json.addProperty("token", token);
        json.addProperty("collection", collection);
        json.addProperty("field", field);
        json.add("value", value);
        json.addProperty("page", page);
        json.addProperty("limit", limit);
        return handleResponse(json);
    }
}
