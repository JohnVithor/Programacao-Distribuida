package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;

import java.util.HashMap;

public class CommentHandlerManager extends BasicDBHandlerManager {
    private final GetClient getClient;

    public CommentHandlerManager(DatabaseClient databaseClient, GetClient getClient) {
        super(new HashMap<>(), databaseClient, "Comment");
        this.getClient = getClient;
        handlers.put("CREATE", this::createHandler);
        handlers.put("BYISSUE", this::findByIssueHandler);
    }

    public String createHandler(JsonObject json, String user) {
        JsonElement idIssueElem = json.get("idIssue");
        JsonElement contentElem = json.get("content");
        JsonObject response;
        if (idIssueElem != null && contentElem != null) {
            JsonObject request = new JsonObject();
            int idIssue = idIssueElem.getAsInt();
            JsonObject issue = getClient.get(idIssue, user);
            if (issue.get("status").getAsString().equals("Failure")) {
                response = new JsonObject();
                response.addProperty("status", "Failure");
                response.addProperty("message", "The issue of id: "
                        + issue + "does not exist");
            } else {
                String content = contentElem.getAsString();
                request.addProperty("idIssue", idIssue);
                request.addProperty("content", content);
                request.addProperty("user", user);
                response = databaseClient.save(request, collection).getAsJsonObject();
                response.addProperty("status", "Success");
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

    public String findByIssueHandler(JsonObject json, String user) {
        JsonElement idIssueElem = json.get("idIssue");
        JsonElement pageElem = json.get("page");
        JsonElement limitElem = json.get("limit");
        JsonObject response = new JsonObject();
        if (idIssueElem != null && pageElem != null && limitElem != null) {
            long page = pageElem.getAsLong();
            long limit = limitElem.getAsLong();
            response.add("data", databaseClient.find("idIssue",
                    idIssueElem, page, limit, collection));
            response.addProperty("status", "Success");
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