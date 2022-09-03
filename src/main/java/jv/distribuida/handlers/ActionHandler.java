package jv.distribuida.handlers;

import com.google.gson.JsonObject;

public interface ActionHandler {
    String execute(JsonObject json);
}
