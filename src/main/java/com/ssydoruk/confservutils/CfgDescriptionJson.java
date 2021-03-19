/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssydoruk.confservutils;

import com.genesyslab.platform.configuration.protocol.metadata.CfgDescription;
import com.genesyslab.platform.configuration.protocol.metadata.CfgDescriptionAttributeEnumItem;
import com.genesyslab.platform.configuration.protocol.metadata.CfgDescriptionAttributeInteger;
import com.genesyslab.platform.configuration.protocol.metadata.CfgDescriptionAttributePrimitive;
import com.genesyslab.platform.configuration.protocol.metadata.CfgDescriptionAttributeString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author stepan_sydoruk
 */
public class CfgDescriptionJson {

    Gson gson;

    private CfgDescriptionJson() {
        GsonBuilder gsonBilder = new GsonBuilder();
        gsonBilder.registerTypeHierarchyAdapter(CfgDescription.class, new CfgDescriptionAdapter());
//        gsonBilder.registerTypeAdapter(CfgDescriptionObject.class, new CfgDescriptionObjectAdapter());
//        gsonBilder.registerTypeAdapter(com.genesyslab.platform.configuration.protocol.types.CfgFlag.class, new CfgFlagObjectAdapter());
//        gsonBilder.setPrettyPrinting();
        gson = gsonBilder.create();
    }

    public static CfgDescriptionJson getInstance() {
        return NewSingleton1Holder.INSTANCE;
    }

    private static class NewSingleton1Holder {

        private static final CfgDescriptionJson INSTANCE = new CfgDescriptionJson();
    }

    public static String toJson(Object obj) {
        return CfgDescriptionJson.getInstance().gson.toJson(obj);
    }

    public static CfgDescription fromJson(JsonElement json) {
        return CfgDescriptionJson.getInstance().gson.fromJson(json, CfgDescription.class);
    }

    class CfgDescriptionAdapter implements JsonSerializer<CfgDescription>, JsonDeserializer<CfgDescription> {

        @Override
        public JsonElement serialize(CfgDescription src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("name", src.getName());
            if (src instanceof CfgDescriptionAttributePrimitive) {
                if (src instanceof CfgDescriptionAttributeString) {
                    result.add("type", new JsonPrimitive("string"));
                } else if (src instanceof CfgDescriptionAttributeEnumItem) {
                    result.add("type", new JsonPrimitive("enum"));
                    CfgDescriptionAttributeEnumItem e = (CfgDescriptionAttributeEnumItem) src;
                    JsonObject enumProperties = new JsonObject();
//                    enumProperties.add("values", context.serialize(e.));
                    enumProperties.add("type", new JsonPrimitive(e.getEnumDescription().getCfgEnumClass().getSimpleName()));
                    enumProperties.add("items", context.serialize(com.genesyslab.platform.commons.GEnum.values(e.getEnumDescription().getCfgEnumClass())));
                    result.add("properties", enumProperties);
                } else if (src instanceof CfgDescriptionAttributeInteger) {
                    result.add("type", new JsonPrimitive("int"));
                }
            } else {
                result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
            }
            return result;
        }

        @Override
        public CfgDescription deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            JsonElement element = jsonObject.get("properties");

            try {
                return context.deserialize(element, Class.forName("com.googlecode.whiteboard.model." + type));
            } catch (ClassNotFoundException cnfe) {
                throw new JsonParseException("Unknown element type: " + type, cnfe);
            }
        }
    }
}
