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
import net.minecraft.class_7417;
import net.minecraft.class_7419;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.BlockNbtText;
import net.minecraft.text.EntityNbtText;
import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.NbtText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.ScoreText;
import net.minecraft.text.SelectorText;
import net.minecraft.text.StorageNbtText;
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
    public class_7417 asString();

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
        return MutableText.method_43477(this.asString());
    }

    /**
     * Copies the text itself, the style, and the siblings.
     * 
     * <p>A shallow copy is made for the siblings.
     */
    default public MutableText shallowCopy() {
        return new MutableText(this.asString(), new ArrayList<Text>(this.getSiblings()), this.getStyle());
    }

    public OrderedText asOrderedText();

    @Override
    default public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        Style style2 = this.getStyle().withParent(style);
        Optional<T> optional = this.asString().visitSelf(styledVisitor, style2);
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
        Optional<T> optional = this.asString().visitSelf(visitor);
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
                list.add(Text.method_43470(text).fillStyle(styleOverride));
            }
            return Optional.empty();
        }, style);
        return list;
    }

    /**
     * Creates a literal text with the given string as content.
     */
    public static Text of(@Nullable String string) {
        return string != null ? Text.method_43470(string) : ScreenTexts.field_39003;
    }

    public static MutableText method_43470(String string) {
        return MutableText.method_43477(new LiteralText(string));
    }

    public static MutableText method_43471(String string) {
        return MutableText.method_43477(new TranslatableText(string));
    }

    public static MutableText method_43469(String string, Object ... objects) {
        return MutableText.method_43477(new TranslatableText(string, objects));
    }

    public static MutableText method_43473() {
        return MutableText.method_43477(class_7417.field_39004);
    }

    public static MutableText method_43472(String string) {
        return MutableText.method_43477(new KeybindText(string));
    }

    public static MutableText method_43468(String string, boolean bl, Optional<Text> optional, class_7419 arg) {
        return MutableText.method_43477(new NbtText(string, bl, optional, arg));
    }

    public static MutableText method_43466(String string, String string2) {
        return MutableText.method_43477(new ScoreText(string, string2));
    }

    public static MutableText method_43467(String string, Optional<Text> optional) {
        return MutableText.method_43477(new SelectorText(string, optional));
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
                return Text.method_43470(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                MutableText mutableText;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    string = JsonHelper.getString(jsonObject, "text");
                    mutableText = string.isEmpty() ? Text.method_43473() : Text.method_43470(string);
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
                        mutableText = Text.method_43469(string, objects);
                    } else {
                        mutableText = Text.method_43471(string);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    mutableText = Text.method_43466(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                } else if (jsonObject.has("selector")) {
                    Optional<Text> optional = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    mutableText = Text.method_43467(JsonHelper.getString(jsonObject, "selector"), optional);
                } else if (jsonObject.has("keybind")) {
                    mutableText = Text.method_43472(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    void var9_21;
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    string = JsonHelper.getString(jsonObject, "nbt");
                    Optional<Text> optional2 = this.getSeparator(type, jsonDeserializationContext, jsonObject);
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        BlockNbtText blockNbtText = new BlockNbtText(JsonHelper.getString(jsonObject, "block"));
                    } else if (jsonObject.has("entity")) {
                        EntityNbtText entityNbtText = new EntityNbtText(JsonHelper.getString(jsonObject, "entity"));
                    } else {
                        if (!jsonObject.has("storage")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        StorageNbtText storageNbtText = new StorageNbtText(new Identifier(JsonHelper.getString(jsonObject, "storage")));
                    }
                    mutableText = Text.method_43468(string, bl, optional2, (class_7419)var9_21);
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
            class_7417 lv;
            Text text;
            if (object instanceof Text && (text = (Text)object).getStyle().isEmpty() && text.getSiblings().isEmpty() && (lv = text.asString()) instanceof LiteralText) {
                LiteralText literalText = (LiteralText)lv;
                return literalText.string();
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
            class_7417 lv;
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
            if ((lv = text.asString()) == class_7417.field_39004) {
                jsonObject.addProperty("text", "");
                return jsonObject;
            } else if (lv instanceof LiteralText) {
                LiteralText literalText = (LiteralText)lv;
                jsonObject.addProperty("text", literalText.string());
                return jsonObject;
            } else if (lv instanceof TranslatableText) {
                TranslatableText translatableText = (TranslatableText)lv;
                jsonObject.addProperty("translate", translatableText.getKey());
                if (translatableText.getArgs().length <= 0) return jsonObject;
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
            } else if (lv instanceof ScoreText) {
                ScoreText scoreText = (ScoreText)lv;
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", scoreText.getName());
                jsonObject2.addProperty("objective", scoreText.getObjective());
                jsonObject.add("score", jsonObject2);
                return jsonObject;
            } else if (lv instanceof SelectorText) {
                SelectorText selectorText = (SelectorText)lv;
                jsonObject.addProperty("selector", selectorText.getPattern());
                this.addSeparator(jsonSerializationContext, jsonObject, selectorText.getSeparator());
                return jsonObject;
            } else if (lv instanceof KeybindText) {
                KeybindText keybindText = (KeybindText)lv;
                jsonObject.addProperty("keybind", keybindText.getKey());
                return jsonObject;
            } else {
                if (!(lv instanceof NbtText)) throw new IllegalArgumentException("Don't know how to serialize " + lv + " as a Component");
                NbtText nbtText = (NbtText)lv;
                jsonObject.addProperty("nbt", nbtText.getPath());
                jsonObject.addProperty("interpret", nbtText.shouldInterpret());
                this.addSeparator(jsonSerializationContext, jsonObject, nbtText.method_43484());
                class_7419 lv2 = nbtText.method_43485();
                if (lv2 instanceof BlockNbtText) {
                    BlockNbtText blockNbtText = (BlockNbtText)lv2;
                    jsonObject.addProperty("block", blockNbtText.rawPos());
                    return jsonObject;
                } else if (lv2 instanceof EntityNbtText) {
                    EntityNbtText entityNbtText = (EntityNbtText)lv2;
                    jsonObject.addProperty("entity", entityNbtText.rawSelector());
                    return jsonObject;
                } else {
                    if (!(lv2 instanceof StorageNbtText)) throw new IllegalArgumentException("Don't know how to serialize " + lv + " as a Component");
                    StorageNbtText storageNbtText = (StorageNbtText)lv2;
                    jsonObject.addProperty("storage", storageNbtText.id().toString());
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

