package jv.distribuida.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;

public class MyHandler implements RequestHandler {

    private final String missingAction;

    public MyHandler() {
        this.missingAction = "{\"status\":\"Failure\",\"Message\":\"The 'action' attribute was not found\"}";
    }

    @Override
    public Message handle(Message message) {
        try {
            JsonObject json = JsonParser.parseString(message.getText()).getAsJsonObject();
            JsonElement actionElem = json.get("action");
            if (actionElem == null) {
                message.setText(missingAction);
                return message;
            }
            String action = actionElem.getAsString();
            switch (action) {
                case "CREATE" -> message.setText(createHandler(json));
                case "UPDATE" -> message.setText(updateHandler(json));
                case "GET" -> message.setText(getHandler(json));
                case "FIND" -> message.setText(findHandler(json));
                default -> message.setText(defaultHandler(action));
            }
            return message;
        } catch (JsonSyntaxException | IllegalStateException e) {
            message.setText(exceptionHandler(e.getMessage()));
            return message;
        }
    }

    String createHandler(JsonObject json) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Success");
        return response.toString();
    }

    String updateHandler(JsonObject json) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Success");
        return response.toString();
    }

    String getHandler(JsonObject json) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Success");
        return response.toString();
    }

    String findHandler(JsonObject json) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Success");
        return response.toString();
    }

    String defaultHandler(String action) {
        JsonObject json = new JsonObject();
        json.addProperty("status", "Failure");
        json.addProperty("Message", "Action: '" + action + "' is not supported");
        return json.toString();
    }

    String exceptionHandler(String message) {
        JsonObject json = new JsonObject();
        json.addProperty("status", "Failure");
        json.addProperty("Message", message);
        return json.toString();
    }
}
