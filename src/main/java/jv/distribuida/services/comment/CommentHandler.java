package jv.distribuida.services.comment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.services.AbstractHandler;

public class CommentHandler extends AbstractHandler {
    private final DatabaseClient databaseClient;
    private final String COLLECTION = "Comment";

    public CommentHandler(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public String createHandler(JsonObject json, String user) {
        JsonElement idIssueElem = json.get("idIssue");
        JsonElement contentElem = json.get("content");
        if (idIssueElem != null && contentElem != null) {
            JsonObject request = new JsonObject();
            int idIssue = idIssueElem.getAsInt();
            // TODO checar se a issue existe
            String content = contentElem.getAsString();
            request.addProperty("idIssue", idIssue);
            request.addProperty("content", content);
            request.addProperty("user", user);
            JsonElement response = databaseClient.save(request, COLLECTION);
            return response.toString();
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("idIssue");
            fields.add("content");
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
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", "Update a comment is not supported");
        return response.toString();
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