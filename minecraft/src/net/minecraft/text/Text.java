package net.minecraft.text;

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
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.SystemUtil;

public interface Text extends Message, Iterable<Text> {
	Text setStyle(Style style);

	Style getStyle();

	default Text append(String string) {
		return this.append(new LiteralText(string));
	}

	Text append(Text text);

	String asString();

	@Override
	default String getString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.stream().forEach(text -> stringBuilder.append(text.asString()));
		return stringBuilder.toString();
	}

	default String asTruncatedString(int i) {
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Text> iterator = this.stream().iterator();

		while (iterator.hasNext()) {
			int j = i - stringBuilder.length();
			if (j <= 0) {
				break;
			}

			String string = ((Text)iterator.next()).asString();
			stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
		}

		return stringBuilder.toString();
	}

	default String asFormattedString() {
		StringBuilder stringBuilder = new StringBuilder();
		String string = "";
		Iterator<Text> iterator = this.stream().iterator();

		while (iterator.hasNext()) {
			Text text = (Text)iterator.next();
			String string2 = text.asString();
			if (!string2.isEmpty()) {
				String string3 = text.getStyle().asString();
				if (!string3.equals(string)) {
					if (!string.isEmpty()) {
						stringBuilder.append(Formatting.RESET);
					}

					stringBuilder.append(string3);
					string = string3;
				}

				stringBuilder.append(string2);
			}
		}

		if (!string.isEmpty()) {
			stringBuilder.append(Formatting.RESET);
		}

		return stringBuilder.toString();
	}

	List<Text> getSiblings();

	Stream<Text> stream();

	default Stream<Text> streamCopied() {
		return this.stream().map(Text::copyWithoutChildren);
	}

	default Iterator<Text> iterator() {
		return this.streamCopied().iterator();
	}

	Text copy();

	default Text deepCopy() {
		Text text = this.copy();
		text.setStyle(this.getStyle().deepCopy());

		for (Text text2 : this.getSiblings()) {
			text.append(text2.deepCopy());
		}

		return text;
	}

	default Text styled(Consumer<Style> consumer) {
		consumer.accept(this.getStyle());
		return this;
	}

	default Text formatted(Formatting... formattings) {
		for (Formatting formatting : formattings) {
			this.formatted(formatting);
		}

		return this;
	}

	default Text formatted(Formatting formatting) {
		Style style = this.getStyle();
		if (formatting.isColor()) {
			style.setColor(formatting);
		}

		if (formatting.isModifier()) {
			switch (formatting) {
				case OBFUSCATED:
					style.setObfuscated(true);
					break;
				case BOLD:
					style.setBold(true);
					break;
				case STRIKETHROUGH:
					style.setStrikethrough(true);
					break;
				case UNDERLINE:
					style.setUnderline(true);
					break;
				case ITALIC:
					style.setItalic(true);
			}
		}

		return this;
	}

	static Text copyWithoutChildren(Text text) {
		Text text2 = text.copy();
		text2.setStyle(text.getStyle().copy());
		return text2;
	}

	public static class Serializer implements JsonDeserializer<Text>, JsonSerializer<Text> {
		private static final Gson GSON = SystemUtil.get(() -> {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.disableHtmlEscaping();
			gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
			gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
			gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
			return gsonBuilder.create();
		});
		private static final Field JSON_READER_POS = SystemUtil.get(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field JSON_READER_LINE_START = SystemUtil.get(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
			}
		});

		public Text method_10871(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new LiteralText(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					Text text = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						Text text2 = this.method_10871(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (text == null) {
							text = text2;
						} else {
							text.append(text2);
						}
					}

					return text;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				Text text;
				if (jsonObject.has("text")) {
					text = new LiteralText(jsonObject.get("text").getAsString());
				} else if (jsonObject.has("translate")) {
					String string = jsonObject.get("translate").getAsString();
					if (jsonObject.has("with")) {
						JsonArray jsonArray = jsonObject.getAsJsonArray("with");
						Object[] objects = new Object[jsonArray.size()];

						for (int i = 0; i < objects.length; i++) {
							objects[i] = this.method_10871(jsonArray.get(i), type, jsonDeserializationContext);
							if (objects[i] instanceof LiteralText) {
								LiteralText literalText = (LiteralText)objects[i];
								if (literalText.getStyle().isEmpty() && literalText.getSiblings().isEmpty()) {
									objects[i] = literalText.getRawString();
								}
							}
						}

						text = new TranslatableText(string, objects);
					} else {
						text = new TranslatableText(string);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					text = new ScoreText(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
					if (jsonObject2.has("value")) {
						((ScoreText)text).setScore(JsonHelper.getString(jsonObject2, "value"));
					}
				} else if (jsonObject.has("selector")) {
					text = new SelectorText(JsonHelper.getString(jsonObject, "selector"));
				} else if (jsonObject.has("keybind")) {
					text = new KeybindText(JsonHelper.getString(jsonObject, "keybind"));
				} else {
					if (!jsonObject.has("nbt")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					String string = JsonHelper.getString(jsonObject, "nbt");
					boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
					if (jsonObject.has("block")) {
						text = new NbtText.BlockNbtText(string, bl, JsonHelper.getString(jsonObject, "block"));
					} else {
						if (!jsonObject.has("entity")) {
							throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
						}

						text = new NbtText.EntityNbtText(string, bl, JsonHelper.getString(jsonObject, "entity"));
					}
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = jsonObject.getAsJsonArray("extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						text.append(this.method_10871(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				text.setStyle(jsonDeserializationContext.deserialize(jsonElement, Style.class));
				return text;
			}
		}

		private void addStyle(Style style, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			JsonElement jsonElement = jsonSerializationContext.serialize(style);
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject2 = (JsonObject)jsonElement;

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
				}
			}
		}

		public JsonElement method_10874(Text text, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!text.getStyle().isEmpty()) {
				this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
			}

			if (!text.getSiblings().isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Text text2 : text.getSiblings()) {
					jsonArray.add(this.method_10874(text2, text2.getClass(), jsonSerializationContext));
				}

				jsonObject.add("extra", jsonArray);
			}

			if (text instanceof LiteralText) {
				jsonObject.addProperty("text", ((LiteralText)text).getRawString());
			} else if (text instanceof TranslatableText) {
				TranslatableText translatableText = (TranslatableText)text;
				jsonObject.addProperty("translate", translatableText.getKey());
				if (translatableText.getArgs() != null && translatableText.getArgs().length > 0) {
					JsonArray jsonArray2 = new JsonArray();

					for (Object object : translatableText.getArgs()) {
						if (object instanceof Text) {
							jsonArray2.add(this.method_10874((Text)object, object.getClass(), jsonSerializationContext));
						} else {
							jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonObject.add("with", jsonArray2);
				}
			} else if (text instanceof ScoreText) {
				ScoreText scoreText = (ScoreText)text;
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", scoreText.getName());
				jsonObject2.addProperty("objective", scoreText.getObjective());
				jsonObject2.addProperty("value", scoreText.asString());
				jsonObject.add("score", jsonObject2);
			} else if (text instanceof SelectorText) {
				SelectorText selectorText = (SelectorText)text;
				jsonObject.addProperty("selector", selectorText.getPattern());
			} else if (text instanceof KeybindText) {
				KeybindText keybindText = (KeybindText)text;
				jsonObject.addProperty("keybind", keybindText.getKey());
			} else {
				if (!(text instanceof NbtText)) {
					throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
				}

				NbtText nbtText = (NbtText)text;
				jsonObject.addProperty("nbt", nbtText.getPath());
				jsonObject.addProperty("interpret", nbtText.shouldInterpret());
				if (text instanceof NbtText.BlockNbtText) {
					NbtText.BlockNbtText blockNbtText = (NbtText.BlockNbtText)text;
					jsonObject.addProperty("block", blockNbtText.getPos());
				} else {
					if (!(text instanceof NbtText.EntityNbtText)) {
						throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
					}

					NbtText.EntityNbtText entityNbtText = (NbtText.EntityNbtText)text;
					jsonObject.addProperty("entity", entityNbtText.getSelector());
				}
			}

			return jsonObject;
		}

		public static String toJson(Text text) {
			return GSON.toJson(text);
		}

		public static JsonElement toJsonTree(Text text) {
			return GSON.toJsonTree(text);
		}

		@Nullable
		public static Text fromJson(String string) {
			return JsonHelper.deserialize(GSON, string, Text.class, false);
		}

		@Nullable
		public static Text fromJson(JsonElement jsonElement) {
			return GSON.fromJson(jsonElement, Text.class);
		}

		@Nullable
		public static Text fromLenientJson(String string) {
			return JsonHelper.deserialize(GSON, string, Text.class, true);
		}

		public static Text fromJson(com.mojang.brigadier.StringReader stringReader) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(stringReader.getRemaining()));
				jsonReader.setLenient(false);
				Text text = GSON.<Text>getAdapter(Text.class).read(jsonReader);
				stringReader.setCursor(stringReader.getCursor() + getPosition(jsonReader));
				return text;
			} catch (IOException var3) {
				throw new JsonParseException(var3);
			}
		}

		private static int getPosition(JsonReader jsonReader) {
			try {
				return JSON_READER_POS.getInt(jsonReader) - JSON_READER_LINE_START.getInt(jsonReader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}
	}
}
