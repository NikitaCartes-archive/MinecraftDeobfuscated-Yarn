package net.minecraft.text;

import com.google.common.collect.Lists;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.Util;

/**
 * A text. Can be converted to and from JSON format.
 * 
 * <p>Each text has a tree structure, embodying all its {@link
 * #getSiblings() siblings}. To iterate contents in the text and all
 * its siblings, call {@code visit} methods.
 * 
 * <p>This interface does not expose mutation operations. For mutation,
 * refer to {@link MutableText}.
 * 
 * @see MutableText
 */
public interface Text extends Message, StringVisitable {
	/**
	 * Returns the style of this text.
	 */
	Style getStyle();

	/**
	 * {@return the content of the text}
	 */
	TextContent getContent();

	@Override
	default String getString() {
		return StringVisitable.super.getString();
	}

	/**
	 * Returns the full string representation of this text, truncated beyond
	 * the supplied {@code length}.
	 * 
	 * @param length the max length allowed for the string representation of the text
	 */
	default String asTruncatedString(int length) {
		StringBuilder stringBuilder = new StringBuilder();
		this.visit(string -> {
			int j = length - stringBuilder.length();
			if (j <= 0) {
				return TERMINATE_VISIT;
			} else {
				stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
				return Optional.empty();
			}
		});
		return stringBuilder.toString();
	}

	/**
	 * Returns the siblings of this text.
	 */
	List<Text> getSiblings();

	/**
	 * Copies the text's content, excluding the styles or siblings.
	 */
	default MutableText copyContentOnly() {
		return MutableText.of(this.getContent());
	}

	/**
	 * Copies the text's content, the style, and the siblings.
	 * 
	 * <p>A shallow copy is made for the siblings.
	 */
	default MutableText copy() {
		return new MutableText(this.getContent(), new ArrayList(this.getSiblings()), this.getStyle());
	}

	OrderedText asOrderedText();

	@Override
	default <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
		Style style2 = this.getStyle().withParent(style);
		Optional<T> optional = this.getContent().visit(styledVisitor, style2);
		if (optional.isPresent()) {
			return optional;
		} else {
			for (Text text : this.getSiblings()) {
				Optional<T> optional2 = text.visit(styledVisitor, style2);
				if (optional2.isPresent()) {
					return optional2;
				}
			}

			return Optional.empty();
		}
	}

	@Override
	default <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		Optional<T> optional = this.getContent().visit(visitor);
		if (optional.isPresent()) {
			return optional;
		} else {
			for (Text text : this.getSiblings()) {
				Optional<T> optional2 = text.visit(visitor);
				if (optional2.isPresent()) {
					return optional2;
				}
			}

			return Optional.empty();
		}
	}

	default List<Text> withoutStyle() {
		return this.getWithStyle(Style.EMPTY);
	}

	default List<Text> getWithStyle(Style style) {
		List<Text> list = Lists.<Text>newArrayList();
		this.visit((styleOverride, text) -> {
			if (!text.isEmpty()) {
				list.add(literal(text).fillStyle(styleOverride));
			}

			return Optional.empty();
		}, style);
		return list;
	}

	/**
	 * {@return whether the text contains {@code text}, without considering styles}
	 */
	default boolean contains(Text text) {
		if (this.equals(text)) {
			return true;
		} else {
			List<Text> list = this.withoutStyle();
			List<Text> list2 = text.getWithStyle(this.getStyle());
			return Collections.indexOfSubList(list, list2) != -1;
		}
	}

	/**
	 * Creates a literal text with the given string as content.
	 */
	static Text of(@Nullable String string) {
		return (Text)(string != null ? literal(string) : ScreenTexts.EMPTY);
	}

	static MutableText literal(String string) {
		return MutableText.of(new LiteralTextContent(string));
	}

	static MutableText translatable(String key) {
		return MutableText.of(new TranslatableTextContent(key, null, TranslatableTextContent.EMPTY_ARGUMENTS));
	}

	static MutableText translatable(String key, Object... args) {
		return MutableText.of(new TranslatableTextContent(key, null, args));
	}

	static MutableText translatableWithFallback(String key, @Nullable String fallback) {
		return MutableText.of(new TranslatableTextContent(key, fallback, TranslatableTextContent.EMPTY_ARGUMENTS));
	}

	static MutableText translatableWithFallback(String key, @Nullable String fallback, Object... args) {
		return MutableText.of(new TranslatableTextContent(key, fallback, args));
	}

	static MutableText empty() {
		return MutableText.of(TextContent.EMPTY);
	}

	static MutableText keybind(String string) {
		return MutableText.of(new KeybindTextContent(string));
	}

	static MutableText nbt(String rawPath, boolean interpret, Optional<Text> separator, NbtDataSource dataSource) {
		return MutableText.of(new NbtTextContent(rawPath, interpret, separator, dataSource));
	}

	static MutableText score(String name, String objective) {
		return MutableText.of(new ScoreTextContent(name, objective));
	}

	static MutableText selector(String pattern, Optional<Text> separator) {
		return MutableText.of(new SelectorTextContent(pattern, separator));
	}

	/**
	 * A JSON serializer for {@link Text}.
	 */
	public static class Serializer implements JsonDeserializer<MutableText>, JsonSerializer<Text> {
		private static final Gson GSON = Util.make(() -> {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.disableHtmlEscaping();
			gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Text.Serializer());
			gsonBuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
			gsonBuilder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
			return gsonBuilder.create();
		});
		private static final Field JSON_READER_POS = Util.make(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field JSON_READER_LINE_START = Util.make(() -> {
			try {
				new JsonReader(new StringReader(""));
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
			}
		});

		public MutableText deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			if (jsonElement.isJsonPrimitive()) {
				return Text.literal(jsonElement.getAsString());
			} else if (!jsonElement.isJsonObject()) {
				if (jsonElement.isJsonArray()) {
					JsonArray jsonArray3 = jsonElement.getAsJsonArray();
					MutableText mutableText = null;

					for (JsonElement jsonElement2 : jsonArray3) {
						MutableText mutableText2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
						if (mutableText == null) {
							mutableText = mutableText2;
						} else {
							mutableText.append(mutableText2);
						}
					}

					return mutableText;
				} else {
					throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
				}
			} else {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				MutableText mutableText;
				if (jsonObject.has("text")) {
					String string = JsonHelper.getString(jsonObject, "text");
					mutableText = string.isEmpty() ? Text.empty() : Text.literal(string);
				} else if (jsonObject.has("translate")) {
					String string = JsonHelper.getString(jsonObject, "translate");
					String string2 = JsonHelper.getString(jsonObject, "fallback", null);
					if (jsonObject.has("with")) {
						JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
						Object[] objects = new Object[jsonArray.size()];

						for (int i = 0; i < objects.length; i++) {
							objects[i] = optimizeArgument(this.deserialize(jsonArray.get(i), type, jsonDeserializationContext));
						}

						mutableText = Text.translatableWithFallback(string, string2, objects);
					} else {
						mutableText = Text.translatableWithFallback(string, string2);
					}
				} else if (jsonObject.has("score")) {
					JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
					if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
						throw new JsonParseException("A score component needs a least a name and an objective");
					}

					mutableText = Text.score(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
				} else if (jsonObject.has("selector")) {
					Optional<Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
					mutableText = Text.selector(JsonHelper.getString(jsonObject, "selector"), optional);
				} else if (jsonObject.has("keybind")) {
					mutableText = Text.keybind(JsonHelper.getString(jsonObject, "keybind"));
				} else {
					if (!jsonObject.has("nbt")) {
						throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
					}

					String string = JsonHelper.getString(jsonObject, "nbt");
					Optional<Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
					boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
					NbtDataSource nbtDataSource;
					if (jsonObject.has("block")) {
						nbtDataSource = new BlockNbtDataSource(JsonHelper.getString(jsonObject, "block"));
					} else if (jsonObject.has("entity")) {
						nbtDataSource = new EntityNbtDataSource(JsonHelper.getString(jsonObject, "entity"));
					} else {
						if (!jsonObject.has("storage")) {
							throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
						}

						nbtDataSource = new StorageNbtDataSource(new Identifier(JsonHelper.getString(jsonObject, "storage")));
					}

					mutableText = Text.nbt(string, bl, optional2, nbtDataSource);
				}

				if (jsonObject.has("extra")) {
					JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
					if (jsonArray2.size() <= 0) {
						throw new JsonParseException("Unexpected empty array of components");
					}

					for (int j = 0; j < jsonArray2.size(); j++) {
						mutableText.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
					}
				}

				mutableText.setStyle(jsonDeserializationContext.deserialize(jsonElement, Style.class));
				return mutableText;
			}
		}

		private static Object optimizeArgument(Object text) {
			if (text instanceof Text text2
				&& text2.getStyle().isEmpty()
				&& text2.getSiblings().isEmpty()
				&& text2.getContent() instanceof LiteralTextContent literalTextContent) {
				return literalTextContent.string();
			}

			return text;
		}

		private Optional<Text> getSeparator(Type type, JsonDeserializationContext context, JsonObject json) {
			return json.has("separator") ? Optional.of(this.deserialize(json.get("separator"), type, context)) : Optional.empty();
		}

		private void addStyle(Style style, JsonObject json, JsonSerializationContext context) {
			JsonElement jsonElement = context.serialize(style);
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObject = (JsonObject)jsonElement;

				for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
					json.add((String)entry.getKey(), (JsonElement)entry.getValue());
				}
			}
		}

		public JsonElement serialize(Text text, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (!text.getStyle().isEmpty()) {
				this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
			}

			if (!text.getSiblings().isEmpty()) {
				JsonArray jsonArray = new JsonArray();

				for (Text text2 : text.getSiblings()) {
					jsonArray.add(this.serialize(text2, Text.class, jsonSerializationContext));
				}

				jsonObject.add("extra", jsonArray);
			}

			TextContent textContent = text.getContent();
			if (textContent == TextContent.EMPTY) {
				jsonObject.addProperty("text", "");
			} else if (textContent instanceof LiteralTextContent literalTextContent) {
				jsonObject.addProperty("text", literalTextContent.string());
			} else if (textContent instanceof TranslatableTextContent translatableTextContent) {
				jsonObject.addProperty("translate", translatableTextContent.getKey());
				String string = translatableTextContent.getFallback();
				if (string != null) {
					jsonObject.addProperty("fallback", string);
				}

				if (translatableTextContent.getArgs().length > 0) {
					JsonArray jsonArray2 = new JsonArray();

					for (Object object : translatableTextContent.getArgs()) {
						if (object instanceof Text) {
							jsonArray2.add(this.serialize((Text)object, object.getClass(), jsonSerializationContext));
						} else {
							jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonObject.add("with", jsonArray2);
				}
			} else if (textContent instanceof ScoreTextContent scoreTextContent) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.addProperty("name", scoreTextContent.getName());
				jsonObject2.addProperty("objective", scoreTextContent.getObjective());
				jsonObject.add("score", jsonObject2);
			} else if (textContent instanceof SelectorTextContent selectorTextContent) {
				jsonObject.addProperty("selector", selectorTextContent.getPattern());
				this.addSeparator(jsonSerializationContext, jsonObject, selectorTextContent.getSeparator());
			} else if (textContent instanceof KeybindTextContent keybindTextContent) {
				jsonObject.addProperty("keybind", keybindTextContent.getKey());
			} else {
				if (!(textContent instanceof NbtTextContent nbtTextContent)) {
					throw new IllegalArgumentException("Don't know how to serialize " + textContent + " as a Component");
				}

				jsonObject.addProperty("nbt", nbtTextContent.getPath());
				jsonObject.addProperty("interpret", nbtTextContent.shouldInterpret());
				this.addSeparator(jsonSerializationContext, jsonObject, nbtTextContent.getSeparator());
				NbtDataSource nbtDataSource = nbtTextContent.getDataSource();
				if (nbtDataSource instanceof BlockNbtDataSource blockNbtDataSource) {
					jsonObject.addProperty("block", blockNbtDataSource.rawPos());
				} else if (nbtDataSource instanceof EntityNbtDataSource entityNbtDataSource) {
					jsonObject.addProperty("entity", entityNbtDataSource.rawSelector());
				} else {
					if (!(nbtDataSource instanceof StorageNbtDataSource storageNbtDataSource)) {
						throw new IllegalArgumentException("Don't know how to serialize " + textContent + " as a Component");
					}

					jsonObject.addProperty("storage", storageNbtDataSource.id().toString());
				}
			}

			return jsonObject;
		}

		private void addSeparator(JsonSerializationContext context, JsonObject json, Optional<Text> optionalSeparator) {
			optionalSeparator.ifPresent(separator -> json.add("separator", this.serialize(separator, separator.getClass(), context)));
		}

		public static String toJson(Text text) {
			return GSON.toJson(text);
		}

		public static String toSortedJsonString(Text text) {
			return JsonHelper.toSortedString(toJsonTree(text));
		}

		public static JsonElement toJsonTree(Text text) {
			return GSON.toJsonTree(text);
		}

		@Nullable
		public static MutableText fromJson(String json) {
			return JsonHelper.deserializeNullable(GSON, json, MutableText.class, false);
		}

		@Nullable
		public static MutableText fromJson(JsonElement json) {
			return GSON.fromJson(json, MutableText.class);
		}

		@Nullable
		public static MutableText fromLenientJson(String json) {
			return JsonHelper.deserializeNullable(GSON, json, MutableText.class, true);
		}

		public static MutableText fromJson(com.mojang.brigadier.StringReader reader) {
			try {
				JsonReader jsonReader = new JsonReader(new StringReader(reader.getRemaining()));
				jsonReader.setLenient(false);
				MutableText mutableText = GSON.<MutableText>getAdapter(MutableText.class).read(jsonReader);
				reader.setCursor(reader.getCursor() + getPosition(jsonReader));
				return mutableText;
			} catch (StackOverflowError | IOException var3) {
				throw new JsonParseException(var3);
			}
		}

		private static int getPosition(JsonReader reader) {
			try {
				return JSON_READER_POS.getInt(reader) - JSON_READER_LINE_START.getInt(reader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}
	}
}
