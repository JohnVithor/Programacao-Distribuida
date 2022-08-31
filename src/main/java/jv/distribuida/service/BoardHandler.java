package jv.distribuida.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BoardHandler extends AbstractHandler {
    @Override
    String createHandler(JsonObject json, String user) {
        JsonElement nameElem = json.get("name");
        JsonElement descElem = json.get("description");
        JsonObject response = new JsonObject();
        response.addProperty("status", "Success");
        return response.toString();
    }

    @Override
    String updateHandler(JsonObject json, String user) {
        return null;
    }

    @Override
    String getHandler(JsonObject json, String user) {
        return null;
    }

    @Override
    String findHandler(JsonObject json, String user) {
        return null;
    }
}
