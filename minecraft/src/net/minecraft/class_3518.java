package net.minecraft;

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
import org.apache.commons.lang3.StringUtils;

public class class_3518 {
	private static final Gson field_15657 = new GsonBuilder().create();

	public static boolean method_15289(JsonObject jsonObject, String string) {
		return !method_15278(jsonObject, string) ? false : jsonObject.getAsJsonPrimitive(string).isString();
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_15286(JsonElement jsonElement) {
		return !jsonElement.isJsonPrimitive() ? false : jsonElement.getAsJsonPrimitive().isString();
	}

	public static boolean method_15275(JsonElement jsonElement) {
		return !jsonElement.isJsonPrimitive() ? false : jsonElement.getAsJsonPrimitive().isNumber();
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_15254(JsonObject jsonObject, String string) {
		return !method_15278(jsonObject, string) ? false : jsonObject.getAsJsonPrimitive(string).isBoolean();
	}

	public static boolean method_15264(JsonObject jsonObject, String string) {
		return !method_15294(jsonObject, string) ? false : jsonObject.get(string).isJsonArray();
	}

	public static boolean method_15278(JsonObject jsonObject, String string) {
		return !method_15294(jsonObject, string) ? false : jsonObject.get(string).isJsonPrimitive();
	}

	public static boolean method_15294(JsonObject jsonObject, String string) {
		return jsonObject == null ? false : jsonObject.get(string) != null;
	}

	public static String method_15287(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsString();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a string, was " + method_15266(jsonElement));
		}
	}

	public static String method_15265(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15287(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a string");
		}
	}

	public static String method_15253(JsonObject jsonObject, String string, String string2) {
		return jsonObject.has(string) ? method_15287(jsonObject.get(string), string) : string2;
	}

	public static class_1792 method_15256(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive()) {
			String string2 = jsonElement.getAsString();
			return (class_1792)class_2378.field_11142
				.method_17966(new class_2960(string2))
				.orElseThrow(() -> new JsonSyntaxException("Expected " + string + " to be an item, was unknown string '" + string2 + "'"));
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be an item, was " + method_15266(jsonElement));
		}
	}

	public static class_1792 method_15288(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15256(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find an item");
		}
	}

	public static boolean method_15268(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsBoolean();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Boolean, was " + method_15266(jsonElement));
		}
	}

	public static boolean method_15270(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15268(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Boolean");
		}
	}

	public static boolean method_15258(JsonObject jsonObject, String string, boolean bl) {
		return jsonObject.has(string) ? method_15268(jsonObject.get(string), string) : bl;
	}

	public static float method_15269(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsFloat();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Float, was " + method_15266(jsonElement));
		}
	}

	public static float method_15259(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15269(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Float");
		}
	}

	public static float method_15277(JsonObject jsonObject, String string, float f) {
		return jsonObject.has(string) ? method_15269(jsonObject.get(string), string) : f;
	}

	public static long method_15263(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsLong();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Long, was " + method_15266(jsonElement));
		}
	}

	public static long method_15280(JsonObject jsonObject, String string, long l) {
		return jsonObject.has(string) ? method_15263(jsonObject.get(string), string) : l;
	}

	public static int method_15257(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsInt();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Int, was " + method_15266(jsonElement));
		}
	}

	public static int method_15260(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15257(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a Int");
		}
	}

	public static int method_15282(JsonObject jsonObject, String string, int i) {
		return jsonObject.has(string) ? method_15257(jsonObject.get(string), string) : i;
	}

	public static byte method_15293(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
			return jsonElement.getAsByte();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a Byte, was " + method_15266(jsonElement));
		}
	}

	public static byte method_15271(JsonObject jsonObject, String string, byte b) {
		return jsonObject.has(string) ? method_15293(jsonObject.get(string), string) : b;
	}

	public static JsonObject method_15295(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonObject()) {
			return jsonElement.getAsJsonObject();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a JsonObject, was " + method_15266(jsonElement));
		}
	}

	public static JsonObject method_15296(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15295(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonObject");
		}
	}

	public static JsonObject method_15281(JsonObject jsonObject, String string, JsonObject jsonObject2) {
		return jsonObject.has(string) ? method_15295(jsonObject.get(string), string) : jsonObject2;
	}

	public static JsonArray method_15252(JsonElement jsonElement, String string) {
		if (jsonElement.isJsonArray()) {
			return jsonElement.getAsJsonArray();
		} else {
			throw new JsonSyntaxException("Expected " + string + " to be a JsonArray, was " + method_15266(jsonElement));
		}
	}

	public static JsonArray method_15261(JsonObject jsonObject, String string) {
		if (jsonObject.has(string)) {
			return method_15252(jsonObject.get(string), string);
		} else {
			throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonArray");
		}
	}

	public static JsonArray method_15292(JsonObject jsonObject, String string, @Nullable JsonArray jsonArray) {
		return jsonObject.has(string) ? method_15252(jsonObject.get(string), string) : jsonArray;
	}

	public static <T> T method_15291(
		@Nullable JsonElement jsonElement, String string, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_
	) {
		if (jsonElement != null) {
			return jsonDeserializationContext.deserialize(jsonElement, class_);
		} else {
			throw new JsonSyntaxException("Missing " + string);
		}
	}

	public static <T> T method_15272(JsonObject jsonObject, String string, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_) {
		if (jsonObject.has(string)) {
			return method_15291(jsonObject.get(string), string, jsonDeserializationContext, class_);
		} else {
			throw new JsonSyntaxException("Missing " + string);
		}
	}

	public static <T> T method_15283(
		JsonObject jsonObject, String string, T object, JsonDeserializationContext jsonDeserializationContext, Class<? extends T> class_
	) {
		return jsonObject.has(string) ? method_15291(jsonObject.get(string), string, jsonDeserializationContext, class_) : object;
	}

	public static String method_15266(JsonElement jsonElement) {
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
	public static <T> T method_15267(Gson gson, Reader reader, Class<T> class_, boolean bl) {
		try {
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(bl);
			return gson.<T>getAdapter(class_).read(jsonReader);
		} catch (IOException var5) {
			throw new JsonParseException(var5);
		}
	}

	@Nullable
	public static <T> T method_15273(Gson gson, Reader reader, Type type, boolean bl) {
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
	public static <T> T method_15262(Gson gson, String string, Type type, boolean bl) {
		return method_15273(gson, new StringReader(string), type, bl);
	}

	@Nullable
	public static <T> T method_15279(Gson gson, String string, Class<T> class_, boolean bl) {
		return method_15267(gson, new StringReader(string), class_, bl);
	}

	@Nullable
	public static <T> T method_15297(Gson gson, Reader reader, Type type) {
		return method_15273(gson, reader, type, false);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static <T> T method_15290(Gson gson, String string, Type type) {
		return method_15262(gson, string, type, false);
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static <T> T method_15276(Gson gson, Reader reader, Class<T> class_) {
		return method_15267(gson, reader, class_, false);
	}

	@Nullable
	public static <T> T method_15284(Gson gson, String string, Class<T> class_) {
		return method_15279(gson, string, class_, false);
	}

	public static JsonObject method_15298(String string, boolean bl) {
		return method_15274(new StringReader(string), bl);
	}

	public static JsonObject method_15274(Reader reader, boolean bl) {
		return method_15267(field_15657, reader, JsonObject.class, bl);
	}

	public static JsonObject method_15285(String string) {
		return method_15298(string, false);
	}

	public static JsonObject method_15255(Reader reader) {
		return method_15274(reader, false);
	}
}
