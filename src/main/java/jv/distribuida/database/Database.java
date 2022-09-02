package jv.distribuida.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private final HashMap<String, Object> locks;
    private final ConcurrentHashMap<String, ArrayList<JsonObject>> data;
    private final DateTimeFormatter formatter;

    public Database(HashMap<String, Object> collections) {
        this.locks = collections;
        this.data = new ConcurrentHashMap<>();
        for (String collection : collections.keySet()) {
            ArrayList<JsonObject> schema = new ArrayList<>();
            data.put(collection, schema);
        }
        this.formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    }

    public JsonObject save(JsonObject data, String collection) {
        check_collection(collection);
        synchronized (locks.get(collection)) {
            String time = LocalDateTime.now().format(formatter);
            data.addProperty("id", this.data.get(collection).size());
            data.addProperty("creation", time);
            data.addProperty("modification", time);
            this.data.get(collection).add(data);
            return data.deepCopy();
        }
    }

    public JsonObject get(int id, String collection) {
        check_collection(collection);
        ArrayList<JsonObject> col = this.data.get(collection);
        if (col.size() > id) {
            JsonObject r;
            synchronized (col.get(id)) {
                r = this.data.get(collection).get(id).deepCopy();
            }
            return r;
        }
        throw new RuntimeException("ID: " + id + " not found on collection: " + collection);
    }

    public JsonArray find(String field, JsonElement value, long page, long limit, String collection) {
        check_collection(collection);
        JsonArray response = new JsonArray();
        List<JsonObject> filtered = List.copyOf(this.data.get(collection))
                .stream()
                .filter(v -> v.has(field) && v.get(field).equals(value))
                .skip(page * limit)
                .limit(limit)
                .toList();
        for (JsonElement e : filtered) {
            response.add(e);
        }
        return response.deepCopy();
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
                JsonObject old_data = col.get(id);
                old_data.remove("modification");
                String time = LocalDateTime.now().format(formatter);
                old_data.addProperty("modification", time);
                for (String field :new_data.keySet()) {
                    if (old_data.has(field)) {
                        old_data.remove(field);
                    }
                    old_data.add(field, new_data.get(field));
                }
                col.set(id, old_data);
                return old_data.deepCopy();
            }
        }
        throw new RuntimeException("ID: " + id + " not found on collection: " + collection);
    }

    private void check_collection(String collection) {
        if (!this.data.containsKey(collection)) {
            throw new RuntimeException("Collection: " + collection + " not found on the database");
        }
    }
}
