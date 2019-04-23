/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.NbtComponent;
import net.minecraft.network.chat.ScoreComponent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

public interface Component
extends Message,
Iterable<Component> {
    public Component setStyle(Style var1);

    public Style getStyle();

    default public Component append(String string) {
        return this.append(new TextComponent(string));
    }

    public Component append(Component var1);

    public String getText();

    @Override
    default public String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        this.stream().forEach(component -> stringBuilder.append(component.getText()));
        return stringBuilder.toString();
    }

    default public String getStringTruncated(int i) {
        int j;
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = this.stream().iterator();
        while (iterator.hasNext() && (j = i - stringBuilder.length()) > 0) {
            String string = ((Component)iterator.next()).getText();
            stringBuilder.append(string.length() <= j ? string : string.substring(0, j));
        }
        return stringBuilder.toString();
    }

    default public String getFormattedText() {
        StringBuilder stringBuilder = new StringBuilder();
        String string = "";
        Iterator iterator = this.stream().iterator();
        while (iterator.hasNext()) {
            Component component = (Component)iterator.next();
            String string2 = component.getText();
            if (string2.isEmpty()) continue;
            String string3 = component.getStyle().getFormatString();
            if (!string3.equals(string)) {
                if (!string.isEmpty()) {
                    stringBuilder.append((Object)ChatFormat.RESET);
                }
                stringBuilder.append(string3);
                string = string3;
            }
            stringBuilder.append(string2);
        }
        if (!string.isEmpty()) {
            stringBuilder.append((Object)ChatFormat.RESET);
        }
        return stringBuilder.toString();
    }

    public List<Component> getSiblings();

    public Stream<Component> stream();

    default public Stream<Component> streamCopied() {
        return this.stream().map(Component::copyWithoutChildren);
    }

    @Override
    default public Iterator<Component> iterator() {
        return this.streamCopied().iterator();
    }

    public Component copyShallow();

    default public Component copy() {
        Component component = this.copyShallow();
        component.setStyle(this.getStyle().clone());
        for (Component component2 : this.getSiblings()) {
            component.append(component2.copy());
        }
        return component;
    }

    default public Component modifyStyle(Consumer<Style> consumer) {
        consumer.accept(this.getStyle());
        return this;
    }

    default public Component applyFormat(ChatFormat ... chatFormats) {
        for (ChatFormat chatFormat : chatFormats) {
            this.applyFormat(chatFormat);
        }
        return this;
    }

    default public Component applyFormat(ChatFormat chatFormat) {
        Style style = this.getStyle();
        if (chatFormat.isColor()) {
            style.setColor(chatFormat);
        }
        if (chatFormat.isModifier()) {
            switch (chatFormat) {
                case OBFUSCATED: {
                    style.setObfuscated(true);
                    break;
                }
                case BOLD: {
                    style.setBold(true);
                    break;
                }
                case STRIKETHROUGH: {
                    style.setStrikethrough(true);
                    break;
                }
                case UNDERLINE: {
                    style.setUnderline(true);
                    break;
                }
                case ITALIC: {
                    style.setItalic(true);
                    break;
                }
            }
        }
        return this;
    }

    public static Component copyWithoutChildren(Component component) {
        Component component2 = component.copyShallow();
        component2.setStyle(component.getStyle().copy());
        return component2;
    }

    public static class Serializer
    implements JsonDeserializer<Component>,
    JsonSerializer<Component> {
        private static final Gson GSON = SystemUtil.get(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.registerTypeHierarchyAdapter(Component.class, new Serializer());
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
            } catch (NoSuchFieldException noSuchFieldException) {
                throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", noSuchFieldException);
            }
        });
        private static final Field LINE_START_FIELD = SystemUtil.get(() -> {
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
        public Component method_10871(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            void var5_18;
            if (jsonElement.isJsonPrimitive()) {
                return new TextComponent(jsonElement.getAsString());
            }
            if (jsonElement.isJsonObject()) {
                void var5_16;
                String string;
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("text")) {
                    TextComponent textComponent = new TextComponent(jsonObject.get("text").getAsString());
                } else if (jsonObject.has("translate")) {
                    string = jsonObject.get("translate").getAsString();
                    if (jsonObject.has("with")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("with");
                        Object[] objects = new Object[jsonArray.size()];
                        for (int i = 0; i < objects.length; ++i) {
                            TextComponent textComponent;
                            objects[i] = this.method_10871(jsonArray.get(i), type, jsonDeserializationContext);
                            if (!(objects[i] instanceof TextComponent) || !(textComponent = (TextComponent)objects[i]).getStyle().isEmpty() || !textComponent.getSiblings().isEmpty()) continue;
                            objects[i] = textComponent.getTextField();
                        }
                        TranslatableComponent translatableComponent = new TranslatableComponent(string, objects);
                    } else {
                        TranslatableComponent translatableComponent = new TranslatableComponent(string, new Object[0]);
                    }
                } else if (jsonObject.has("score")) {
                    JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
                    if (!jsonObject2.has("name") || !jsonObject2.has("objective")) throw new JsonParseException("A score component needs a least a name and an objective");
                    ScoreComponent scoreComponent = new ScoreComponent(JsonHelper.getString(jsonObject2, "name"), JsonHelper.getString(jsonObject2, "objective"));
                    if (jsonObject2.has("value")) {
                        scoreComponent.setText(JsonHelper.getString(jsonObject2, "value"));
                    }
                } else if (jsonObject.has("selector")) {
                    SelectorComponent selectorComponent = new SelectorComponent(JsonHelper.getString(jsonObject, "selector"));
                } else if (jsonObject.has("keybind")) {
                    KeybindComponent keybindComponent = new KeybindComponent(JsonHelper.getString(jsonObject, "keybind"));
                } else {
                    if (!jsonObject.has("nbt")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                    string = JsonHelper.getString(jsonObject, "nbt");
                    boolean bl = JsonHelper.getBoolean(jsonObject, "interpret", false);
                    if (jsonObject.has("block")) {
                        NbtComponent.BlockPosArgument blockPosArgument = new NbtComponent.BlockPosArgument(string, bl, JsonHelper.getString(jsonObject, "block"));
                    } else {
                        if (!jsonObject.has("entity")) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
                        NbtComponent.EntityNbtComponent entityNbtComponent = new NbtComponent.EntityNbtComponent(string, bl, JsonHelper.getString(jsonObject, "entity"));
                    }
                }
                if (jsonObject.has("extra")) {
                    JsonArray jsonArray2 = jsonObject.getAsJsonArray("extra");
                    if (jsonArray2.size() <= 0) throw new JsonParseException("Unexpected empty array of components");
                    for (int j = 0; j < jsonArray2.size(); ++j) {
                        var5_16.append(this.method_10871(jsonArray2.get(j), type, jsonDeserializationContext));
                    }
                }
                var5_16.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)Style.class)));
                return var5_16;
            }
            if (!jsonElement.isJsonArray()) throw new JsonParseException("Don't know how to turn " + jsonElement + " into a Component");
            JsonArray jsonArray3 = jsonElement.getAsJsonArray();
            Object var5_17 = null;
            for (JsonElement jsonElement2 : jsonArray3) {
                Component component2 = this.method_10871(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                if (var5_18 == null) {
                    Component component = component2;
                    continue;
                }
                var5_18.append(component2);
            }
            return var5_18;
        }

        private void addStyle(Style style, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            JsonElement jsonElement = jsonSerializationContext.serialize(style);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject2 = (JsonObject)jsonElement;
                for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                    jsonObject.add(entry.getKey(), entry.getValue());
                }
            }
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
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
                return jsonObject;
            } else if (component instanceof TranslatableComponent) {
                TranslatableComponent translatableComponent = (TranslatableComponent)component;
                jsonObject.addProperty("translate", translatableComponent.getKey());
                if (translatableComponent.getParams() == null || translatableComponent.getParams().length <= 0) return jsonObject;
                JsonArray jsonArray2 = new JsonArray();
                for (Object object : translatableComponent.getParams()) {
                    if (object instanceof Component) {
                        jsonArray2.add(this.method_10874((Component)object, object.getClass(), jsonSerializationContext));
                        continue;
                    }
                    jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
                }
                jsonObject.add("with", jsonArray2);
                return jsonObject;
            } else if (component instanceof ScoreComponent) {
                ScoreComponent scoreComponent = (ScoreComponent)component;
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("name", scoreComponent.getName());
                jsonObject2.addProperty("objective", scoreComponent.getObjective());
                jsonObject2.addProperty("value", scoreComponent.getText());
                jsonObject.add("score", jsonObject2);
                return jsonObject;
            } else if (component instanceof SelectorComponent) {
                SelectorComponent selectorComponent = (SelectorComponent)component;
                jsonObject.addProperty("selector", selectorComponent.getPattern());
                return jsonObject;
            } else if (component instanceof KeybindComponent) {
                KeybindComponent keybindComponent = (KeybindComponent)component;
                jsonObject.addProperty("keybind", keybindComponent.getKeybind());
                return jsonObject;
            } else {
                if (!(component instanceof NbtComponent)) throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
                NbtComponent nbtComponent = (NbtComponent)component;
                jsonObject.addProperty("nbt", nbtComponent.getPath());
                jsonObject.addProperty("interpret", nbtComponent.isComponentJson());
                if (component instanceof NbtComponent.BlockPosArgument) {
                    NbtComponent.BlockPosArgument blockPosArgument = (NbtComponent.BlockPosArgument)component;
                    jsonObject.addProperty("block", blockPosArgument.getPos());
                    return jsonObject;
                } else {
                    if (!(component instanceof NbtComponent.EntityNbtComponent)) throw new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
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
                Component component = GSON.getAdapter(Component.class).read(jsonReader);
                stringReader.setCursor(stringReader.getCursor() + Serializer.getReaderPosition(jsonReader));
                return component;
            } catch (IOException iOException) {
                throw new JsonParseException(iOException);
            }
        }

        private static int getReaderPosition(JsonReader jsonReader) {
            try {
                return POS_FIELD.getInt(jsonReader) - LINE_START_FIELD.getInt(jsonReader) + 1;
            } catch (IllegalAccessException illegalAccessException) {
                throw new IllegalStateException("Couldn't read position of JsonReader", illegalAccessException);
            }
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.method_10874((Component)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_10871(jsonElement, type, jsonDeserializationContext);
        }
    }
}

