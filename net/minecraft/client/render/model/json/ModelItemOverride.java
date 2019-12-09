/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelItemOverride {
    private final Identifier modelId;
    private final Map<Identifier, Float> minPropertyValues;

    public ModelItemOverride(Identifier modelId, Map<Identifier, Float> minPropertyValues) {
        this.modelId = modelId;
        this.minPropertyValues = minPropertyValues;
    }

    public Identifier getModelId() {
        return this.modelId;
    }

    boolean matches(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
        Item item = stack.getItem();
        for (Map.Entry<Identifier, Float> entry : this.minPropertyValues.entrySet()) {
            ItemPropertyGetter itemPropertyGetter = item.getPropertyGetter(entry.getKey());
            if (itemPropertyGetter != null && !(itemPropertyGetter.call(stack, world, entity) < entry.getValue().floatValue())) continue;
            return false;
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<ModelItemOverride> {
        protected Deserializer() {
        }

        @Override
        public ModelItemOverride deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = element.getAsJsonObject();
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "model"));
            Map<Identifier, Float> map = this.deserializeMinPropertyValues(jsonObject);
            return new ModelItemOverride(identifier, map);
        }

        protected Map<Identifier, Float> deserializeMinPropertyValues(JsonObject object) {
            LinkedHashMap<Identifier, Float> map = Maps.newLinkedHashMap();
            JsonObject jsonObject = JsonHelper.getObject(object, "predicate");
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                map.put(new Identifier(entry.getKey()), Float.valueOf(JsonHelper.asFloat(entry.getValue(), entry.getKey())));
            }
            return map;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

