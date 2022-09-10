package jv.distribuida.loadbalancer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

public class ServiceInstance implements Serializable {
    private final static String heartbeat = "{\"heartbeat\":\"Ok?\"}";
    private final static String heartbeatR = "{\"heartbeat\":true}";
    private final InetAddress address;
    private final int port;
    private final int heartbeatPort;
    private final ConnectionType type;
//    private final Connection connection;
//    private final Connection hbconnection;

    public ServiceInstance(InetAddress address, int port, int heartbeatPort, ConnectionType type) throws IOException {
        this.address = address;
        this.port = port;
        this.heartbeatPort = heartbeatPort;
        this.type = type;
//        this.connection = ConnectionCreator.createConnection(type, address, port);
//        this.connection.setTimeout(1000);
//        this.hbconnection = ConnectionCreator.createConnection(type, address, port);
//        this.hbconnection.setTimeout(1000);
    }

    public static void startHeartBeat(final Connection hbconnection) {
        Thread.ofVirtual().start(() -> {
            while (true) {
                Message message = null;
                try {
                    message = hbconnection.receive();
                    message.setText(heartbeatR);
                    hbconnection.send(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public JsonObject redirect(JsonObject json) throws IOException {
        Connection connection = ConnectionCreator.createConnection(type, address, port);
        connection.setTimeout(1000);
        connection.send(new Message(address, port, json.toString()));
        try {
            return JsonParser.parseString(connection.receive().getText()).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            return exceptionHandler(e.getMessage());
        } finally {
            connection.close();
        }
    }

    public boolean heartbeat() {
        Connection hbconnection = null;
        try {
            hbconnection = ConnectionCreator.createConnection(type, address, heartbeatPort);
            hbconnection.setTimeout(1000);
            hbconnection.send(new Message(address, heartbeatPort, heartbeat));
            JsonObject response = JsonParser.parseString(hbconnection.receive().getText()).getAsJsonObject();
            JsonElement heartElem = response.get("heartbeat");
            if (heartElem != null) {
                return heartElem.getAsBoolean();
            } else {
                return false;
            }
        } catch (JsonSyntaxException | IllegalStateException | IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                if (hbconnection != null) {
                    hbconnection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
