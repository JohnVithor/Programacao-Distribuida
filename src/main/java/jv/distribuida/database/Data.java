package jv.distribuida.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.HashMap;

public class Data implements Serializable {
    private String collection;
    private String id;
    private JsonObject data;

    public Data(String collection, String id, JsonObject data) {
        this.collection = collection;
        this.id = id;
        this.data = data;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}