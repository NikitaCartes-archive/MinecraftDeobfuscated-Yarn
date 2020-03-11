/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.operator;

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

    private BoundedIntUnaryOperator(@Nullable Integer min, @Nullable Integer max) {
        this.min = min;
        this.max = max;
        if (min == null) {
            if (max == null) {
                this.operator = i -> i;
            } else {
                int i2 = max;
                this.operator = j -> Math.min(i2, j);
            }
        } else {
            int i3 = min;
            if (max == null) {
                this.operator = j -> Math.max(i3, j);
            } else {
                int j2 = max;
                this.operator = k -> MathHelper.clamp(k, i3, j2);
            }
        }
    }

    public static BoundedIntUnaryOperator create(int min, int max) {
        return new BoundedIntUnaryOperator(min, max);
    }

    public static BoundedIntUnaryOperator createMin(int min) {
        return new BoundedIntUnaryOperator(min, null);
    }

    public static BoundedIntUnaryOperator createMax(int max) {
        return new BoundedIntUnaryOperator(null, max);
    }

    @Override
    public int applyAsInt(int value) {
        return this.operator.applyAsInt(value);
    }

    public static class Serializer
    implements JsonDeserializer<BoundedIntUnaryOperator>,
    JsonSerializer<BoundedIntUnaryOperator> {
        @Override
        public BoundedIntUnaryOperator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "value");
            Integer integer = jsonObject.has("min") ? Integer.valueOf(JsonHelper.getInt(jsonObject, "min")) : null;
            Integer integer2 = jsonObject.has("max") ? Integer.valueOf(JsonHelper.getInt(jsonObject, "max")) : null;
            return new BoundedIntUnaryOperator(integer, integer2);
        }

        @Override
        public JsonElement serialize(BoundedIntUnaryOperator boundedIntUnaryOperator, Type type, JsonSerializationContext jsonSerializationContext) {
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
        public /* synthetic */ JsonElement serialize(Object op, Type type, JsonSerializationContext context) {
            return this.serialize((BoundedIntUnaryOperator)op, type, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

