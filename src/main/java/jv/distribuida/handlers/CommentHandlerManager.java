package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;

import java.util.HashMap;

public class CommentHandlerManager extends BasicDBHandlerManager {
    public CommentHandlerManager(DatabaseClient databaseClient) {
        super(new HashMap<>(), databaseClient, "Comment");
        handlers.put("CREATE", this::createHandler);
        handlers.put("BYISSUE", this::findByIssueHandler);
    }

    public String createHandler(JsonObject json, String token) {
        JsonElement idIssueElem = json.get("idIssue");
        JsonElement contentElem = json.get("content");
        JsonObject response;
        if (idIssueElem != null && contentElem != null) {
            int idIssue = idIssueElem.getAsInt();
            JsonObject request = new JsonObject();
            String content = contentElem.getAsString();
            request.addProperty("idIssue", idIssue);
            request.addProperty("content", content);
            request.addProperty("user", getUser(token));
            try {
                response = databaseClient.save(request, collection, "comment").getAsJsonObject();
                response.addProperty("status", "Success");
            } catch (RuntimeException e) {
                response = new JsonObject();
                response.addProperty("status", "Failure");
                response.addProperty("message", e.getMessage());
                return response.getAsString();
            }
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("idIssue");
            fields.add("content");
            response.add("fields", fields);
        }
        return response.toString();
    }

    public String findByIssueHandler(JsonObject json, String token) {
        JsonElement idIssueElem = json.get("idIssue");
        JsonElement pageElem = json.get("page");
        JsonElement limitElem = json.get("limit");
        JsonObject response = new JsonObject();
        if (idIssueElem != null && pageElem != null && limitElem != null) {
            long page = pageElem.getAsLong();
            long limit = limitElem.getAsLong();
            try {
                response.add("data", databaseClient.find("idIssue",
                        idIssueElem, page, limit, collection, token));
                response.addProperty("status", "Success");
            } catch (RuntimeException e) {
                response = new JsonObject();
                response.addProperty("status", "Failure");
                response.addProperty("message", e.getMessage());
                return response.getAsString();
            }
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("idIssue (valid issue id)");
            fields.add("page (first page is 0)");
            fields.add("limit (items per page)");
            response.add("fields", fields);
        }
        return response.toString();
    }
}