package jv.distribuida.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Connection;
import jv.distribuida.network.Message;
import jv.distribuida.network.TCPConnection;
import jv.distribuida.network.UDPConnection;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class DatabaseClient {
    private final Connection connection;

    private final InetAddress address;
    private final int port;

    public DatabaseClient(InetAddress address, int port, Connection connection) {
        this.connection = connection;
        this.address = address;
        this.port = port;
    }

    JsonElement handleResponse(JsonObject request) {
        try {
            connection.send(new Message(address, port, request.toString()));
            Message message = connection.receive();
            JsonObject response = JsonParser.parseString(message.getText()).getAsJsonObject();
            if (response.get("status").getAsString().equals("Success")){
                return response.get("data").getAsJsonObject();
            } else {
                throw new RuntimeException(response.get("message").getAsString());
            }
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonElement save(JsonObject data, String collection) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "CREATE");
        json.addProperty("token", "Board");
        json.addProperty("collection", collection);
        json.add("data", data);
        return handleResponse(json);
    }

    public JsonElement get(int id, String collection) {
        JsonObject json = new JsonObject();
        json.addProperty("action", "GET");
        json.addProperty("token", "Board");
        json.addProperty("collection", collection);
        json.addProperty("id", id);
        return handleResponse(json);
    }
//
//    public JsonObject find(String field, JsonElement value, String collection) {
//        return this.data.get(collection).values().stream().filter(v -> v.get(field).equals(value)).toList();
//    }
//
//    public JsonObject update(String id, JsonObject new_data, String collection) {
//        if (!this.data.containsKey(collection)) {
//            throw new RuntimeException("Collection: " + collection + " not found on the database");
//        }
//        HashMap<String, JsonObject> col = this.data.get(collection);
//        if (col.containsKey(id)){
//            synchronized (col.get(id)) {
//                JsonObject data = col.get(id);
//                // todo
//                return data;
//            }
//        }
//        throw new RuntimeException("ID: " + id + " not found on collection: " + collection);
//    }
}
