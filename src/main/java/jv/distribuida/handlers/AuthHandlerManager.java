package jv.distribuida.handlers;

import com.google.gson.*;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.network.Message;
import jv.distribuida.network.RequestHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AuthHandlerManager implements RequestHandler {

    private final static String missingAction = "{\"status\":\"Failure\",\"message\":\"The 'action' attribute was not found\"}";
    protected final DatabaseClient databaseClient;
    protected final String collection;
    protected final ActionHandler defaultHandler;
    protected final HashMap<String, ActionHandler> handlers;
    private final DateTimeFormatter formatter;

    public AuthHandlerManager(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
        this.collection = "Auth";
        this.defaultHandler = (json) -> {
            JsonElement actionElem = json.get("action");
            String action = actionElem.getAsString();
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "Action: '" + action + "' is not supported");
            return response.toString();
        };
        this.formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.handlers = new HashMap<>();
        handlers.put("LOGIN", this::loginHandler);
        handlers.put("AUTHORIZE", this::authorizeHandler);
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
            ActionHandler handler = handlers.getOrDefault(action, defaultHandler);
            message.setText(handler.execute(json));
            return message;
        } catch (JsonSyntaxException | IllegalStateException e) {
            message.setText(exceptionHandler(e.getMessage()));
            return message;
        }
    }

    public String loginHandler(JsonObject json) {
        JsonElement usernameElem = json.get("username");
        JsonElement passwordElem = json.get("password");
        JsonObject response = new JsonObject();
        if (usernameElem != null && passwordElem != null) {
            String username = usernameElem.getAsString();
            String password = passwordElem.getAsString();
            JsonArray result = databaseClient.find("username", usernameElem, 0, 1, collection, "AUTH").getAsJsonArray();
            if (!result.isEmpty()) {
                JsonObject user = result.get(0).getAsJsonObject();
                String hashpass = user.get("password").getAsString();
                // TODO: verificar password de forma decente
                if (hashpass.equals(password)) {
                    // TODO: criar um token decente
                    String now = LocalDateTime.now().format(formatter);
                    response.addProperty("token", username + "$" + password + "$" + now);
                    response.addProperty("status", "Success");
                } else {
                    response.addProperty("status", "Failure");
                    response.addProperty("message", "Invalid credentials");
                }
            } else {
                response.addProperty("status", "Failure");
                response.addProperty("message", "Invalid credentials");
            }
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("username");
            fields.add("password");
            response.add("fields", fields);
        }
        return response.toString();
    }


    public String authorizeHandler(JsonObject json) {
        JsonElement tokenElem = json.get("token");
        JsonObject response = new JsonObject();
        if (tokenElem != null) {
            String token = tokenElem.getAsString();
            // TODO: validar um token
            response.addProperty("status", "Success");
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "A token must be given");
        }
        return response.toString();
    }

    String exceptionHandler(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", message);
        return response.toString();
    }

}