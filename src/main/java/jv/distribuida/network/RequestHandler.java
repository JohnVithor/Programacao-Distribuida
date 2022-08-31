package jv.distribuida.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jv.distribuida.network.Connection;
import jv.distribuida.network.Message;

import java.io.IOException;

public interface RequestHandler {

    Message handle(Message message) throws IOException;
}
