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
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

public class JsonHelper {
	private static final Gson GSON = new GsonBuilder().create();

	public static boolean hasString(JsonObject jsonObject, String string) {
		return !hasPrimitive(jsonObject, string) ? false : jsonObject.getAsJsonPrimitive(string).isString();
	}

	@Environment(EnvType.CLIENT)
	public static boolean isString(JsonElement jsonElement) {
		return !jsonElement.isJsonPrimitive() ? false : jsonElement.getAsJsonPrimitive().isString();
	}

	public static boolean isNumber(JsonElement jsonElement) {
		return !jsonElement.isJsonPrimitive() ? false : jsonElement.getAsJsonPrimitive().isNumber();
	}

	@Environment(EnvType.CLIENT)
	public static boolean hasBoolean(JsonObject jsonObject, String string) {
		return !hasPrimitive(jsonObject, string) ? false : jsonObject.getAsJsonPrimitive(string).isBoolean();
	}

	public static boolean hasArray(JsonObject jsonObject, String string) {
		return !hasElement(jsonObject, string) ? false : jsonObject.get(string).isJsonArray();
	}

	public static boolean hasPrimitive(JsonObject jsonObject, String string) {
		return !hasElement(jsonObject, string) ? false : jsonObject.get(string).isJsonPrimitive();
	}

	public static boolean hasElement(JsonObject jsonObject, String string) {
		return jsonObject == null ? false : jsonObject.get(string) != null;
	}

	public static String asString(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsString();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a string, was " + getType(jsonElement));
		}
	}

	public static String getString(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asString(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a string");
		}
	}

	public static String getString(JsonObject jsonObject, String string, String string2) {
		return jsonObject.has(string) ? asString(jsonObject.get(string), string) : string2;
	}

	public static Item asItem(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive()) {
			String string2 = jsonElement.getAsString();
			return (Item)Registry.ITEM
				.getOrEmpty(new Identifier(string2))
				.orElseThrow(() -> new JsonSyntaxException("Expected " + string + " to be an item, was unknown string '" + string2 + "'"));
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be an item, was " + getType(jsonElement));
		}
	}

	public static Item getItem(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asItem(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find an item");
		}
	}

	public static boolean asBoolean(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsBoolean();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Boolean, was " + getType(jsonElement));
		}
	}

	public static boolean getBoolean(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asBoolean(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Boolean");
		}
	}

	public static boolean getBoolean(JsonObject jsonObject, String string, boolean bl) {
		return jsonObject.has(string) ? asBoolean(jsonObject.get(string), string) : bl;
	}

	public static float asFloat(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsFloat();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Float, was " + getType(jsonElement));
		}
	}

	public static float getFloat(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asFloat(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Float");
		}
	}

	public static float getFloat(JsonObject jsonObject, String string, float f) {
		return jsonObject.has(string) ? asFloat(jsonObject.get(string), string) : f;
	}

	public static long asLong(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsLong();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Long, was " + getType(jsonElement));
		}
	}

	public static long method_22449(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asLong(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Long");
		}
	}

	public static long getLong(JsonObject jsonObject, String string, long l) {
		return jsonObject.has(string) ? asLong(jsonObject.get(string), string) : l;
	}

	public static int asInt(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsInt();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Int, was " + getType(jsonElement));
		}
	}

	public static int getInt(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asInt(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Int");
		}
	}

	public static int getInt(JsonObject jsonObject, String string, int i) {
		return jsonObject.has(string) ? asInt(jsonObject.get(string), string) : i;
	}

	public static byte asByte(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsByte();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Byte, was " + getType(jsonElement));
		}
	}

	public static byte getByte(JsonObject jsonObject, String string, byte b) {
		return jsonObject.has(string) ? asByte(jsonObject.get(string), string) : b;
	}

	public static JsonObject asObject(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonObject()) {
			return jsonElement.getAsJsonObject();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a JsonObject, was " + getType(jsonElement));
		}
	}

	public static JsonObject getObject(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asObject(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonObject");
		}
	}

	public static JsonObject getObject(JsonObject jsonObject, String string, JsonObject jsonObject2) {
		return jsonObject.has(string) ? asObject(jsonObject.get(string), string) : jsonObject2;
	}

	public static JsonArray asArray(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonArray()) {
			return jsonElement.getAsJsonArray();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a JsonArray, was " + getType(jsonElement));
		}
	}

	public static JsonArray getArray(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return asArray(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonArray");
		}
	}

	@Nullable
	public static JsonArray getArray(JsonObject jsonObject, String string, @Nullable JsonArray jsonArray) {
		return jsonObject.has(string) ? asArray(jsonObject.get(string), string) : jsonArray;
	}

	public static <T> T deserialize(
		@Nullable JsonElement jsonElement, String string, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_
	) {
		if (jsonElement != null) {
			return jsonDeserializationContext.deserialize(jsonElement, class_);
		} else {
			throw new JsonSyntaxException("Missing " + string);
		}
	}

	public static <T> T deserialize(JsonObject jsonObject, String string, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_) {
		if (jsonObject.has(string)) {
			return deserialize(jsonObject.get(string), string, jsonDeserializationContext, class_);
		} else {
			throw new JsonSyntaxException("Missing " + string);
		}
	}

	public static <T> T deserialize(
		JsonObject jsonObject, String string, T object, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_
	) {
		return jsonObject.has(string) ? deserialize(jsonObject.get(string), string, jsonDeserializationContext, class_) : object;
	}

	public static String getType(JsonElement jsonElement) {
		String string = StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);
		if (jsonElement == null) {
			return "null (missing)";
		} else if (jsonElement.isJsonNull()) {
			return "null (json)";
		} else if (jsonElement.isJsonArray()) {
			return "an array (" + string + ")";
		} else if (jsonElement.isJsonObject()) {
			return "an object (" + string + ")";
		} else {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					return "a number (" + string + ")";
				}

				if (jsonPrimitive.isBoolean()) {
					return "a boolean (" + string + ")";
				}
			}

			return string;
		}
	}

	@Nullable
	public static <T> T deserialize(Gson gson, Reader reader, Class<T> class_, boolean bl) {
		try {
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(bl);
			return gson.<T>getAdapter(class_).read(jsonReader);
		} catch (IOException var5) {
			throw new JsonParseException(var5);
		}
	}

	@Nullable
	public static <T> T deserialize(Gson gson, Reader reader, Type type, boolean bl) {
		try {
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(bl);
			return (T)gson.getAdapter(TypeToken.get(type)).read(jsonReader);
		} catch (IOException var5) {
			throw new JsonParseException(var5);
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static <T> T deserialize(Gson gson, String string, Type type, boolean bl) {
		return deserialize(gson, new StringReader(string), type, bl);
	}

	@Nullable
	public static <T> T deserialize(Gson gson, String string, Class<T> class_, boolean bl) {
		return deserialize(gson, new StringReader(string), class_, bl);
	}

	@Nullable
	public static <T> T deserialize(Gson gson, Reader reader, Type type) {
		return deserialize(gson, reader, type, false);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static <T> T deserialize(Gson gson, String string, Type type) {
		return deserialize(gson, string, type, false);
	}

	@Nullable
	public static <T> T deserialize(Gson gson, Reader reader, Class<T> class_) {
		return deserialize(gson, reader, class_, false);
	}

	@Nullable
	public static <T> T deserialize(Gson gson, String string, Class<T> class_) {
		return deserialize(gson, string, class_, false);
	}

	public static JsonObject deserialize(String string, boolean bl) {
		return deserialize(new StringReader(string), bl);
	}

	public static JsonObject deserialize(Reader reader, boolean bl) {
		return deserialize(GSON, reader, JsonObject.class, bl);
	}

	public static JsonObject deserialize(String string) {
		return deserialize(string, false);
	}

	public static JsonObject deserialize(Reader reader) {
		return deserialize(reader, false);
	}
}
