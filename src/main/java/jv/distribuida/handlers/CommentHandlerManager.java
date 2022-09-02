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
        handlers.put("CREATE", this::createHandler);
        this.getClient = getClient;
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
}