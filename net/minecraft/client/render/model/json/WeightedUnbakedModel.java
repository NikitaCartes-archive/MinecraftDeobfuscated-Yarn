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
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WeightedUnbakedModel
implements UnbakedModel {
    private final List<ModelVariant> variants;

    public WeightedUnbakedModel(List<ModelVariant> variants) {
        this.variants = variants;
    }

    public List<ModelVariant> getVariants() {
        return this.variants;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof WeightedUnbakedModel) {
            WeightedUnbakedModel weightedUnbakedModel = (WeightedUnbakedModel)o;
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
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return this.getVariants().stream().map(ModelVariant::getLocation).distinct().flatMap(identifier -> ((UnbakedModel)unbakedModelGetter.apply((Identifier)identifier)).getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences).stream()).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        if (this.getVariants().isEmpty()) {
            return null;
        }
        WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
        for (ModelVariant modelVariant : this.getVariants()) {
            BakedModel bakedModel = loader.bake(modelVariant.getLocation(), modelVariant);
            builder.add(bakedModel, modelVariant.getWeight());
        }
        return builder.getFirst();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<WeightedUnbakedModel> {
        @Override
        public WeightedUnbakedModel deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            ArrayList<ModelVariant> list = Lists.newArrayList();
            if (element.isJsonArray()) {
                JsonArray jsonArray = element.getAsJsonArray();
                if (jsonArray.size() == 0) {
                    throw new JsonParseException("Empty variant array");
                }
                for (JsonElement jsonElement : jsonArray) {
                    list.add((ModelVariant)context.deserialize(jsonElement, (Type)((Object)ModelVariant.class)));
                }
            } else {
                list.add((ModelVariant)context.deserialize(element, (Type)((Object)ModelVariant.class)));
            }
            return new WeightedUnbakedModel(list);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

