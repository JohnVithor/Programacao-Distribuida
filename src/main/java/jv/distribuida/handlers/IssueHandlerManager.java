package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.GetClient;
import jv.distribuida.client.DatabaseClient;

import java.util.HashMap;

public class IssueHandlerManager extends BasicDBHandlerManager {
    private final GetClient getClient;

    public IssueHandlerManager(DatabaseClient databaseClient, GetClient getClient) {
        super(new HashMap<>(), databaseClient, "Issue");
        this.getClient = getClient;
        handlers.put("CREATE", this::createHandler);
        handlers.put("UPDATE", this::updateHandler);
        handlers.put("MOVE", this::moveHandler);
    }

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

    public String updateHandler(JsonObject json, String user) {
        JsonObject response;
        JsonElement idBoardElem = json.get("idBoard");
        if (idBoardElem != null) {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "The idBoard can not be updated, use the action MOVE");
            return response.toString();
        }
        JsonElement idElem = json.get("id");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
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
            fields.add("idBoard (valid)");
            fields.add("name");
            fields.add("description");
            response.add("fields", fields);
        }
        return response.toString();
    }

    public String moveHandler(JsonObject json, String user) {
        JsonObject response;
        JsonElement fromElem = json.get("from");
        JsonElement toElem = json.get("to");
        JsonElement idElem = json.get("id");
        if (idElem != null && fromElem != null && toElem != null) {
            JsonObject request = new JsonObject();
            int id = idElem.getAsInt();
            try {
                JsonObject issue = databaseClient.get(id, collection).getAsJsonObject();
                int from = fromElem.getAsInt();
                int currentBoard = issue.get("idBoard").getAsInt();
                if (currentBoard != from) {
                    response = new JsonObject();
                    response.addProperty("status", "Failure");
                    response.addProperty("message", "The issue of id: "
                            + id + "does not is on board of id: " + from + " is on board of id: " + currentBoard);
                    return response.toString();
                }
                int to = toElem.getAsInt();
                JsonObject board = getClient.get(to, user);
                if (board.get("status").getAsString().equals("Failure")) {
                    response = new JsonObject();
                    response.addProperty("status", "Failure");
                    response.addProperty("message", "The board of id: "
                            + to + "does not exist");
                } else {
                    request.addProperty("id", id);
                    request.addProperty("idBoard", to);
                    response = databaseClient.update(request, collection).getAsJsonObject();
                    response.addProperty("status", "Success");
                }
            } catch (RuntimeException e) {
                JsonObject responseEx = new JsonObject();
                responseEx.addProperty("status", "Failure");
                responseEx.addProperty("message", "The Issue of id: "
                        + id + "does not exist");
                return responseEx.toString();
            }
        } else {
            response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("id (valid)");
            fields.add("from (valid board id)");
            fields.add("to (valid board id)");
            response.add("fields", fields);
        }
        return response.toString();
    }
}