package jv.distribuida.services;

import com.google.gson.*;
import jv.distribuida.client.DatabaseClient;

public abstract class AbstractDBHandler extends AbstractHandler {

    protected final DatabaseClient databaseClient;
    protected final String collection;

    public AbstractDBHandler(DatabaseClient databaseClient, String collection) {
        this.databaseClient = databaseClient;
        this.collection = collection;
    }

    public abstract String createHandler(JsonObject json, String user);

    public abstract String updateHandler(JsonObject json, String user);

    @Override
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

    @Override
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