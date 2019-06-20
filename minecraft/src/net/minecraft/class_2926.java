package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;

public class class_2926 {
	private class_2561 field_13284;
	private class_2926.class_2927 field_13285;
	private class_2926.class_2930 field_13286;
	private String field_13283;

	public class_2561 method_12680() {
		return this.field_13284;
	}

	public void method_12684(class_2561 arg) {
		this.field_13284 = arg;
	}

	public class_2926.class_2927 method_12682() {
		return this.field_13285;
	}

	public void method_12681(class_2926.class_2927 arg) {
		this.field_13285 = arg;
	}

	public class_2926.class_2930 method_12683() {
		return this.field_13286;
	}

	public void method_12679(class_2926.class_2930 arg) {
		this.field_13286 = arg;
	}

	public void method_12677(String string) {
		this.field_13283 = string;
	}

	public String method_12678() {
		return this.field_13283;
	}

	public static class class_2927 {
		private final int field_13289;
		private final int field_13288;
		private GameProfile[] field_13287;

		public class_2927(int i, int j) {
			this.field_13289 = i;
			this.field_13288 = j;
		}

		public int method_12687() {
			return this.field_13289;
		}

		public int method_12688() {
			return this.field_13288;
		}

		public GameProfile[] method_12685() {
			return this.field_13287;
		}

		public void method_12686(GameProfile[] gameProfiles) {
			this.field_13287 = gameProfiles;
		}

		public static class class_2928 implements JsonDeserializer<class_2926.class_2927>, JsonSerializer<class_2926.class_2927> {
			public class_2926.class_2927 method_12689(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				JsonObject jsonObject = class_3518.method_15295(jsonElement, "players");
				class_2926.class_2927 lv = new class_2926.class_2927(class_3518.method_15260(jsonObject, "max"), class_3518.method_15260(jsonObject, "online"));
				if (class_3518.method_15264(jsonObject, "sample")) {
					JsonArray jsonArray = class_3518.method_15261(jsonObject, "sample");
					if (jsonArray.size() > 0) {
						GameProfile[] gameProfiles = new GameProfile[jsonArray.size()];

						for (int i = 0; i < gameProfiles.length; i++) {
							JsonObject jsonObject2 = class_3518.method_15295(jsonArray.get(i), "player[" + i + "]");
							String string = class_3518.method_15265(jsonObject2, "id");
							gameProfiles[i] = new GameProfile(UUID.fromString(string), class_3518.method_15265(jsonObject2, "name"));
						}

						lv.method_12686(gameProfiles);
					}
				}

				return lv;
			}

			public JsonElement method_12690(class_2926.class_2927 arg, Type type, JsonSerializationContext jsonSerializationContext) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("max", arg.method_12687());
				jsonObject.addProperty("online", arg.method_12688());
				if (arg.method_12685() != null && arg.method_12685().length > 0) {
					JsonArray jsonArray = new JsonArray();

					for (int i = 0; i < arg.method_12685().length; i++) {
						JsonObject jsonObject2 = new JsonObject();
						UUID uUID = arg.method_12685()[i].getId();
						jsonObject2.addProperty("id", uUID == null ? "" : uUID.toString());
						jsonObject2.addProperty("name", arg.method_12685()[i].getName());
						jsonArray.add(jsonObject2);
					}

					jsonObject.add("sample", jsonArray);
				}

				return jsonObject;
			}
		}
	}

	public static class class_2929 implements JsonDeserializer<class_2926>, JsonSerializer<class_2926> {
		public class_2926 method_12691(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "status");
			class_2926 lv = new class_2926();
			if (jsonObject.has("description")) {
				lv.method_12684(jsonDeserializationContext.deserialize(jsonObject.get("description"), class_2561.class));
			}

			if (jsonObject.has("players")) {
				lv.method_12681(jsonDeserializationContext.deserialize(jsonObject.get("players"), class_2926.class_2927.class));
			}

			if (jsonObject.has("version")) {
				lv.method_12679(jsonDeserializationContext.deserialize(jsonObject.get("version"), class_2926.class_2930.class));
			}

			if (jsonObject.has("favicon")) {
				lv.method_12677(class_3518.method_15265(jsonObject, "favicon"));
			}

			return lv;
		}

		public JsonElement method_12692(class_2926 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (arg.method_12680() != null) {
				jsonObject.add("description", jsonSerializationContext.serialize(arg.method_12680()));
			}

			if (arg.method_12682() != null) {
				jsonObject.add("players", jsonSerializationContext.serialize(arg.method_12682()));
			}

			if (arg.method_12683() != null) {
				jsonObject.add("version", jsonSerializationContext.serialize(arg.method_12683()));
			}

			if (arg.method_12678() != null) {
				jsonObject.addProperty("favicon", arg.method_12678());
			}

			return jsonObject;
		}
	}

	public static class class_2930 {
		private final String field_13290;
		private final int field_13291;

		public class_2930(String string, int i) {
			this.field_13290 = string;
			this.field_13291 = i;
		}

		public String method_12693() {
			return this.field_13290;
		}

		public int method_12694() {
			return this.field_13291;
		}

		public static class class_2931 implements JsonDeserializer<class_2926.class_2930>, JsonSerializer<class_2926.class_2930> {
			public class_2926.class_2930 method_12695(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				JsonObject jsonObject = class_3518.method_15295(jsonElement, "version");
				return new class_2926.class_2930(class_3518.method_15265(jsonObject, "name"), class_3518.method_15260(jsonObject, "protocol"));
			}

			public JsonElement method_12696(class_2926.class_2930 arg, Type type, JsonSerializationContext jsonSerializationContext) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name", arg.method_12693());
				jsonObject.addProperty("protocol", arg.method_12694());
				return jsonObject;
			}
		}
	}
}
