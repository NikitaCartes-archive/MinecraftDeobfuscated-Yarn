/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.BlockNbtDataSource;
import net.minecraft.text.EntityNbtDataSource;
import net.minecraft.text.KeybindTextContent;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.NbtDataSource;
import net.minecraft.text.NbtTextContent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.ScoreTextContent;
import net.minecraft.text.SelectorTextContent;
import net.minecraft.text.StorageNbtDataSource;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

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
public interface Text
extends Message,
StringVisitable {
    /**
     * Returns the style of this text.
     */
    public Style getStyle();

    /**
     * {@return the content of the text}
     */
    public TextContent getContent();

    @Override
    default public String getString() {
        return StringVisitable.super.getString();
    }

    /**
     * Returns the full string representation of this text, truncated beyond
     * the supplied {@code length}.
     * 
     * @param length the max length allowed for the string representation of the text
     */
    default public String asTruncatedString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        this.visit(string -> {
            int j = length - stringBuilder.length();
            if (j <= 0) {
                return TERMINATE_VISIT;
            }
            stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
            return Optional.empty();
        });
        return stringBuilder.toString();
    }

    /**
     * Returns the siblings of this text.
     */
    public List<Text> getSiblings();

    /**
     * Copies the text itself, excluding the styles or siblings.
     */
    default public MutableText copy() {
        return MutableText.of(this.getContent());
    }

    /**
     * Copies the text itself, the style, and the siblings.
     * 
     * <p>A shallow copy is made for the siblings.
     */
    default public MutableText shallowCopy() {
        return new MutableText(this.getContent(), new ArrayList<Text>(this.getSiblings()), this.getStyle());
    }

    public OrderedText asOrderedText();

    @Override
    default public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        Style style2 = this.getStyle().withParent(style);
        Optional<T> optional = this.getContent().visit(styledVisitor, style2);
        if (optional.isPresent()) {
            return optional;
        }
        for (Text text : this.getSiblings()) {
            Optional<T> optional2 = text.visit(styledVisitor, style2);
            if (!optional2.isPresent()) continue;
            return optional2;
        }
        return Optional.empty();
    }

    @Override
    default public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        Optional<T> optional = this.getContent().visit(visitor);
        if (optional.isPresent()) {
            return optional;
        }
        for (Text text : this.getSiblings()) {
            Optional<T> optional2 = text.visit(visitor);
            if (!optional2.isPresent()) continue;
            return optional2;
        }
        return Optional.empty();
    }

    default public List<Text> getWithStyle(Style style) {
        ArrayList<Text> list = Lists.newArrayList();
        this.visit((styleOverride, text) -> {
            if (!text.isEmpty()) {
                list.add(Text.literal(text).fillStyle(styleOverride));
            }
            return Optional.empty();
        }, style);
        return list;
    }

    /**
     * Creates a literal text with the given string as content.
     */
    public static Text of(@Nullable String string) {
        return string != null ? Text.literal(string) : ScreenTexts.EMPTY;
    }

    public static MutableText literal(String string) {
        return MutableText.of(new LiteralTextContent(string));
    }

    public static MutableText translatable(String key) {
        return MutableText.of(new TranslatableTextContent(key));
    }

    public static MutableText translatable(String key, Object ... args) {
        return MutableText.of(new TranslatableTextContent(key, args));
    }

    public static MutableText empty() {
        return MutableText.of(TextContent.EMPTY);
    }

    public static MutableText keybind(String string) {
        return MutableText.of(new KeybindTextContent(string));
    }

    public static MutableText nbt(String rawPath, boolean interpret, Optional<Text> separator, NbtDataSource dataSource) {
        return MutableText.of(new NbtTextContent(rawPath, interpret, separator, dataSource));
    }

    public static MutableText score(String name, String objective) {
        return MutableText.of(new ScoreTextContent(name, objective));
    }

    public static MutableText selector(String pattern, Optional<Text> separator) {
        return MutableText.of(new SelectorTextContent(pattern, separator));
    }

    public static class Serializer
    implements JsonDeserializer<MutableText>,
    JsonSerializer<Text> {
        private static final Gson GSON = Util.make(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeHierarchyAdapter(Text.class, new Serializer());
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
            } catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", noSuchFieldException);
            }
        });
        private static final Field JSON_READER_LINE_START = Util.make(() -> {
            try {
                new JsonReader(new StringReader(""));
                Field field = JsonReader.class.getDeclaredField("lineStart");
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", noSuchFieldException);
            }
        });

        /*
         * WARNING - void declaration
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public MutableText deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonPrimitive()) {
                return Text.literal(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                MutableText mutableText;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    string = JsonHelper.getString(jsonObject, "text");
                    mutableText = string.isEmpty() ? Text.empty() : Text.literal(string);
                } else if (jsonObject.has("translate")) {
                    string = JsonHelper.getString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        void var9_17;
                        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
                        Object[] objects = new Object[jsonArray.size()];
                        boolean bl = false;
                        while (var9_17 < objects.length) {
                            objects[var9_17] = Serializer.method_43474(this.deserialize(jsonArray.get((int)var9_17), type, jsonDeserializationContext));
                            ++var9_17;
                        }
                        mutableText = Text.translatable(string, objects);
                    } else {
                        mutableText = Text.translatable(string);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    mutableText = Text.score(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                } else if (jsonObject.has("selector")) {
                    Optional<Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    mutableText = Text.selector(JsonHelper.getString(jsonObject, "selector"), optional);
                } else if (jsonObject.has("keybind")) {
                    mutableText = Text.keybind(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    void var9_21;
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    string = JsonHelper.getString(jsonObject, "nbt");
                    Optional<Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        BlockNbtDataSource blockNbtDataSource = new BlockNbtDataSource(JsonHelper.getString(jsonObject, "block"));
                    } else if (jsonObject.has("entity")) {
                        EntityNbtDataSource entityNbtDataSource = new EntityNbtDataSource(JsonHelper.getString(jsonObject, "entity"));
                    } else {
                        if (!jsonObject.has("storage")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        StorageNbtDataSource storageNbtDataSource = new StorageNbtDataSource(new Identifier(JsonHelper.getString(jsonObject, "storage")));
                    }
                    mutableText = Text.nbt(string, bl, optional2, (NbtDataSource)var9_21);
                }
                if (jsonObject.has("extra")) {
                    JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
                    if (jsonArray2.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int j = 0; j < jsonArray2.size(); ++j) {
                        mutableText.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
                    }
                }
                mutableText.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)Style.class)));
                return mutableText;
            }
            if (!jsonElement.isJsonArray()) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
            JsonArray jsonArray3 = jsonElement.getAsJsonArray();
            MutableText mutableText = null;
            for (JsonElement jsonElement2 : jsonArray3) {
                MutableText mutableText2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                if (mutableText == null) {
                    mutableText = mutableText2;
                    continue;
                }
                mutableText.append(mutableText2);
            }
            return mutableText;
        }

        private static Object method_43474(Object object) {
            TextContent textContent;
            Text text;
            if (object instanceof Text && (text = (Text)object).getStyle().isEmpty() && text.getSiblings().isEmpty() && (textContent = text.getContent()) instanceof LiteralTextContent) {
                LiteralTextContent literalTextContent = (LiteralTextContent)textContent;
                return literalTextContent.string();
            }
            return object;
        }

        private Optional<Text> getSeparator(Type type, JsonDeserializationContext context, JsonObject json) {
            if (json.has("separator")) {
                return Optional.of(this.deserialize(json.get("separator"), type, context));
            }
            return Optional.empty();
        }

        private void addStyle(Style style, JsonObject json, JsonSerializationContext context) {
            JsonElement jsonElement = context.serialize(style);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = (JsonObject)jsonElement;
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    json.add(entry.getKey(), entry.getValue());
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public JsonElement serialize(Text text, Type type, JsonSerializationContext jsonSerializationContext) {
            TextContent textContent;
            JsonObject jsonObject = new JsonObject();
            if (!text.getStyle().isEmpty()) {
                this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
            }
            if (!text.getSiblings().isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Text text2 : text.getSiblings()) {
                    jsonArray.add(this.serialize(text2, (Type)((Object)Text.class), jsonSerializationContext));
                }
                jsonObject.add("extra", jsonArray);
            }
            if ((textContent = text.getContent()) == TextContent.EMPTY) {
                jsonObject.addProperty("text", "");
                return jsonObject;
            } else if (textContent instanceof LiteralTextContent) {
                LiteralTextContent literalTextContent = (LiteralTextContent)textContent;
                jsonObject.addProperty("text", literalTextContent.string());
                return jsonObject;
            } else if (textContent instanceof TranslatableTextContent) {
                TranslatableTextContent translatableTextContent = (TranslatableTextContent)textContent;
                jsonObject.addProperty("translate", translatableTextContent.getKey());
                if (translatableTextContent.getArgs().length <= 0) return jsonObject;
                JsonArray jsonArray2 = new JsonArray();
                for (Object object : translatableTextContent.getArgs()) {
                    if (object instanceof Text) {
                        jsonArray2.add(this.serialize((Text)object, (Type)object.getClass(), jsonSerializationContext));
                        continue;
                    }
                    jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
                }
                jsonObject.add("with", jsonArray2);
                return jsonObject;
            } else if (textContent instanceof ScoreTextContent) {
                ScoreTextContent scoreTextContent = (ScoreTextContent)textContent;
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", scoreTextContent.getName());
                jsonObject2.addProperty("objective", scoreTextContent.getObjective());
                jsonObject.add("score", jsonObject2);
                return jsonObject;
            } else if (textContent instanceof SelectorTextContent) {
                SelectorTextContent selectorTextContent = (SelectorTextContent)textContent;
                jsonObject.addProperty("selector", selectorTextContent.getPattern());
                this.addSeparator(jsonSerializationContext, jsonObject, selectorTextContent.getSeparator());
                return jsonObject;
            } else if (textContent instanceof KeybindTextContent) {
                KeybindTextContent keybindTextContent = (KeybindTextContent)textContent;
                jsonObject.addProperty("keybind", keybindTextContent.getKey());
                return jsonObject;
            } else {
                if (!(textContent instanceof NbtTextContent)) throw new IllegalArgumentException("Don't know how to serialize " + textContent + " as a Component");
                NbtTextContent nbtTextContent = (NbtTextContent)textContent;
                jsonObject.addProperty("nbt", nbtTextContent.getPath());
                jsonObject.addProperty("interpret", nbtTextContent.shouldInterpret());
                this.addSeparator(jsonSerializationContext, jsonObject, nbtTextContent.getSeparator());
                NbtDataSource nbtDataSource = nbtTextContent.getDataSource();
                if (nbtDataSource instanceof BlockNbtDataSource) {
                    BlockNbtDataSource blockNbtDataSource = (BlockNbtDataSource)nbtDataSource;
                    jsonObject.addProperty("block", blockNbtDataSource.rawPos());
                    return jsonObject;
                } else if (nbtDataSource instanceof EntityNbtDataSource) {
                    EntityNbtDataSource entityNbtDataSource = (EntityNbtDataSource)nbtDataSource;
                    jsonObject.addProperty("entity", entityNbtDataSource.rawSelector());
                    return jsonObject;
                } else {
                    if (!(nbtDataSource instanceof StorageNbtDataSource)) throw new IllegalArgumentException("Don't know how to serialize " + textContent + " as a Component");
                    StorageNbtDataSource storageNbtDataSource = (StorageNbtDataSource)nbtDataSource;
                    jsonObject.addProperty("storage", storageNbtDataSource.id().toString());
                }
            }
            return jsonObject;
        }

        private void addSeparator(JsonSerializationContext context, JsonObject json, Optional<Text> separator2) {
            separator2.ifPresent(separator -> json.add("separator", this.serialize((Text)separator, (Type)separator.getClass(), context)));
        }

        public static String toJson(Text text) {
            return GSON.toJson(text);
        }

        public static JsonElement toJsonTree(Text text) {
            return GSON.toJsonTree(text);
        }

        @Nullable
        public static MutableText fromJson(String json) {
            return JsonHelper.deserialize(GSON, json, MutableText.class, false);
        }

        @Nullable
        public static MutableText fromJson(JsonElement json) {
            return GSON.fromJson(json, MutableText.class);
        }

        @Nullable
        public static MutableText fromLenientJson(String json) {
            return JsonHelper.deserialize(GSON, json, MutableText.class, true);
        }

        public static MutableText fromJson(com.mojang.brigadier.StringReader reader) {
            try {
                JsonReader jsonReader = new JsonReader(new StringReader(reader.getRemaining()));
                jsonReader.setLenient(false);
                MutableText mutableText = GSON.getAdapter(MutableText.class).read(jsonReader);
                reader.setCursor(reader.getCursor() + Serializer.getPosition(jsonReader));
                return mutableText;
            } catch (IOException | StackOverflowError throwable) {
                throw new JsonParseException(throwable);
            }
        }

        private static int getPosition(JsonReader reader) {
            try {
                return JSON_READER_POS.getInt(reader) - JSON_READER_LINE_START.getInt(reader) + 1;
            } catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalAccessException);
            }
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object text, Type type, JsonSerializationContext context) {
            return this.serialize((Text)text, type, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(json, type, context);
        }
    }
}

