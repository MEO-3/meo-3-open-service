package org.thingai.app.meo.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static synchronized String toJson(Object src) {
        return gson.toJson(src);
    }

    public static synchronized <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static synchronized <T> T fromJsonObject(JsonObject jsonObject, Class<T> classOfT) {
        return gson.fromJson(jsonObject, classOfT);
    }
}
