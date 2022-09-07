package jv.distribuida.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;

import java.util.HashMap;

public abstract class BasicHandlerManager implements RequestHandler {
    private final static String missingAction = "{\"status\":\"Failure\",\"message\":\"The 'action' attribute was not found\"}";
    private final static String missingToken = "{\"status\":\"Failure\",\"message\":\"The 'token' attribute was not found\"}";
    private final static String heartbeat = "{\"heartbeat\":true}";

    protected final HashMap<String, AuthActionHandler> handlers;
    protected final AuthActionHandler defaultHandler;

    public BasicHandlerManager(HashMap<String, AuthActionHandler> handlers) {
        this.handlers = handlers;
        this.defaultHandler = (json, token) -> {
            JsonElement actionElem = json.get("action");
            String action = actionElem.getAsString();
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "Action: '" + action + "' is not supported");
            return response.toString();
        };
    }

    @Override
    public Message handle(Message message) {
        try {
            JsonObject json = JsonParser.parseString(message.getText()).getAsJsonObject();
            JsonElement hbElem = json.get("heartbeat");
            if (hbElem != null) {
                message.setText(heartbeat);
                return message;
            }
            JsonElement actionElem = json.get("action");
            if (actionElem == null) {
                message.setText(missingAction);
                return message;
            }
            JsonElement tokenElem = json.get("token");
            if (tokenElem == null) {
                message.setText(missingToken);
                return message;
            }
            String action = actionElem.getAsString();
            String token = tokenElem.getAsString();
            AuthActionHandler handler = handlers.getOrDefault(action, defaultHandler);
            message.setText(handler.execute(json, token));
            return message;
        } catch (JsonSyntaxException | IllegalStateException e) {
            message.setText(exceptionHandler(e.getMessage()));
            return message;
        }
    }

    public String exceptionHandler(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", message);
        return response.toString();
    }

    public String getUser(String token) {
        //TODO:
        return token;
    }
}