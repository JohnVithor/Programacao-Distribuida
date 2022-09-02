package jv.distribuida.handlers;

import com.google.gson.*;
import jv.distribuida.client.DatabaseClient;

import java.util.HashMap;

public abstract class BasicDBHandlerManager extends BasicHandlerManager {

    protected final DatabaseClient databaseClient;
    protected final String collection;

    public BasicDBHandlerManager(HashMap<String, ActionHandler> handlers, DatabaseClient databaseClient, String collection) {
        super(handlers);
        this.databaseClient = databaseClient;
        this.collection = collection;
        handlers.put("GET", this::getHandler);
        handlers.put("FIND", this::findHandler);
    }

    public String getHandler(JsonObject json, String user) {
        JsonElement idElem = json.get("id");
        JsonObject response;
        if (idElem != null) {
            response = databaseClient.get(idElem.getAsInt(), collection).getAsJsonObject();
            response.addProperty("status", "Success");
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("id");
            response.add("fields", fields);
        }
        return response.toString();
    }

    public String findHandler(JsonObject json, String user) {
        JsonElement fieldElem = json.get("field");
        JsonElement valueElem = json.get("value");
        JsonObject response = new JsonObject();
        if (fieldElem != null && valueElem != null) {
            response.add("data", databaseClient.find(fieldElem.getAsString(), valueElem, collection));
            response.addProperty("status", "Success");
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("field (existing field)");
            fields.add("value (to compare with)");
            response.add("fields", fields);
        }
        return response.toString();
    }
}