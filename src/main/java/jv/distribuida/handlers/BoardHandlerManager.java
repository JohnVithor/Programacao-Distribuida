package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jv.distribuida.client.DatabaseClient;

import java.util.HashMap;

public class BoardHandlerManager extends BasicDBHandlerManager {

    public BoardHandlerManager(DatabaseClient databaseClient) {
        super(new HashMap<>(), databaseClient, "Board");
        handlers.put("CREATE", this::createHandler);
        handlers.put("UPDATE", this::updateHandler);
        handlers.put("BYUSER", this::findByUserHandler);
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
            response = databaseClient.save(request, collection, "board").getAsJsonObject();
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
        if (idElem == null) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "The field id is necessary");
            return response.toString();
        }
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        if (nameElem == null && descElem == null) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "At least one of the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
            return response.toString();
        } else {
            JsonObject request = new JsonObject();
            request.addProperty("id", idElem.getAsInt());
            if (nameElem != null) {
                String name = nameElem.getAsString();
                request.addProperty("name", name);
            }
            if (descElem != null) {
                String description = descElem.getAsString();
                request.addProperty("description", description);
            }
            JsonObject response = databaseClient.update(request, collection).getAsJsonObject();
            response.addProperty("status", "Success");
            return response.toString();
        }
    }

    String findByUserHandler(JsonObject json, String user) {
        JsonElement pageElem = json.get("page");
        JsonElement limitElem = json.get("limit");
        JsonObject response = new JsonObject();
        if (pageElem != null && limitElem != null) {
            long page = pageElem.getAsLong();
            long limit = limitElem.getAsLong();
            response.add("data", databaseClient.find("user",
                    new JsonPrimitive(user), page, limit, collection));
            response.addProperty("status", "Success");
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("page (first page is 0)");
            fields.add("limit (items per page)");
            response.add("fields", fields);
        }
        return response.toString();
    }
}