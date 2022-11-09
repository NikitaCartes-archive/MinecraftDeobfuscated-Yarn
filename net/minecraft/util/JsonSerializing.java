/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.function.Function;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializableType;
import org.jetbrains.annotations.Nullable;

public class JsonSerializing {
    public static <E, T extends JsonSerializableType<E>> SerializerBuilder<E, T> createSerializerBuilder(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeGetter) {
        return new SerializerBuilder<E, T>(registry, rootFieldName, idFieldName, typeGetter);
    }

    public static class SerializerBuilder<E, T extends JsonSerializableType<E>> {
        private final Registry<T> registry;
        private final String rootFieldName;
        private final String idFieldName;
        private final Function<E, T> typeGetter;
        @Nullable
        private Pair<T, ElementSerializer<? extends E>> elementSerializer;
        @Nullable
        private T defaultType;

        SerializerBuilder(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification) {
            this.registry = registry;
            this.rootFieldName = rootFieldName;
            this.idFieldName = idFieldName;
            this.typeGetter = typeIdentification;
        }

        public SerializerBuilder<E, T> elementSerializer(T type, ElementSerializer<? extends E> serializer) {
            this.elementSerializer = Pair.of(type, serializer);
            return this;
        }

        public SerializerBuilder<E, T> defaultType(T defaultType) {
            this.defaultType = defaultType;
            return this;
        }

        public Object build() {
            return new GsonSerializer<E, T>(this.registry, this.rootFieldName, this.idFieldName, this.typeGetter, this.defaultType, this.elementSerializer);
        }
    }

    public static interface ElementSerializer<T> {
        public JsonElement toJson(T var1, JsonSerializationContext var2);

        public T fromJson(JsonElement var1, JsonDeserializationContext var2);
    }

    static class GsonSerializer<E, T extends JsonSerializableType<E>>
    implements JsonDeserializer<E>,
    JsonSerializer<E> {
        private final Registry<T> registry;
        private final String rootFieldName;
        private final String idFieldName;
        private final Function<E, T> typeGetter;
        @Nullable
        private final T defaultType;
        @Nullable
        private final Pair<T, ElementSerializer<? extends E>> elementSerializer;

        GsonSerializer(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeGetter, @Nullable T defaultType, @Nullable Pair<T, ElementSerializer<? extends E>> elementSerializer) {
            this.registry = registry;
            this.rootFieldName = rootFieldName;
            this.idFieldName = idFieldName;
            this.typeGetter = typeGetter;
            this.defaultType = defaultType;
            this.elementSerializer = elementSerializer;
        }

        @Override
        public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                Object jsonSerializableType;
                JsonObject jsonObject = JsonHelper.asObject(json, this.rootFieldName);
                String string = JsonHelper.getString(jsonObject, this.idFieldName, "");
                if (string.isEmpty()) {
                    jsonSerializableType = this.defaultType;
                } else {
                    Identifier identifier = new Identifier(string);
                    jsonSerializableType = (JsonSerializableType)this.registry.get(identifier);
                }
                if (jsonSerializableType == null) {
                    throw new JsonSyntaxException("Unknown type '" + string + "'");
                }
                return (E)((JsonSerializableType)jsonSerializableType).getJsonSerializer().fromJson(jsonObject, context);
            }
            if (this.elementSerializer == null) {
                throw new UnsupportedOperationException("Object " + json + " can't be deserialized");
            }
            return this.elementSerializer.getSecond().fromJson(json, context);
        }

        @Override
        public JsonElement serialize(E object, Type type, JsonSerializationContext context) {
            JsonSerializableType jsonSerializableType = (JsonSerializableType)this.typeGetter.apply(object);
            if (this.elementSerializer != null && this.elementSerializer.getFirst() == jsonSerializableType) {
                return this.elementSerializer.getSecond().toJson(object, context);
            }
            if (jsonSerializableType == null) {
                throw new JsonSyntaxException("Unknown type: " + object);
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(this.idFieldName, this.registry.getId(jsonSerializableType).toString());
            jsonSerializableType.getJsonSerializer().toJson(jsonObject, object, context);
            return jsonObject;
        }
    }
}

