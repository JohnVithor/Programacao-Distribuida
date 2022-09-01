package jv.distribuida.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jv.distribuida.database.Data;
import jv.distribuida.database.Database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class DatabaseHandler extends AbstractHandler {
    private final Database database;
    private ByteArrayOutputStream bos;
    private ObjectOutputStream oos;

    public DatabaseHandler(Database database) {
        this.database = database;
        try{
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
//    private String generate_id(JsonObject json) throws IOException {
//        byte[] data_bytes = json.toString().getBytes(StandardCharsets.UTF_8);
//        oos.writeObject(LocalDateTime.now());
//        oos.flush();
//        byte[] now_bytes = bos.toByteArray();
//        byte[] allByteArray = new byte[now_bytes.length + data_bytes.length];
//        ByteBuffer buff = ByteBuffer.wrap(allByteArray);
//        buff.put(now_bytes);
//        buff.put(data_bytes);
//        return buff.array().toString();
//    }
    @Override
    String createHandler(JsonObject json, String user) {
        JsonObject response = new JsonObject();
        JsonElement collectionElem = json.get("collection");
        JsonElement dataElem = json.get("data");
        if (collectionElem != null && dataElem != null) {
            String collection = collectionElem.getAsString();
            JsonObject data = dataElem.getAsJsonObject();
//            try {
//                String id = generate_id(data);
//                data.addProperty("id", id);
            database.save(data, collection);
//            } catch (IOException e) {
//                response.addProperty("status", "Failure");
//                response.addProperty("message", e.getMessage());
//                return response.toString();
//            }
            response.addProperty("status", "Success");
            response.add("data", data);
            response.addProperty("collection", collection);
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("collection");
            fields.add("data");
            response.add("fields", fields);
        }
        return response.toString();
    }

    @Override
    String getHandler(JsonObject json, String user) {
        JsonObject response = new JsonObject();
        JsonElement collectionElem = json.get("collection");
        JsonElement idElem = json.get("id");
        if (collectionElem != null && idElem != null) {
            String collection = collectionElem.getAsString();
            int id = idElem.getAsInt();
            JsonObject data = database.get(id, collection);
            response.addProperty("status", "Success");
            response.add("data", data);
            response.addProperty("collection", collection);
        } else {
            response.addProperty("status", "Failure");
            response.addProperty("message", "All the listed fields are needed");
            JsonArray fields = new JsonArray();
            fields.add("collection");
            fields.add("data (should have valid id field)");
            response.add("fields", fields);
        }
        return response.toString();
    }

    @Override
    String updateHandler(JsonObject json, String user) {
        return null;
    }

    @Override
    String findHandler(JsonObject json, String user) {
        return null;
    }
}
