package jv.distribuida.loadbalancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Connection;
import jv.distribuida.network.ConnectionCreator;
import jv.distribuida.network.ConnectionType;
import jv.distribuida.network.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

public class ServiceInstance implements Serializable {
    private final InetAddress address;
    private final int port;
    private final Connection connection;

    private final static String heartbeat = "{\"heartbeat\":\"Ok?\"}";

    public ServiceInstance(InetAddress address, int port, ConnectionType type) throws IOException {
        this.address = address;
        this.port = port;
        this.connection = ConnectionCreator.createConnection(type, address, port);
    }

    public JsonObject redirect(JsonObject json) throws IOException {
        synchronized (connection) {
            connection.send(new Message(address, port, json.getAsString()));
            try {
                return JsonParser.parseString(connection.receive().getText()).getAsJsonObject();
            } catch (JsonSyntaxException | IllegalStateException | IOException e) {
                return exceptionHandler(e.getMessage());
            }
        }
    }

    public boolean heartbeat() {
        synchronized (connection) {
            try {
                connection.send(new Message(address, port, heartbeat));
                JsonObject response = JsonParser.parseString(connection.receive().getText()).getAsJsonObject();
                JsonElement heartElem = response.get("heartbeat");
                if (heartElem != null) {
                    return heartElem.getAsBoolean();
                } else {
                    return false;
                }
            } catch (JsonSyntaxException | IllegalStateException | IOException e) {
                return false;
            }
        }
    }

    JsonObject exceptionHandler(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", message);
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceInstance instance = (ServiceInstance) o;

        if (port != instance.port) return false;
        return address.equals(instance.address);
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + port;
        return result;
    }
}
