/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@Environment(value=EnvType.CLIENT)
public class ModelOverride {
    private final Identifier modelId;
    private final List<Condition> conditions;

    public ModelOverride(Identifier modelId, List<Condition> conditions) {
        this.modelId = modelId;
        this.conditions = ImmutableList.copyOf(conditions);
    }

    public Identifier getModelId() {
        return this.modelId;
    }

    public Stream<Condition> streamConditions() {
        return this.conditions.stream();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Condition {
        private final Identifier type;
        private final float threshold;

        public Condition(Identifier type, float threshold) {
            this.type = type;
            this.threshold = threshold;
        }

        public Identifier getType() {
            return this.type;
        }

        public float getThreshold() {
            return this.threshold;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<ModelOverride> {
        protected Deserializer() {
        }

        @Override
        public ModelOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "model"));
            List<Condition> list = this.deserializeMinPropertyValues(jsonObject);
            return new ModelOverride(identifier, list);
        }

        protected List<Condition> deserializeMinPropertyValues(JsonObject object) {
            LinkedHashMap<Identifier, Float> map = Maps.newLinkedHashMap();
            JsonObject jsonObject = JsonHelper.getObject(object, "predicate");
            for (Map.Entry<String, JsonElement> entry2 : jsonObject.entrySet()) {
                map.put(new Identifier(entry2.getKey()), Float.valueOf(JsonHelper.asFloat(entry2.getValue(), entry2.getKey())));
            }
            return map.entrySet().stream().map(entry -> new Condition((Identifier)entry.getKey(), ((Float)entry.getValue()).floatValue())).collect(ImmutableList.toImmutableList());
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

