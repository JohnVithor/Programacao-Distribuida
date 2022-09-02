package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;

import java.util.HashMap;

public class BoardHandlerManager extends BasicDBHandlerManager {

    public BoardHandlerManager(DatabaseClient databaseClient) {
        super(new HashMap<>(), databaseClient, "Board");
        handlers.put("CREATE", this::createHandler);
        handlers.put("UPDATE", this::updateHandler);
    }

    public String createHandler(JsonObject json, String user) {
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        JsonObject response;
        if (nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            String name = nameElem.getAsString();
            String description = descElem.getAsString();
            request.addProperty("name", name);
            request.addProperty("description", description);
            request.addProperty("user", user);
            response = databaseClient.save(request, collection).getAsJsonObject();
            response.addProperty("status", "Success");
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
        }
        return response.toString();
    }

    public String updateHandler(JsonObject json, String user) {
        JsonElement idElem = json.get("id");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        JsonObject response;
        if (idElem != null && nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            int id = idElem.getAsInt();
            String name = nameElem.getAsString();
            String description = descElem.getAsString();
            request.addProperty("id", id);
            request.addProperty("name", name);
            request.addProperty("description", description);
            response = databaseClient.update(request, collection).getAsJsonObject();
            response.addProperty("status", "Success");
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("id (valid)");
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
        }
        return response.toString();
    }
}