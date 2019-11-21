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

    public ModelItemOverride(Identifier identifier, Map<Identifier, Float> map) {
        this.modelId = identifier;
        this.minPropertyValues = map;
    }

    public Identifier getModelId() {
        return this.modelId;
    }

    boolean matches(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        for (Map.Entry<Identifier, Float> entry : this.minPropertyValues.entrySet()) {
            ItemPropertyGetter itemPropertyGetter = item.getPropertyGetter(entry.getKey());
            if (itemPropertyGetter != null && !(itemPropertyGetter.call(itemStack, world, livingEntity) < entry.getValue().floatValue())) continue;
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
        public ModelItemOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "model"));
            Map<Identifier, Float> map = this.deserializeMinPropertyValues(jsonObject);
            return new ModelItemOverride(identifier, map);
        }

        protected Map<Identifier, Float> deserializeMinPropertyValues(JsonObject jsonObject) {
            LinkedHashMap<Identifier, Float> map = Maps.newLinkedHashMap();
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "predicate");
            for (Map.Entry<String, JsonElement> entry : jsonObject2.entrySet()) {
                map.put(new Identifier(entry.getKey()), Float.valueOf(JsonHelper.asFloat(entry.getValue(), entry.getKey())));
            }
            return map;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

