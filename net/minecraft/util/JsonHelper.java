/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class JsonHelper {
    private static final Gson GSON = new GsonBuilder().create();

    public static boolean hasString(JsonObject object, String element) {
        if (!JsonHelper.hasPrimitive(object, element)) {
            return false;
        }
        return object.getAsJsonPrimitive(element).isString();
    }

    public static boolean isString(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }
        return element.getAsJsonPrimitive().isString();
    }

    public static boolean hasNumber(JsonObject object, String element) {
        if (!JsonHelper.hasPrimitive(object, element)) {
            return false;
        }
        return object.getAsJsonPrimitive(element).isNumber();
    }

    public static boolean isNumber(JsonElement element) {
        if (!element.isJsonPrimitive()) {
            return false;
        }
        return element.getAsJsonPrimitive().isNumber();
    }

    public static boolean hasBoolean(JsonObject object, String element) {
        if (!JsonHelper.hasPrimitive(object, element)) {
            return false;
        }
        return object.getAsJsonPrimitive(element).isBoolean();
    }

    public static boolean isBoolean(JsonElement object) {
        if (!object.isJsonPrimitive()) {
            return false;
        }
        return object.getAsJsonPrimitive().isBoolean();
    }

    public static boolean hasArray(JsonObject object, String element) {
        if (!JsonHelper.hasElement(object, element)) {
            return false;
        }
        return object.get(element).isJsonArray();
    }

    public static boolean hasJsonObject(JsonObject object, String element) {
        if (!JsonHelper.hasElement(object, element)) {
            return false;
        }
        return object.get(element).isJsonObject();
    }

    public static boolean hasPrimitive(JsonObject object, String element) {
        if (!JsonHelper.hasElement(object, element)) {
            return false;
        }
        return object.get(element).isJsonPrimitive();
    }

    public static boolean hasElement(JsonObject object, String lement) {
        if (object == null) {
            return false;
        }
        return object.get(lement) != null;
    }

    public static String asString(JsonElement element, String name) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a string, was " + JsonHelper.getType(element));
    }

    public static String getString(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asString(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a string");
    }

    public static String getString(JsonObject object, String element, String defaultStr) {
        if (object.has(element)) {
            return JsonHelper.asString(object.get(element), element);
        }
        return defaultStr;
    }

    public static Item asItem(JsonElement element, String name) {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return Registry.ITEM.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Expected " + name + " to be an item, was unknown string '" + string + "'"));
        }
        throw new JsonSyntaxException("Expected " + name + " to be an item, was " + JsonHelper.getType(element));
    }

    public static Item getItem(JsonObject object, String key) {
        if (object.has(key)) {
            return JsonHelper.asItem(object.get(key), key);
        }
        throw new JsonSyntaxException("Missing " + key + ", expected to find an item");
    }

    public static Item getItem(JsonObject object, String key, Item defaultItem) {
        if (object.has(key)) {
            return JsonHelper.asItem(object.get(key), key);
        }
        return defaultItem;
    }

    public static boolean asBoolean(JsonElement element, String name) {
        if (element.isJsonPrimitive()) {
            return element.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Boolean, was " + JsonHelper.getType(element));
    }

    public static boolean getBoolean(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asBoolean(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Boolean");
    }

    public static boolean getBoolean(JsonObject object, String element, boolean defaultBoolean) {
        if (object.has(element)) {
            return JsonHelper.asBoolean(object.get(element), element);
        }
        return defaultBoolean;
    }

    public static double asDouble(JsonElement object, String name) {
        if (object.isJsonPrimitive() && object.getAsJsonPrimitive().isNumber()) {
            return object.getAsDouble();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Double, was " + JsonHelper.getType(object));
    }

    public static double getDouble(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asDouble(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Double");
    }

    public static double getDouble(JsonObject object, String element, double defaultDouble) {
        if (object.has(element)) {
            return JsonHelper.asDouble(object.get(element), element);
        }
        return defaultDouble;
    }

    public static float asFloat(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Float, was " + JsonHelper.getType(element));
    }

    public static float getFloat(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asFloat(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Float");
    }

    public static float getFloat(JsonObject object, String element, float defaultFloat) {
        if (object.has(element)) {
            return JsonHelper.asFloat(object.get(element), element);
        }
        return defaultFloat;
    }

    public static long asLong(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsLong();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Long, was " + JsonHelper.getType(element));
    }

    public static long getLong(JsonObject object, String name) {
        if (object.has(name)) {
            return JsonHelper.asLong(object.get(name), name);
        }
        throw new JsonSyntaxException("Missing " + name + ", expected to find a Long");
    }

    public static long getLong(JsonObject object, String element, long defaultLong) {
        if (object.has(element)) {
            return JsonHelper.asLong(object.get(element), element);
        }
        return defaultLong;
    }

    public static int asInt(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Int, was " + JsonHelper.getType(element));
    }

    public static int getInt(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asInt(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Int");
    }

    public static int getInt(JsonObject object, String element, int defaultInt) {
        if (object.has(element)) {
            return JsonHelper.asInt(object.get(element), element);
        }
        return defaultInt;
    }

    public static byte asByte(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsByte();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Byte, was " + JsonHelper.getType(element));
    }

    public static byte getByte(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asByte(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Byte");
    }

    public static byte getByte(JsonObject object, String element, byte defaultByte) {
        if (object.has(element)) {
            return JsonHelper.asByte(object.get(element), element);
        }
        return defaultByte;
    }

    public static char asChar(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsCharacter();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Character, was " + JsonHelper.getType(element));
    }

    public static char getChar(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asChar(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Character");
    }

    public static char getChar(JsonObject object, String element, char defaultChar) {
        if (object.has(element)) {
            return JsonHelper.asChar(object.get(element), element);
        }
        return defaultChar;
    }

    public static BigDecimal asBigDecimal(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsBigDecimal();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a BigDecimal, was " + JsonHelper.getType(element));
    }

    public static BigDecimal getBigDecimal(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asBigDecimal(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a BigDecimal");
    }

    public static BigDecimal getBigDecimal(JsonObject object, String element, BigDecimal defaultBigDecimal) {
        if (object.has(element)) {
            return JsonHelper.asBigDecimal(object.get(element), element);
        }
        return defaultBigDecimal;
    }

    public static BigInteger asBigInteger(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsBigInteger();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a BigInteger, was " + JsonHelper.getType(element));
    }

    public static BigInteger getBigInteger(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asBigInteger(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a BigInteger");
    }

    public static BigInteger getBigInteger(JsonObject object, String element, BigInteger defaultBigInteger) {
        if (object.has(element)) {
            return JsonHelper.asBigInteger(object.get(element), element);
        }
        return defaultBigInteger;
    }

    public static short asShort(JsonElement element, String name) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsShort();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Short, was " + JsonHelper.getType(element));
    }

    public static short getShort(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asShort(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a Short");
    }

    public static short getShort(JsonObject object, String element, short defaultShort) {
        if (object.has(element)) {
            return JsonHelper.asShort(object.get(element), element);
        }
        return defaultShort;
    }

    public static JsonObject asObject(JsonElement element, String name) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonObject, was " + JsonHelper.getType(element));
    }

    public static JsonObject getObject(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asObject(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonObject");
    }

    public static JsonObject getObject(JsonObject object, String element, JsonObject defaultObject) {
        if (object.has(element)) {
            return JsonHelper.asObject(object.get(element), element);
        }
        return defaultObject;
    }

    public static JsonArray asArray(JsonElement element, String name) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray, was " + JsonHelper.getType(element));
    }

    public static JsonArray getArray(JsonObject object, String element) {
        if (object.has(element)) {
            return JsonHelper.asArray(object.get(element), element);
        }
        throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonArray");
    }

    @Nullable
    public static JsonArray getArray(JsonObject object, String name, @Nullable JsonArray defaultArray) {
        if (object.has(name)) {
            return JsonHelper.asArray(object.get(name), name);
        }
        return defaultArray;
    }

    public static <T> T deserialize(@Nullable JsonElement element, String name, JsonDeserializationContext context, Class<? extends T> type) {
        if (element != null) {
            return context.deserialize(element, type);
        }
        throw new JsonSyntaxException("Missing " + name);
    }

    public static <T> T deserialize(JsonObject object, String element, JsonDeserializationContext context, Class<? extends T> type) {
        if (object.has(element)) {
            return JsonHelper.deserialize(object.get(element), element, context, type);
        }
        throw new JsonSyntaxException("Missing " + element);
    }

    public static <T> T deserialize(JsonObject object, String element, T defaultValue, JsonDeserializationContext context, Class<? extends T> type) {
        if (object.has(element)) {
            return JsonHelper.deserialize(object.get(element), element, context, type);
        }
        return defaultValue;
    }

    public static String getType(JsonElement element) {
        String string = StringUtils.abbreviateMiddle(String.valueOf(element), "...", 10);
        if (element == null) {
            return "null (missing)";
        }
        if (element.isJsonNull()) {
            return "null (json)";
        }
        if (element.isJsonArray()) {
            return "an array (" + string + ")";
        }
        if (element.isJsonObject()) {
            return "an object (" + string + ")";
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
                return "a number (" + string + ")";
            }
            if (jsonPrimitive.isBoolean()) {
                return "a boolean (" + string + ")";
            }
        }
        return string;
    }

    @Nullable
    public static <T> T deserialize(Gson gson, Reader reader, Class<T> type, boolean lenient) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(lenient);
            return gson.getAdapter(type).read(jsonReader);
        } catch (IOException iOException) {
            throw new JsonParseException(iOException);
        }
    }

    @Nullable
    public static <T> T deserialize(Gson gson, Reader reader, TypeToken<T> typeToken, boolean lenient) {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(lenient);
            return gson.getAdapter(typeToken).read(jsonReader);
        } catch (IOException iOException) {
            throw new JsonParseException(iOException);
        }
    }

    @Nullable
    public static <T> T deserialize(Gson gson, String content, TypeToken<T> typeToken, boolean lenient) {
        return JsonHelper.deserialize(gson, (Reader)new StringReader(content), typeToken, lenient);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, String content, Class<T> class_, boolean lenient) {
        return JsonHelper.deserialize(gson, (Reader)new StringReader(content), class_, lenient);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, Reader reader, TypeToken<T> typeToken) {
        return JsonHelper.deserialize(gson, reader, typeToken, false);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, String content, TypeToken<T> typeToken) {
        return JsonHelper.deserialize(gson, content, typeToken, false);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, Reader reader, Class<T> class_) {
        return JsonHelper.deserialize(gson, reader, class_, false);
    }

    @Nullable
    public static <T> T deserialize(Gson gson, String content, Class<T> class_) {
        return JsonHelper.deserialize(gson, content, class_, false);
    }

    public static JsonObject deserialize(String content, boolean lenient) {
        return JsonHelper.deserialize(new StringReader(content), lenient);
    }

    public static JsonObject deserialize(Reader reader, boolean lenient) {
        return JsonHelper.deserialize(GSON, reader, JsonObject.class, lenient);
    }

    public static JsonObject deserialize(String content) {
        return JsonHelper.deserialize(content, false);
    }

    public static JsonObject deserialize(Reader reader) {
        return JsonHelper.deserialize(reader, false);
    }
}

