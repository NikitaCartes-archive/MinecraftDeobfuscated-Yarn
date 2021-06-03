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
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class JsonSerializing {
    public static <E, T extends JsonSerializableType<E>> TypeHandler<E, T> createTypeHandler(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification) {
        return new TypeHandler<E, T>(registry, rootFieldName, idFieldName, typeIdentification);
    }

    public static class TypeHandler<E, T extends JsonSerializableType<E>> {
        private final Registry<T> registry;
        private final String rootFieldName;
        private final String idFieldName;
        private final Function<E, T> typeIdentification;
        @Nullable
        private Pair<T, CustomSerializer<? extends E>> customSerializer;
        @Nullable
        private T field_28444;

        TypeHandler(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification) {
            this.registry = registry;
            this.rootFieldName = rootFieldName;
            this.idFieldName = idFieldName;
            this.typeIdentification = typeIdentification;
        }

        public TypeHandler<E, T> method_32385(T jsonSerializableType, CustomSerializer<? extends E> customSerializer) {
            this.customSerializer = Pair.of(jsonSerializableType, customSerializer);
            return this;
        }

        public TypeHandler<E, T> method_33409(T jsonSerializableType) {
            this.field_28444 = jsonSerializableType;
            return this;
        }

        public Object createGsonSerializer() {
            return new GsonSerializer<E, T>(this.registry, this.rootFieldName, this.idFieldName, this.typeIdentification, this.field_28444, this.customSerializer);
        }
    }

    public static interface CustomSerializer<T> {
        public JsonElement toJson(T var1, JsonSerializationContext var2);

        public T fromJson(JsonElement var1, JsonDeserializationContext var2);
    }

    static class GsonSerializer<E, T extends JsonSerializableType<E>>
    implements JsonDeserializer<E>,
    JsonSerializer<E> {
        private final Registry<T> registry;
        private final String rootFieldName;
        private final String idFieldName;
        private final Function<E, T> typeIdentification;
        @Nullable
        private final T field_28445;
        @Nullable
        private final Pair<T, CustomSerializer<? extends E>> elementSerializer;

        GsonSerializer(Registry<T> registry, String rootFieldName, String idFieldName, Function<E, T> typeIdentification, @Nullable T jsonSerializableType, @Nullable Pair<T, CustomSerializer<? extends E>> elementSerializer) {
            this.registry = registry;
            this.rootFieldName = rootFieldName;
            this.idFieldName = idFieldName;
            this.typeIdentification = typeIdentification;
            this.field_28445 = jsonSerializableType;
            this.elementSerializer = elementSerializer;
        }

        @Override
        public E deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                Object jsonSerializableType;
                JsonObject jsonObject = JsonHelper.asObject(json, this.rootFieldName);
                String string = JsonHelper.getString(jsonObject, this.idFieldName, "");
                if (string.isEmpty()) {
                    jsonSerializableType = this.field_28445;
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
            JsonSerializableType jsonSerializableType = (JsonSerializableType)this.typeIdentification.apply(object);
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

