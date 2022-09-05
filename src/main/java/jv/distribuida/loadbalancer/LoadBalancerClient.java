package jv.distribuida.loadbalancer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Connection;
import jv.distribuida.network.Message;

import java.io.IOException;
import java.net.InetAddress;

public class LoadBalancerClient {
    private final Connection connection;
    private final InetAddress address;
    private final int port;

    public LoadBalancerClient(InetAddress address, int port, Connection connection) {
        this.connection = connection;
        this.address = address;
        this.port = port;
    }

    JsonObject handleResponse(JsonObject request) {
        try {
            connection.send(new Message(address, port, request.toString()));
            Message message = connection.receive();
            return JsonParser.parseString(message.getText()).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject register(String service, String address, int port, boolean auth) {
        JsonObject json = new JsonObject();
        json.addProperty("service", service);
        json.addProperty("address", address);
        json.addProperty("port", port);
        json.addProperty("auth", auth);
        return handleResponse(json);
    }
}
