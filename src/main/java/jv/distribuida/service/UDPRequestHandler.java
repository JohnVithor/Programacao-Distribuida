package jv.distribuida.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jv.distribuida.network.Message;
import jv.distribuida.network.UDPConnection;

import java.io.IOException;

public class UDPRequestHandler implements Runnable {

    private final UDPConnection connection;
    private final Message message;
    public UDPRequestHandler(UDPConnection connection, Message message) {
        this.connection = connection;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            JsonObject json = JsonParser.parseString(message.getText()).getAsJsonObject();
            String action = json.get("action").getAsString();
            System.out.println(action);
            message.setText("OK-"+action);
            System.out.println(message.getPort());
            connection.send(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
