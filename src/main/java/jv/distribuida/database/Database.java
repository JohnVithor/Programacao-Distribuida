package jv.distribuida.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {
    private final HashMap<String, Object> locks;
    private final HashMap<String, ArrayList<JsonObject>> data;

    public Database(HashMap<String, Object> collections) {
        this.locks = collections;
        this.data = new HashMap<>();
        for (String collection : collections.keySet()) {
            ArrayList<JsonObject> schema = new ArrayList<>();
            data.put(collection, schema);
        }
    }

    public void save(JsonObject data, String collection) {
        check_collection(collection);
        synchronized (locks.get(collection)) {
            data.addProperty("id", this.data.get(collection).size());
            this.data.get(collection).add(data);
        }
    }

    public JsonObject get(int id, String collection) {
        check_collection(collection);
        return this.data.get(collection).get(id);
    }

    public JsonArray find(String field, JsonElement value, String collection) {
        check_collection(collection);
        JsonArray response = new JsonArray();
        List<JsonObject> filtered = this.data.get(collection)
                .stream().filter(v -> v.get(field).equals(value)).toList();
        for (JsonElement e : filtered) {
            response.add(e);
        }
        return response;
    }

    public JsonObject update(JsonObject new_data, String collection) {
        JsonElement idElem = new_data.get("id");
        if (idElem == null) {
            throw new RuntimeException("Field 'id' was not found - Update not possible");
        }
        int id = idElem.getAsInt();
        check_collection(collection);
        ArrayList<JsonObject> col = this.data.get(collection);
        if (col.size() > id) {
            synchronized (col.get(id)) {
                col.set(id, new_data);
            }
            return new_data;
        }
        throw new RuntimeException("ID: " + id + " not found on collection: " + collection);
    }

    private void check_collection(String collection) {
        if (!this.data.containsKey(collection)) {
            throw new RuntimeException("Collection: " + collection + " not found on the database");
        }
    }
}
