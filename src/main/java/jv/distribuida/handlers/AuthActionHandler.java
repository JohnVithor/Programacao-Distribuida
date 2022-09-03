package jv.distribuida.handlers;

import com.google.gson.JsonObject;

public interface AuthActionHandler {
    String execute(JsonObject json, String user);
}
