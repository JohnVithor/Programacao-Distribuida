package jv.distribuida.services.comment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;
import jv.distribuida.services.AbstractDBHandler;
import jv.distribuida.services.AbstractHandler;

public class CommentHandler extends AbstractDBHandler {
    private final GetClient getClient;

    public CommentHandler(DatabaseClient databaseClient, GetClient getClient) {
        super(databaseClient, "Comment");
        this.getClient = getClient;
    }

    @Override
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

    @Override
    public String updateHandler(JsonObject json, String user) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "Failure");
        response.addProperty("message", "Update a comment is not supported");
        return response.toString();
    }
}