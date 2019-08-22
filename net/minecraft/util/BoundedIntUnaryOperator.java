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
import java.lang.reflect.Type;
import java.util.function.IntUnaryOperator;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class BoundedIntUnaryOperator
implements IntUnaryOperator {
    private final Integer min;
    private final Integer max;
    private final IntUnaryOperator operator;

    private BoundedIntUnaryOperator(@Nullable Integer integer, @Nullable Integer integer2) {
        this.min = integer;
        this.max = integer2;
        if (integer == null) {
            if (integer2 == null) {
                this.operator = i -> i;
            } else {
                int i2 = integer2;
                this.operator = j -> Math.min(i2, j);
            }
        } else {
            int i3 = integer;
            if (integer2 == null) {
                this.operator = j -> Math.max(i3, j);
            } else {
                int j2 = integer2;
                this.operator = k -> MathHelper.clamp(k, i3, j2);
            }
        }
    }

    public static BoundedIntUnaryOperator create(int i, int j) {
        return new BoundedIntUnaryOperator(i, j);
    }

    public static BoundedIntUnaryOperator createMin(int i) {
        return new BoundedIntUnaryOperator(i, null);
    }

    public static BoundedIntUnaryOperator createMax(int i) {
        return new BoundedIntUnaryOperator(null, i);
    }

    @Override
    public int applyAsInt(int i) {
        return this.operator.applyAsInt(i);
    }

    public static class Serializer
    implements JsonDeserializer<BoundedIntUnaryOperator>,
    JsonSerializer<BoundedIntUnaryOperator> {
        public BoundedIntUnaryOperator method_286(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
            Integer integer = jsonObject.has("min") ? Integer.valueOf(JsonHelper.getInt(jsonObject, "min")) : null;
            Integer integer2 = jsonObject.has("max") ? Integer.valueOf(JsonHelper.getInt(jsonObject, "max")) : null;
            return new BoundedIntUnaryOperator(integer, integer2);
        }

        public JsonElement method_287(BoundedIntUnaryOperator boundedIntUnaryOperator, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (boundedIntUnaryOperator.max != null) {
                jsonObject.addProperty("max", boundedIntUnaryOperator.max);
            }
            if (boundedIntUnaryOperator.min != null) {
                jsonObject.addProperty("min", boundedIntUnaryOperator.min);
            }
            return jsonObject;
        }

        @Override
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.method_287((BoundedIntUnaryOperator)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_286(jsonElement, type, jsonDeserializationContext);
        }
    }
}

