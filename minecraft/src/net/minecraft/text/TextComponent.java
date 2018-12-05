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
import net.minecraft.class_2574;
import net.minecraft.class_3530;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;

public interface TextComponent extends Message, Iterable<TextComponent> {
	TextComponent setStyle(Style style);

	Style getStyle();

	default TextComponent append(String string) {
		return this.append(new StringTextComponent(string));
	}

	TextComponent append(TextComponent textComponent);

	String getText();

	@Override
	default String getString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.stream().forEach(textComponent -> stringBuilder.append(textComponent.getText()));
		return stringBuilder.toString();
	}

	default String getStringTruncated(int i) {
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<TextComponent> iterator = this.stream().iterator();

		while (iterator.hasNext()) {
			int j = i - stringBuilder.length();
			if (j <= 0) {
				break;
			}

			String string = ((TextComponent)iterator.next()).getText();
			stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
		}

		return stringBuilder.toString();
	}

	default String getFormattedText() {
		StringBuilder stringBuilder = new StringBuilder();
		String string = "";
		Iterator<TextComponent> iterator = this.stream().iterator();

		while (iterator.hasNext()) {
			TextComponent textComponent = (TextComponent)iterator.next();
			String string2 = textComponent.getText();
			if (!string2.isEmpty()) {
				String string3 = textComponent.getStyle().getFormatString();
				if (!string3.equals(string)) {
					if (!string.isEmpty()) {
						stringBuilder.append(TextFormat.RESET);
					}

					stringBuilder.append(string3);
					string = string3;
				}

				stringBuilder.append(string2);
			}
		}

		if (!string.isEmpty()) {
			stringBuilder.append(TextFormat.RESET);
		}

		return stringBuilder.toString();
	}

	List<TextComponent> getChildren();

	Stream<TextComponent> stream();

	default Stream<TextComponent> streamCloned() {
		return this.stream().map(TextComponent::cloneWithoutChildren);
	}

	default Iterator<TextComponent> iterator() {
		return this.streamCloned().iterator();
	}

	TextComponent cloneShallow();

	default TextComponent clone() {
		TextComponent textComponent = this.cloneShallow();
		textComponent.setStyle(this.getStyle().clone());

		for (TextComponent textComponent2 : this.getChildren()) {
			textComponent.append(textComponent2.clone());
		}

		return textComponent;
	}

	default TextComponent modifyStyle(Consumer<Style> consumer) {
		consumer.accept(this.getStyle());
		return this;
	}

	default TextComponent applyFormat(TextFormat... textFormats) {
		for (TextFormat textFormat : textFormats) {
			this.applyFormat(textFormat);
		}

		return this;
	}

	default TextComponent applyFormat(TextFormat textFormat) {
		Style style = this.getStyle();
		if (textFormat.isColor()) {
			style.setColor(textFormat);
		}

		if (textFormat.isModifier()) {
			switch (textFormat) {
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

	static TextComponent cloneWithoutChildren(TextComponent textComponent) {
		TextComponent textComponent2 = textComponent.cloneShallow();
		textComponent2.setStyle(textComponent.getStyle().copy());
		return textComponent2;
	}

	public static class Serializer implements JsonDeserializer<TextComponent>, JsonSerializer<TextComponent> {
		private static final Gson GSON = SystemUtil.get(() -> {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer());
			gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
			gsonBuilder.registerTypeAdapterFactory(new class_3530());
			return gsonBuilder.create();
		});
		private static final Field POS_FIELD = SystemUtil.get(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field LINE_START_FIELD = SystemUtil.get(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
			}
		});

		public TextComponent method_10871(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new StringTextComponent(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					TextComponent textComponent = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						TextComponent textComponent2 = this.method_10871(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (textComponent == null) {
							textComponent = textComponent2;
						} else {
							textComponent.append(textComponent2);
						}
					}

					return textComponent;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				TextComponent textComponent;
				if (jsonObject.has("text")) {
					textComponent = new StringTextComponent(jsonObject.get("text").getAsString());
				} else if (jsonObject.has("translate")) {
					String string = jsonObject.get("translate").getAsString();
					if (jsonObject.has("with")) {
						JsonArray jsonArray = jsonObject.getAsJsonArray("with");
						Object[] objects = new Object[jsonArray.size()];

						for (int i = 0; i < objects.length; i++) {
							objects[i] = this.method_10871(jsonArray.get(i), type, jsonDeserializationContext);
							if (objects[i] instanceof StringTextComponent) {
								StringTextComponent stringTextComponent = (StringTextComponent)objects[i];
								if (stringTextComponent.getStyle().isEmpty() && stringTextComponent.getChildren().isEmpty()) {
									objects[i] = stringTextComponent.getTextField();
								}
							}
						}

						textComponent = new TranslatableTextComponent(string, objects);
					} else {
						textComponent = new TranslatableTextComponent(string);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					textComponent = new ScoreTextComponent(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
					if (jsonObject2.has("value")) {
						((ScoreTextComponent)textComponent).setText(JsonHelper.getString(jsonObject2, "value"));
					}
				} else if (jsonObject.has("selector")) {
					textComponent = new SelectorTextComponent(JsonHelper.getString(jsonObject, "selector"));
				} else if (jsonObject.has("keybind")) {
					textComponent = new KeybindTextComponent(JsonHelper.getString(jsonObject, "keybind"));
				} else {
					if (!jsonObject.has("nbt")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					String string = JsonHelper.getString(jsonObject, "nbt");
					boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
					if (jsonObject.has("block")) {
						textComponent = new class_2574.class_2575(string, bl, JsonHelper.getString(jsonObject, "block"));
					} else {
						if (!jsonObject.has("entity")) {
							throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
						}

						textComponent = new class_2574.class_2576(string, bl, JsonHelper.getString(jsonObject, "entity"));
					}
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = jsonObject.getAsJsonArray("extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						textComponent.append(this.method_10871(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				textComponent.setStyle(jsonDeserializationContext.deserialize(jsonElement, Style.class));
				return textComponent;
			}
		}

		private void method_10875(Style style, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
			JsonElement jsonElement = jsonSerializationContext.serialize(style);
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject2 = (JsonObject)jsonElement;

				for (Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
					jsonObject.add((String)entry.getKey(), (JsonElement)entry.getValue());
				}
			}
		}

		public JsonElement method_10874(TextComponent textComponent, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!textComponent.getStyle().isEmpty()) {
				this.method_10875(textComponent.getStyle(), jsonObject, jsonSerializationContext);
			}

			if (!textComponent.getChildren().isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (TextComponent textComponent2 : textComponent.getChildren()) {
					jsonArray.add(this.method_10874(textComponent2, textComponent2.getClass(), jsonSerializationContext));
				}

				jsonObject.add("extra", jsonArray);
			}

			if (textComponent instanceof StringTextComponent) {
				jsonObject.addProperty("text", ((StringTextComponent)textComponent).getTextField());
			} else if (textComponent instanceof TranslatableTextComponent) {
				TranslatableTextComponent translatableTextComponent = (TranslatableTextComponent)textComponent;
				jsonObject.addProperty("translate", translatableTextComponent.getKey());
				if (translatableTextComponent.getParams() != null && translatableTextComponent.getParams().length > 0) {
					JsonArray jsonArray2 = new JsonArray();

					for (Object object : translatableTextComponent.getParams()) {
						if (object instanceof TextComponent) {
							jsonArray2.add(this.method_10874((TextComponent)object, object.getClass(), jsonSerializationContext));
						} else {
							jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonObject.add("with", jsonArray2);
				}
			} else if (textComponent instanceof ScoreTextComponent) {
				ScoreTextComponent scoreTextComponent = (ScoreTextComponent)textComponent;
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", scoreTextComponent.getName());
				jsonObject2.addProperty("objective", scoreTextComponent.getObjective());
				jsonObject2.addProperty("value", scoreTextComponent.getText());
				jsonObject.add("score", jsonObject2);
			} else if (textComponent instanceof SelectorTextComponent) {
				SelectorTextComponent selectorTextComponent = (SelectorTextComponent)textComponent;
				jsonObject.addProperty("selector", selectorTextComponent.getPattern());
			} else if (textComponent instanceof KeybindTextComponent) {
				KeybindTextComponent keybindTextComponent = (KeybindTextComponent)textComponent;
				jsonObject.addProperty("keybind", keybindTextComponent.getKeybind());
			} else {
				if (!(textComponent instanceof class_2574)) {
					throw new IllegalArgumentException("Don't know how to serialize " + textComponent + " as a Component");
				}

				class_2574 lv = (class_2574)textComponent;
				jsonObject.addProperty("nbt", lv.method_10920());
				jsonObject.addProperty("interpret", lv.method_10921());
				if (textComponent instanceof class_2574.class_2575) {
					class_2574.class_2575 lv2 = (class_2574.class_2575)textComponent;
					jsonObject.addProperty("block", lv2.method_10922());
				} else {
					if (!(textComponent instanceof class_2574.class_2576)) {
						throw new IllegalArgumentException("Don't know how to serialize " + textComponent + " as a Component");
					}

					class_2574.class_2576 lv3 = (class_2574.class_2576)textComponent;
					jsonObject.addProperty("entity", lv3.method_10924());
				}
			}

			return jsonObject;
		}

		public static String toJsonString(TextComponent textComponent) {
			return GSON.toJson(textComponent);
		}

		public static JsonElement toJson(TextComponent textComponent) {
			return GSON.toJsonTree(textComponent);
		}

		@Nullable
		public static TextComponent fromJsonString(String string) {
			return JsonHelper.deserialize(GSON, string, TextComponent.class, false);
		}

		@Nullable
		public static TextComponent fromJson(JsonElement jsonElement) {
			return GSON.fromJson(jsonElement, TextComponent.class);
		}

		@Nullable
		public static TextComponent fromLenientJsonString(String string) {
			return JsonHelper.deserialize(GSON, string, TextComponent.class, true);
		}

		public static TextComponent fromJsonString(com.mojang.brigadier.StringReader stringReader) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(stringReader.getRemaining()));
				jsonReader.setLenient(false);
				TextComponent textComponent = GSON.<TextComponent>getAdapter(TextComponent.class).read(jsonReader);
				stringReader.setCursor(stringReader.getCursor() + getReaderPosition(jsonReader));
				return textComponent;
			} catch (IOException var3) {
				throw new JsonParseException(var3);
			}
		}

		private static int getReaderPosition(JsonReader jsonReader) {
			try {
				return POS_FIELD.getInt(jsonReader) - LINE_START_FIELD.getInt(jsonReader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}
	}
}
