package jv.distribuida.database;

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
        for (String collection:collections.keySet()) {
            ArrayList<JsonObject> schema = new ArrayList<>();
            data.put(collection, schema);
        }
    }

//    public void save(String id, JsonObject data, String collection) {
//        synchronized (locks.get(collection)) {
//            this.data.get(collection).put(id, data);
//        }
//    }

    public void save(JsonObject data, String collection) {
        synchronized (locks.get(collection)) {
            data.addProperty("id", this.data.get(collection).size());
            this.data.get(collection).add(data);
        }
    }

    public JsonObject get(int id, String collection) {
        return this.data.get(collection).get(id);
    }

    public List<JsonObject> find(String field, JsonElement value, String collection) {
        return this.data.get(collection).stream().filter(v -> v.get(field).equals(value)).toList();
    }

    public JsonObject update(int id, JsonObject new_data, String collection) {
        if (!this.data.containsKey(collection)) {
            throw new RuntimeException("Collection: " + collection + " not found on the database");
        }
        ArrayList<JsonObject> col = this.data.get(collection);
        if (col.size() > id){
            synchronized (col.get(id)) {
                JsonObject data = col.get(id);
                // todo
                return data;
            }
        }
        throw new RuntimeException("ID: " + id + " not found on collection: " + collection);
    }
}
