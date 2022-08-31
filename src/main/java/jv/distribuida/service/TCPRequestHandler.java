package jv.distribuida.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jv.distribuida.network.TCPConnection;

import java.io.IOException;

public class TCPRequestHandler implements Runnable {

    private final TCPConnection connection;
    public TCPRequestHandler(TCPConnection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            String message = connection.receive();
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String action = json.get("action").getAsString();
            connection.send("OK-"+action);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
