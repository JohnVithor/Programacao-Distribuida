package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;

import java.util.HashMap;

public abstract class BasicDBHandlerManager extends BasicHandlerManager {

    protected final DatabaseClient databaseClient;
    protected final String collection;

    public BasicDBHandlerManager(HashMap<String, AuthActionHandler> handlers, DatabaseClient databaseClient, String collection) {
        super(handlers);
        this.databaseClient = databaseClient;
        this.collection = collection;
        handlers.put("GET", this::getHandler);
        handlers.put("FIND", this::findHandler);
    }

    public String getHandler(JsonObject json, String token) {
        JsonElement idElem = json.get("id");
        JsonObject response;
        if (idElem != null) {
            response = databaseClient.get(idElem.getAsInt(), collection, token).getAsJsonObject();
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

    public String findHandler(JsonObject json, String token) {
        JsonElement fieldElem = json.get("field");
        JsonElement valueElem = json.get("value");
        JsonElement pageElem = json.get("page");
        JsonElement limitElem = json.get("limit");
        JsonObject response = new JsonObject();
        if (fieldElem != null && valueElem != null && pageElem != null && limitElem != null) {
            String field = fieldElem.getAsString();
            long page = pageElem.getAsLong();
            long limit = limitElem.getAsLong();
            response.add("data", databaseClient.find(field, valueElem, page, limit, collection, token));
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