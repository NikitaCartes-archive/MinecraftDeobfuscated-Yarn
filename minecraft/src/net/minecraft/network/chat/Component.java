package net.minecraft.network.chat;

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
import net.minecraft.ChatFormat;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.SystemUtil;

public interface Component extends Message, Iterable<Component> {
	Component setStyle(Style style);

	Style getStyle();

	default Component append(String string) {
		return this.append(new TextComponent(string));
	}

	Component append(Component component);

	String getText();

	@Override
	default String getString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.stream().forEach(component -> stringBuilder.append(component.getText()));
		return stringBuilder.toString();
	}

	default String getStringTruncated(int i) {
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<Component> iterator = this.stream().iterator();

		while (iterator.hasNext()) {
			int j = i - stringBuilder.length();
			if (j <= 0) {
				break;
			}

			String string = ((Component)iterator.next()).getText();
			stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
		}

		return stringBuilder.toString();
	}

	default String getFormattedText() {
		StringBuilder stringBuilder = new StringBuilder();
		String string = "";
		Iterator<Component> iterator = this.stream().iterator();

		while (iterator.hasNext()) {
			Component component = (Component)iterator.next();
			String string2 = component.getText();
			if (!string2.isEmpty()) {
				String string3 = component.getStyle().getFormatString();
				if (!string3.equals(string)) {
					if (!string.isEmpty()) {
						stringBuilder.append(ChatFormat.field_1070);
					}

					stringBuilder.append(string3);
					string = string3;
				}

				stringBuilder.append(string2);
			}
		}

		if (!string.isEmpty()) {
			stringBuilder.append(ChatFormat.field_1070);
		}

		return stringBuilder.toString();
	}

	List<Component> getSiblings();

	Stream<Component> stream();

	default Stream<Component> streamCopied() {
		return this.stream().map(Component::copyWithoutChildren);
	}

	default Iterator<Component> iterator() {
		return this.streamCopied().iterator();
	}

	Component copyShallow();

	default Component copy() {
		Component component = this.copyShallow();
		component.setStyle(this.getStyle().clone());

		for (Component component2 : this.getSiblings()) {
			component.append(component2.copy());
		}

		return component;
	}

	default Component modifyStyle(Consumer<Style> consumer) {
		consumer.accept(this.getStyle());
		return this;
	}

	default Component applyFormat(ChatFormat... chatFormats) {
		for (ChatFormat chatFormat : chatFormats) {
			this.applyFormat(chatFormat);
		}

		return this;
	}

	default Component applyFormat(ChatFormat chatFormat) {
		Style style = this.getStyle();
		if (chatFormat.isColor()) {
			style.setColor(chatFormat);
		}

		if (chatFormat.isModifier()) {
			switch (chatFormat) {
				case field_1051:
					style.setObfuscated(true);
					break;
				case field_1067:
					style.setBold(true);
					break;
				case field_1055:
					style.setStrikethrough(true);
					break;
				case field_1073:
					style.setUnderline(true);
					break;
				case field_1056:
					style.setItalic(true);
			}
		}

		return this;
	}

	static Component copyWithoutChildren(Component component) {
		Component component2 = component.copyShallow();
		component2.setStyle(component.getStyle().copy());
		return component2;
	}

	public static class Serializer implements JsonDeserializer<Component>, JsonSerializer<Component> {
		private static final Gson GSON = SystemUtil.get(() -> {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.disableHtmlEscaping();
			gsonBuilder.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
			gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
			gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
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

		public Component method_10871(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return new TextComponent(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					Component component = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						Component component2 = this.method_10871(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (component == null) {
							component = component2;
						} else {
							component.append(component2);
						}
					}

					return component;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				Component component;
				if (jsonObject.has("text")) {
					component = new TextComponent(jsonObject.get("text").getAsString());
				} else if (jsonObject.has("translate")) {
					String string = jsonObject.get("translate").getAsString();
					if (jsonObject.has("with")) {
						JsonArray jsonArray = jsonObject.getAsJsonArray("with");
						Object[] objects = new Object[jsonArray.size()];

						for (int i = 0; i < objects.length; i++) {
							objects[i] = this.method_10871(jsonArray.get(i), type, jsonDeserializationContext);
							if (objects[i] instanceof TextComponent) {
								TextComponent textComponent = (TextComponent)objects[i];
								if (textComponent.getStyle().isEmpty() && textComponent.getSiblings().isEmpty()) {
									objects[i] = textComponent.getTextField();
								}
							}
						}

						component = new TranslatableComponent(string, objects);
					} else {
						component = new TranslatableComponent(string);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					component = new ScoreComponent(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
					if (jsonObject2.has("value")) {
						((ScoreComponent)component).setText(JsonHelper.getString(jsonObject2, "value"));
					}
				} else if (jsonObject.has("selector")) {
					component = new SelectorComponent(JsonHelper.getString(jsonObject, "selector"));
				} else if (jsonObject.has("keybind")) {
					component = new KeybindComponent(JsonHelper.getString(jsonObject, "keybind"));
				} else {
					if (!jsonObject.has("nbt")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					String string = JsonHelper.getString(jsonObject, "nbt");
					boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
					if (jsonObject.has("block")) {
						component = new NbtComponent.BlockPosArgument(string, bl, JsonHelper.getString(jsonObject, "block"));
					} else {
						if (!jsonObject.has("entity")) {
							throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
						}

						component = new NbtComponent.EntityNbtComponent(string, bl, JsonHelper.getString(jsonObject, "entity"));
					}
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = jsonObject.getAsJsonArray("extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						component.append(this.method_10871(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				component.setStyle(jsonDeserializationContext.deserialize(jsonElement, Style.class));
				return component;
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

		public JsonElement method_10874(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!component.getStyle().isEmpty()) {
				this.addStyle(component.getStyle(), jsonObject, jsonSerializationContext);
			}

			if (!component.getSiblings().isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Component component2 : component.getSiblings()) {
					jsonArray.add(this.method_10874(component2, component2.getClass(), jsonSerializationContext));
				}

				jsonObject.add("extra", jsonArray);
			}

			if (component instanceof TextComponent) {
				jsonObject.addProperty("text", ((TextComponent)component).getTextField());
			} else if (component instanceof TranslatableComponent) {
				TranslatableComponent translatableComponent = (TranslatableComponent)component;
				jsonObject.addProperty("translate", translatableComponent.getKey());
				if (translatableComponent.getParams() != null && translatableComponent.getParams().length > 0) {
					JsonArray jsonArray2 = new JsonArray();

					for (Object object : translatableComponent.getParams()) {
						if (object instanceof Component) {
							jsonArray2.add(this.method_10874((Component)object, object.getClass(), jsonSerializationContext));
						} else {
							jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonObject.add("with", jsonArray2);
				}
			} else if (component instanceof ScoreComponent) {
				ScoreComponent scoreComponent = (ScoreComponent)component;
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", scoreComponent.getName());
				jsonObject2.addProperty("objective", scoreComponent.getObjective());
				jsonObject2.addProperty("value", scoreComponent.getText());
				jsonObject.add("score", jsonObject2);
			} else if (component instanceof SelectorComponent) {
				SelectorComponent selectorComponent = (SelectorComponent)component;
				jsonObject.addProperty("selector", selectorComponent.getPattern());
			} else if (component instanceof KeybindComponent) {
				KeybindComponent keybindComponent = (KeybindComponent)component;
				jsonObject.addProperty("keybind", keybindComponent.getKeybind());
			} else {
				if (!(component instanceof NbtComponent)) {
					throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
				}

				NbtComponent nbtComponent = (NbtComponent)component;
				jsonObject.addProperty("nbt", nbtComponent.getPath());
				jsonObject.addProperty("interpret", nbtComponent.isComponentJson());
				if (component instanceof NbtComponent.BlockPosArgument) {
					NbtComponent.BlockPosArgument blockPosArgument = (NbtComponent.BlockPosArgument)component;
					jsonObject.addProperty("block", blockPosArgument.getPos());
				} else {
					if (!(component instanceof NbtComponent.EntityNbtComponent)) {
						throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
					}

					NbtComponent.EntityNbtComponent entityNbtComponent = (NbtComponent.EntityNbtComponent)component;
					jsonObject.addProperty("entity", entityNbtComponent.getSelector());
				}
			}

			return jsonObject;
		}

		public static String toJsonString(Component component) {
			return GSON.toJson(component);
		}

		public static JsonElement toJson(Component component) {
			return GSON.toJsonTree(component);
		}

		@Nullable
		public static Component fromJsonString(String string) {
			return JsonHelper.deserialize(GSON, string, Component.class, false);
		}

		@Nullable
		public static Component fromJson(JsonElement jsonElement) {
			return GSON.fromJson(jsonElement, Component.class);
		}

		@Nullable
		public static Component fromLenientJsonString(String string) {
			return JsonHelper.deserialize(GSON, string, Component.class, true);
		}

		public static Component fromJsonString(com.mojang.brigadier.StringReader stringReader) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(stringReader.getRemaining()));
				jsonReader.setLenient(false);
				Component component = GSON.<Component>getAdapter(Component.class).read(jsonReader);
				stringReader.setCursor(stringReader.getCursor() + getReaderPosition(jsonReader));
				return component;
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
