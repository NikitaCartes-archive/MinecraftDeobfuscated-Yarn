package net.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface class_2561 extends Message, Iterable<class_2561> {
	class_2561 method_10862(class_2583 arg);

	class_2583 method_10866();

	default class_2561 method_10864(String string) {
		return this.method_10852(new class_2585(string));
	}

	class_2561 method_10852(class_2561 arg);

	String method_10851();

	@Override
	default String getString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.method_10865().forEach(arg -> stringBuilder.append(arg.method_10851()));
		return stringBuilder.toString();
	}

	default String method_10858(int i) {
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<class_2561> iterator = this.method_10865().iterator();

		while (iterator.hasNext()) {
			int j = i - stringBuilder.length();
			if (j <= 0) {
				break;
			}

			String string = ((class_2561)iterator.next()).method_10851();
			stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
		}

		return stringBuilder.toString();
	}

	default String method_10863() {
		StringBuilder stringBuilder = new StringBuilder();
		String string = "";
		Iterator<class_2561> iterator = this.method_10865().iterator();

		while (iterator.hasNext()) {
			class_2561 lv = (class_2561)iterator.next();
			String string2 = lv.method_10851();
			if (!string2.isEmpty()) {
				String string3 = lv.method_10866().method_10953();
				if (!string3.equals(string)) {
					if (!string.isEmpty()) {
						stringBuilder.append(class_124.field_1070);
					}

					stringBuilder.append(string3);
					string = string3;
				}

				stringBuilder.append(string2);
			}
		}

		if (!string.isEmpty()) {
			stringBuilder.append(class_124.field_1070);
		}

		return stringBuilder.toString();
	}

	List<class_2561> method_10855();

	Stream<class_2561> method_10865();

	default Stream<class_2561> method_10860() {
		return this.method_10865().map(class_2561::method_10857);
	}

	default Iterator<class_2561> iterator() {
		return this.method_10860().iterator();
	}

	class_2561 method_10850();

	default class_2561 method_10853() {
		class_2561 lv = this.method_10850();
		lv.method_10862(this.method_10866().method_10976());

		for (class_2561 lv2 : this.method_10855()) {
			lv.method_10852(lv2.method_10853());
		}

		return lv;
	}

	default class_2561 method_10859(Consumer<class_2583> consumer) {
		consumer.accept(this.method_10866());
		return this;
	}

	default class_2561 method_10856(class_124... args) {
		for (class_124 lv : args) {
			this.method_10854(lv);
		}

		return this;
	}

	default class_2561 method_10854(class_124 arg) {
		class_2583 lv = this.method_10866();
		if (arg.method_543()) {
			lv.method_10977(arg);
		}

		if (arg.method_542()) {
			switch (arg) {
				case field_1051:
					lv.method_10948(true);
					break;
				case field_1067:
					lv.method_10982(true);
					break;
				case field_1055:
					lv.method_10959(true);
					break;
				case field_1073:
					lv.method_10968(true);
					break;
				case field_1056:
					lv.method_10978(true);
			}
		}

		return this;
	}

	static class_2561 method_10857(class_2561 arg) {
		class_2561 lv = arg.method_10850();
		lv.method_10862(arg.method_10866().method_10960());
		return lv;
	}

	public static class class_2562 implements JsonDeserializer<class_2561>, JsonSerializer<class_2561> {
		private static final Gson field_11754 = class_156.method_656(() -> {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.disableHtmlEscaping();
			gsonBuilder.registerTypeHierarchyAdapter(class_2561.class, new class_2561.class_2562());
			gsonBuilder.registerTypeHierarchyAdapter(class_2583.class, new class_2583.class_2584());
			gsonBuilder.registerTypeAdapterFactory(new class_3530());
			return gsonBuilder.create();
		});
		private static final Field field_11753 = class_156.method_656(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field field_11752 = class_156.method_656(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
			}
		});

		public class_2561 method_10871(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new class_2585(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					class_2561 lv = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						class_2561 lv3 = this.method_10871(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (lv == null) {
							lv = lv3;
						} else {
							lv.method_10852(lv3);
						}
					}

					return lv;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				class_2561 lv;
				if (jsonObject.has("text")) {
					lv = new class_2585(jsonObject.get("text").getAsString());
				} else if (jsonObject.has("translate")) {
					String string = jsonObject.get("translate").getAsString();
					if (jsonObject.has("with")) {
						JsonArray jsonArray = jsonObject.getAsJsonArray("with");
						Object[] objects = new Object[jsonArray.size()];

						for (int i = 0; i < objects.length; i++) {
							objects[i] = this.method_10871(jsonArray.get(i), type, jsonDeserializationContext);
							if (objects[i] instanceof class_2585) {
								class_2585 lv2 = (class_2585)objects[i];
								if (lv2.method_10866().method_10967() && lv2.method_10855().isEmpty()) {
									objects[i] = lv2.method_10993();
								}
							}
						}

						lv = new class_2588(string, objects);
					} else {
						lv = new class_2588(string);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					lv = new class_2578(class_3518.method_15265(jsonObject2, "name"), class_3518.method_15265(jsonObject2, "objective"));
					if (jsonObject2.has("value")) {
						((class_2578)lv).method_10927(class_3518.method_15265(jsonObject2, "value"));
					}
				} else if (jsonObject.has("selector")) {
					lv = new class_2579(class_3518.method_15265(jsonObject, "selector"));
				} else if (jsonObject.has("keybind")) {
					lv = new class_2572(class_3518.method_15265(jsonObject, "keybind"));
				} else {
					if (!jsonObject.has("nbt")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					String string = class_3518.method_15265(jsonObject, "nbt");
					boolean bl = class_3518.method_15258(jsonObject, "interpret", false);
					if (jsonObject.has("block")) {
						lv = new class_2574.class_2575(string, bl, class_3518.method_15265(jsonObject, "block"));
					} else {
						if (!jsonObject.has("entity")) {
							throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
						}

						lv = new class_2574.class_2576(string, bl, class_3518.method_15265(jsonObject, "entity"));
					}
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = jsonObject.getAsJsonArray("extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						lv.method_10852(this.method_10871(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				lv.method_10862(jsonDeserializationContext.deserialize(jsonElement, class_2583.class));
				return lv;
			}
		}

		private void method_10875(class_2583 arg, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			JsonElement jsonElement = jsonSerializationContext.serialize(arg);
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject2 = (JsonObject)jsonElement;

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
				}
			}
		}

		public JsonElement method_10874(class_2561 arg, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!arg.method_10866().method_10967()) {
				this.method_10875(arg.method_10866(), jsonObject, jsonSerializationContext);
			}

			if (!arg.method_10855().isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (class_2561 lv : arg.method_10855()) {
					jsonArray.add(this.method_10874(lv, lv.getClass(), jsonSerializationContext));
				}

				jsonObject.add("extra", jsonArray);
			}

			if (arg instanceof class_2585) {
				jsonObject.addProperty("text", ((class_2585)arg).method_10993());
			} else if (arg instanceof class_2588) {
				class_2588 lv2 = (class_2588)arg;
				jsonObject.addProperty("translate", lv2.method_11022());
				if (lv2.method_11023() != null && lv2.method_11023().length > 0) {
					JsonArray jsonArray2 = new JsonArray();

					for (Object object : lv2.method_11023()) {
						if (object instanceof class_2561) {
							jsonArray2.add(this.method_10874((class_2561)object, object.getClass(), jsonSerializationContext));
						} else {
							jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonObject.add("with", jsonArray2);
				}
			} else if (arg instanceof class_2578) {
				class_2578 lv3 = (class_2578)arg;
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", lv3.method_10930());
				jsonObject2.addProperty("objective", lv3.method_10928());
				jsonObject2.addProperty("value", lv3.method_10851());
				jsonObject.add("score", jsonObject2);
			} else if (arg instanceof class_2579) {
				class_2579 lv4 = (class_2579)arg;
				jsonObject.addProperty("selector", lv4.method_10932());
			} else if (arg instanceof class_2572) {
				class_2572 lv5 = (class_2572)arg;
				jsonObject.addProperty("keybind", lv5.method_10901());
			} else {
				if (!(arg instanceof class_2574)) {
					throw new IllegalArgumentException("Don't know how to serialize " + arg + " as a Component");
				}

				class_2574 lv6 = (class_2574)arg;
				jsonObject.addProperty("nbt", lv6.method_10920());
				jsonObject.addProperty("interpret", lv6.method_10921());
				if (arg instanceof class_2574.class_2575) {
					class_2574.class_2575 lv7 = (class_2574.class_2575)arg;
					jsonObject.addProperty("block", lv7.method_10922());
				} else {
					if (!(arg instanceof class_2574.class_2576)) {
						throw new IllegalArgumentException("Don't know how to serialize " + arg + " as a Component");
					}

					class_2574.class_2576 lv8 = (class_2574.class_2576)arg;
					jsonObject.addProperty("entity", lv8.method_10924());
				}
			}

			return jsonObject;
		}

		public static String method_10867(class_2561 arg) {
			return field_11754.toJson(arg);
		}

		public static JsonElement method_10868(class_2561 arg) {
			return field_11754.toJsonTree(arg);
		}

		@Nullable
		public static class_2561 method_10877(String string) {
			return class_3518.method_15279(field_11754, string, class_2561.class, false);
		}

		@Nullable
		public static class_2561 method_10872(JsonElement jsonElement) {
			return field_11754.fromJson(jsonElement, class_2561.class);
		}

		@Nullable
		public static class_2561 method_10873(String string) {
			return class_3518.method_15279(field_11754, string, class_2561.class, true);
		}

		public static class_2561 method_10879(com.mojang.brigadier.StringReader stringReader) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(stringReader.getRemaining()));
				jsonReader.setLenient(false);
				class_2561 lv = field_11754.<class_2561>getAdapter(class_2561.class).read(jsonReader);
				stringReader.setCursor(stringReader.getCursor() + method_10880(jsonReader));
				return lv;
			} catch (IOException var3) {
				throw new JsonParseException(var3);
			}
		}

		private static int method_10880(JsonReader jsonReader) {
			try {
				return field_11753.getInt(jsonReader) - field_11752.getInt(jsonReader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}
	}
}
