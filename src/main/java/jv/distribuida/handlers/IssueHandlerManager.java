package jv.distribuida.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.client.DatabaseClient;
import jv.distribuida.client.GetClient;

import java.util.HashMap;

public class IssueHandlerManager extends BasicDBHandlerManager {
    private final GetClient getClient;

    public IssueHandlerManager(DatabaseClient databaseClient, GetClient getClient) {
        super(new HashMap<>(), databaseClient, "Issue");
        this.getClient = getClient;
        handlers.put("CREATE", this::createHandler);
        handlers.put("UPDATE", this::updateHandler);
        handlers.put("MOVE", this::moveHandler);
        handlers.put("BYBOARD", this::findByBoardHandler);
    }

    public String createHandler(JsonObject json, String token) {
        JsonElement idBoardElem = json.get("idBoard");
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        JsonObject response;
        if (idBoardElem != null && nameElem != null && descElem != null) {
            JsonObject request = new JsonObject();
            int idBoard = idBoardElem.getAsInt();
            JsonObject board = getClient.get("Board", idBoard, token);
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
                request.addProperty("user", getUser(token));
                response = databaseClient.save(request, collection, "issue").getAsJsonObject();
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

    public String updateHandler(JsonObject json, String token) {
        JsonElement idElem = json.get("id");
        if (idElem == null) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "The field id is necessary");
            return response.toString();
        }
        JsonElement idBoardElem = json.get("idBoard");
        if (idBoardElem != null) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "Failure");
            response.addProperty("message", "The idBoard can not be updated, use the action MOVE");
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
            JsonObject response = databaseClient.update(request, collection, token).getAsJsonObject();
            response.addProperty("status", "Success");
            return response.toString();
        }
    }

    public String moveHandler(JsonObject json, String token) {
        JsonObject response;
        JsonElement fromElem = json.get("from");
        JsonElement toElem = json.get("to");
        JsonElement idElem = json.get("id");
        if (idElem != null && fromElem != null && toElem != null) {
            JsonObject request = new JsonObject();
            int id = idElem.getAsInt();
            try {
                JsonObject issue = databaseClient.get(id, collection, token).getAsJsonObject();
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
                JsonObject board = getClient.get("Board", to, getUser(token));
                if (board.get("status").getAsString().equals("Failure")) {
                    response = new JsonObject();
                    response.addProperty("status", "Failure");
                    response.addProperty("message", "The board of id: "
                            + to + "does not exist");
                } else {
                    request.addProperty("id", id);
                    request.addProperty("idBoard", to);
                    response = databaseClient.update(request, collection, token).getAsJsonObject();
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

    public String findByBoardHandler(JsonObject json, String token) {
        JsonElement idBoardElem = json.get("idBoard");
        JsonElement pageElem = json.get("page");
        JsonElement limitElem = json.get("limit");
        JsonObject response = new JsonObject();
        if (idBoardElem != null && pageElem != null && limitElem != null) {
            long page = pageElem.getAsLong();
            long limit = limitElem.getAsLong();
            response.add("data", databaseClient.find("idBoard",
                    idBoardElem, page, limit, collection, token));
            response.addProperty("status", "Success");
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("idBoard (valid board id)");
            fields.add("page (first page is 0)");
            fields.add("limit (items per page)");
            response.add("fields", fields);
        }
        return response.toString();
    }
}