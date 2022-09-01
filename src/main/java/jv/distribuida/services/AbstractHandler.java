package jv.distribuida.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;

public abstract class AbstractHandler implements RequestHandler {
    private final String missingAction;
    private final String missingToken;

    public AbstractHandler() {
        this.missingAction = "{\"status\":\"Failure\",\"message\":\"The 'action' attribute was not found\"}";
        this.missingToken = "{\"status\":\"Failure\",\"message\":\"The 'token' attribute was not found\"}";
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
            JsonElement userElem = json.get("token");
            if (userElem == null) {
                message.setText(missingToken);
                return message;
            }
            String action = actionElem.getAsString();
            String user = userElem.getAsString();
            switch (action) {
                case "CREATE" -> message.setText(createHandler(json, user));
                case "UPDATE" -> message.setText(updateHandler(json, user));
                case "GET" -> message.setText(getHandler(json, user));
                case "FIND" -> message.setText(findHandler(json, user));
                default -> message.setText(defaultHandler(action));
            }
            return message;
        } catch (JsonSyntaxException | IllegalStateException e) {
            message.setText(exceptionHandler(e.getMessage()));
            return message;
        }
    }

    public  abstract String createHandler(JsonObject json, String user);

    public abstract String updateHandler(JsonObject json, String user);

    public abstract String getHandler(JsonObject json, String user);

    public abstract String findHandler(JsonObject json, String user);

    String defaultHandler(String action) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", "Action: '" + action + "' is not supported");
        return response.toString();
    }

    String exceptionHandler(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", message);
        return response.toString();
    }
}