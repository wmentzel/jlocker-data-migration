package com.randomlychosenbytes.jlocker.newformat;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ModuleDeserializer<T> implements JsonDeserializer<T> {

    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public T deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive classNamePrimitive = (JsonPrimitive) jsonObject.get("type");

        String className = classNamePrimitive.getAsString();

        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }

        return (T) gson.fromJson(jsonObject, clazz);
    }
}