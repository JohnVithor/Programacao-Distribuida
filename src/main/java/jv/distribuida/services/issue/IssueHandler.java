package jv.distribuida.services.issue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.database.DatabaseClient;
import jv.distribuida.services.AbstractHandler;

public class IssueHandler extends AbstractHandler {
    private final DatabaseClient databaseClient;
    private final String COLLECTION = "Issue";

    public IssueHandler(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public String createHandler(JsonObject json, String user) {
        JsonElement idBoardElem = json.get("idBoard");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        if (idBoardElem != null && nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            int idBoard = idBoardElem.getAsInt();
            // TODO checar se o board existe
            String name = nameElem.getAsString();
            String description = descElem.getAsString();
            request.addProperty("idBoard", idBoard);
            request.addProperty("name", name);
            request.addProperty("description", description);
            request.addProperty("user", user);
            JsonElement response = databaseClient.save(request, COLLECTION);
            return response.toString();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("idBoard");
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
            return response.toString();
        }
    }

    @Override
    public String getHandler(JsonObject json, String user) {
        JsonElement idElem = json.get("id");
        if (idElem != null) {
            JsonElement response = databaseClient.get(idElem.getAsInt(), COLLECTION);
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
    public String updateHandler(JsonObject json, String user) {
        JsonElement idElem = json.get("id");
        JsonElement idBoardElem = json.get("idBoard");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        if (idElem != null && idBoardElem != null && nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            int id = idElem.getAsInt();
            int idBoard = idBoardElem.getAsInt();
            String name = nameElem.getAsString();
            String description = descElem.getAsString();
            request.addProperty("id", id);
            request.addProperty("idBoard", idBoard);
            request.addProperty("name", name);
            request.addProperty("description", description);
            JsonElement response = databaseClient.update(request, COLLECTION);
            return response.toString();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("id (valid)");
            fields.add("idBoard (valid)");
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
            return response.toString();
        }
    }

    @Override
    public String findHandler(JsonObject json, String user) {
        JsonElement fieldElem = json.get("field");
        JsonElement valueElem = json.get("value");
        if (fieldElem != null && valueElem != null) {
            JsonElement response = databaseClient.find(fieldElem.getAsString(), valueElem, COLLECTION);
            return response.toString();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("field (existing field)");
            fields.add("value (to compare with)");
            response.add("fields", fields);
            return response.toString();
        }
    }
}