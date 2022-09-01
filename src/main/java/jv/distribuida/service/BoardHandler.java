package jv.distribuida.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.database.DatabaseClient;

public class BoardHandler extends AbstractHandler {

    private final DatabaseClient databaseClient;

    public BoardHandler(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    String createHandler(JsonObject json, String user) {
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        if (nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            String name = nameElem.getAsString();
            String description = descElem.getAsString();
            request.addProperty("name", name);
            request.addProperty("description", description);
            JsonElement response = databaseClient.save(request, "Board");
            return response.toString();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
            return response.toString();
        }
    }

    @Override
    String getHandler(JsonObject json, String user) {
        JsonElement idElem = json.get("id");
        if (idElem != null) {
            JsonElement response = databaseClient.get(idElem.getAsInt(), "Board");
            return response.toString();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("id");
            response.add("fields", fields);
            return response.toString();
        }
    }

    @Override
    String updateHandler(JsonObject json, String user) {
        return null;
    }

    @Override
    String findHandler(JsonObject json, String user) {
        return null;
    }
}
