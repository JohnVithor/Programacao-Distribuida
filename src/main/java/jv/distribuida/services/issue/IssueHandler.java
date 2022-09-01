package jv.distribuida.services.issue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.GetClient;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.services.AbstractDBHandler;

public class IssueHandler extends AbstractDBHandler {
    private final GetClient getClient;

    public IssueHandler(DatabaseClient databaseClient, GetClient getClient) {
        super(databaseClient, "Issue");
        this.getClient = getClient;
    }

    @Override
    public String createHandler(JsonObject json, String user) {
        JsonElement idBoardElem = json.get("idBoard");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        JsonObject response;
        if (idBoardElem != null && nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            int idBoard = idBoardElem.getAsInt();
            JsonObject board = getClient.get(idBoard, user);
            if (board.get("status").getAsString().equals("Failure")) {
                response = new JsonObject();
                response.addProperty("status", "Failure");
                response.addProperty("message", "The board of id: "
                        + idBoard + "does not exist");
            } else {
                String name = nameElem.getAsString();
                String description = descElem.getAsString();
                request.addProperty("idBoard", idBoard);
                request.addProperty("name", name);
                request.addProperty("description", description);
                request.addProperty("user", user);
                response = databaseClient.save(request, collection).getAsJsonObject();
                response.addProperty("status", "Success");
            }
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("idBoard");
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
        }
        return response.toString();
    }

    @Override
    public String updateHandler(JsonObject json, String user) {
        JsonElement idElem = json.get("id");
        JsonElement idBoardElem = json.get("idBoard");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        JsonObject response;
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
            response = databaseClient.update(request, collection).getAsJsonObject();
            response.addProperty("status", "Success");
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("id (valid)");
            fields.add("idBoard (valid)");
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
        }
        return response.toString();
    }
}