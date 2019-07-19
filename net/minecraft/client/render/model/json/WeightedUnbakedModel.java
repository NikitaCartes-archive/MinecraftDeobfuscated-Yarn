/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WeightedUnbakedModel
implements UnbakedModel {
    private final List<ModelVariant> variants;

    public WeightedUnbakedModel(List<ModelVariant> list) {
        this.variants = list;
    }

    public List<ModelVariant> getVariants() {
        return this.variants;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof WeightedUnbakedModel) {
            WeightedUnbakedModel weightedUnbakedModel = (WeightedUnbakedModel)object;
            return this.variants.equals(weightedUnbakedModel.variants);
        }
        return false;
    }

    public int hashCode() {
        return this.variants.hashCode();
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return this.getVariants().stream().map(ModelVariant::getLocation).collect(Collectors.toSet());
    }

    @Override
    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<String> set) {
        return this.getVariants().stream().map(ModelVariant::getLocation).distinct().flatMap(identifier -> ((UnbakedModel)function.apply((Identifier)identifier)).getTextureDependencies(function, set).stream()).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public BakedModel bake(ModelLoader modelLoader, Function<Identifier, Sprite> function, ModelBakeSettings modelBakeSettings) {
        if (this.getVariants().isEmpty()) {
            return null;
        }
        WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
        for (ModelVariant modelVariant : this.getVariants()) {
            BakedModel bakedModel = modelLoader.bake(modelVariant.getLocation(), modelVariant);
            builder.add(bakedModel, modelVariant.getWeight());
        }
        return builder.getFirst();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<WeightedUnbakedModel> {
        @Override
        public WeightedUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            ArrayList<ModelVariant> list = Lists.newArrayList();
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (JsonElement jsonElement2 : jsonArray) {
                    list.add((ModelVariant)jsonDeserializationContext.deserialize(jsonElement2, (Type)((Object)ModelVariant.class)));
                }
            } else {
                list.add((ModelVariant)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)ModelVariant.class)));
            }
            return new WeightedUnbakedModel(list);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

