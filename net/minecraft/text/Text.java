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
import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.NbtText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.ScoreText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
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
     * Returns the string representation of this text itself, excluding siblings.
     */
    public String asString();

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
    public MutableText copy();

    /**
     * Copies the text itself, the style, and the siblings.
     * 
     * <p>A shallow copy is made for the siblings.
     */
    public MutableText shallowCopy();

    public OrderedText asOrderedText();

    @Override
    default public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        Style style2 = this.getStyle().withParent(style);
        Optional<T> optional = this.visitSelf(styledVisitor, style2);
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
        Optional<T> optional = this.visitSelf(visitor);
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

    /**
     * Visits the text itself.
     * 
     * @see #visit(StyledVisitor, Style)
     * @return the visitor's return value
     * 
     * @param visitor the visitor
     * @param style the current style
     */
    default public <T> Optional<T> visitSelf(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, this.asString());
    }

    /**
     * Visits the text itself.
     * 
     * @see #visit(Visitor)
     * @return the visitor's return value
     * 
     * @param visitor the visitor
     */
    default public <T> Optional<T> visitSelf(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.asString());
    }

    default public List<Text> getWithStyle(Style style) {
        ArrayList<Text> list = Lists.newArrayList();
        this.visit((styleOverride, text) -> {
            if (!text.isEmpty()) {
                list.add(new LiteralText(text).fillStyle(styleOverride));
            }
            return Optional.empty();
        }, style);
        return list;
    }

    /**
     * Creates a literal text with the given string as content.
     */
    public static Text of(@Nullable String string) {
        return string != null ? new LiteralText(string) : LiteralText.EMPTY;
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
            void var5_19;
            if (jsonElement.isJsonPrimitive()) {
                return new LiteralText(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                void var5_17;
                String string;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    LiteralText literalText = new LiteralText(JsonHelper.getString(jsonObject, "text"));
                } else if (jsonObject.has("translate")) {
                    string = JsonHelper.getString(jsonObject, "translate");
                    if (jsonObject.has("with")) {
                        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "with");
                        Object[] objects = new Object[jsonArray.size()];
                        for (int i = 0; i < objects.length; ++i) {
                            LiteralText literalText;
                            objects[i] = this.deserialize(jsonArray.get(i), type, jsonDeserializationContext);
                            if (!(objects[i] instanceof LiteralText) || !(literalText = (LiteralText)objects[i]).getStyle().isEmpty() || !literalText.getSiblings().isEmpty()) continue;
                            objects[i] = literalText.getRawString();
                        }
                        TranslatableText translatableText = new TranslatableText(string, objects);
                    } else {
                        TranslatableText translatableText = new TranslatableText(string);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    ScoreText scoreText = new ScoreText(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                } else if (jsonObject.has("selector")) {
                    Optional<Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    SelectorText selectorText = new SelectorText(JsonHelper.getString(jsonObject, "selector"), optional);
                } else if (jsonObject.has("keybind")) {
                    KeybindText keybindText = new KeybindText(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    string = JsonHelper.getString(jsonObject, "nbt");
                    Optional<Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        NbtText.BlockNbtText blockNbtText = new NbtText.BlockNbtText(string, bl, JsonHelper.getString(jsonObject, "block"), optional2);
                    } else if (jsonObject.has("entity")) {
                        NbtText.EntityNbtText entityNbtText = new NbtText.EntityNbtText(string, bl, JsonHelper.getString(jsonObject, "entity"), optional2);
                    } else {
                        if (!jsonObject.has("storage")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        NbtText.StorageNbtText storageNbtText = new NbtText.StorageNbtText(string, bl, new Identifier(JsonHelper.getString(jsonObject, "storage")), optional2);
                    }
                }
                if (jsonObject.has("extra")) {
                    JsonArray jsonArray2 = JsonHelper.getArray(jsonObject, "extra");
                    if (jsonArray2.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int j = 0; j < jsonArray2.size(); ++j) {
                        var5_17.append(this.deserialize(jsonArray2.get(j), type, jsonDeserializationContext));
                    }
                }
                var5_17.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)Style.class)));
                return var5_17;
            }
            if (!jsonElement.isJsonArray()) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
            JsonArray jsonArray3 = jsonElement.getAsJsonArray();
            Object var5_18 = null;
            for (JsonElement jsonElement2 : jsonArray3) {
                MutableText mutableText2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                if (var5_19 == null) {
                    MutableText mutableText = mutableText2;
                    continue;
                }
                var5_19.append(mutableText2);
            }
            return var5_19;
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
            JsonObject jsonObject = new JsonObject();
            if (!text.getStyle().isEmpty()) {
                this.addStyle(text.getStyle(), jsonObject, jsonSerializationContext);
            }
            if (!text.getSiblings().isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Text text2 : text.getSiblings()) {
                    jsonArray.add(this.serialize(text2, (Type)text2.getClass(), jsonSerializationContext));
                }
                jsonObject.add("extra", jsonArray);
            }
            if (text instanceof LiteralText) {
                jsonObject.addProperty("text", ((LiteralText)text).getRawString());
                return jsonObject;
            } else if (text instanceof TranslatableText) {
                TranslatableText translatableText = (TranslatableText)text;
                jsonObject.addProperty("translate", translatableText.getKey());
                if (translatableText.getArgs() == null || translatableText.getArgs().length <= 0) return jsonObject;
                JsonArray jsonArray2 = new JsonArray();
                for (Object object : translatableText.getArgs()) {
                    if (object instanceof Text) {
                        jsonArray2.add(this.serialize((Text)object, (Type)object.getClass(), jsonSerializationContext));
                        continue;
                    }
                    jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
                }
                jsonObject.add("with", jsonArray2);
                return jsonObject;
            } else if (text instanceof ScoreText) {
                ScoreText scoreText = (ScoreText)text;
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", scoreText.getName());
                jsonObject2.addProperty("objective", scoreText.getObjective());
                jsonObject.add("score", jsonObject2);
                return jsonObject;
            } else if (text instanceof SelectorText) {
                SelectorText selectorText = (SelectorText)text;
                jsonObject.addProperty("selector", selectorText.getPattern());
                this.addSeparator(jsonSerializationContext, jsonObject, selectorText.getSeparator());
                return jsonObject;
            } else if (text instanceof KeybindText) {
                KeybindText keybindText = (KeybindText)text;
                jsonObject.addProperty("keybind", keybindText.getKey());
                return jsonObject;
            } else {
                if (!(text instanceof NbtText)) throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
                NbtText nbtText = (NbtText)text;
                jsonObject.addProperty("nbt", nbtText.getPath());
                jsonObject.addProperty("interpret", nbtText.shouldInterpret());
                this.addSeparator(jsonSerializationContext, jsonObject, nbtText.separator);
                if (text instanceof NbtText.BlockNbtText) {
                    NbtText.BlockNbtText blockNbtText = (NbtText.BlockNbtText)text;
                    jsonObject.addProperty("block", blockNbtText.getPos());
                    return jsonObject;
                } else if (text instanceof NbtText.EntityNbtText) {
                    NbtText.EntityNbtText entityNbtText = (NbtText.EntityNbtText)text;
                    jsonObject.addProperty("entity", entityNbtText.getSelector());
                    return jsonObject;
                } else {
                    if (!(text instanceof NbtText.StorageNbtText)) throw new IllegalArgumentException("Don't know how to serialize " + text + " as a Component");
                    NbtText.StorageNbtText storageNbtText = (NbtText.StorageNbtText)text;
                    jsonObject.addProperty("storage", storageNbtText.getId().toString());
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

