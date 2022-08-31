package jv.distribuida.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;

public class MyHandler implements RequestHandler {

    @Override
    public Message handle(Message message) {
        try {
            JsonObject json = JsonParser.parseString(message.getText()).getAsJsonObject();
            String action = json.get("action").getAsString();
            System.out.println(action);
            message.setText("OK-"+action);
            return message;
        }catch (JsonSyntaxException | IllegalStateException e) {
            message.setText(e.getMessage());
            return message;
        }
    }
}
