/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultipartUnbakedModel
implements UnbakedModel {
    private final StateManager<Block, BlockState> stateFactory;
    private final List<MultipartModelComponent> components;

    public MultipartUnbakedModel(StateManager<Block, BlockState> stateManager, List<MultipartModelComponent> list) {
        this.stateFactory = stateManager;
        this.components = list;
    }

    public List<MultipartModelComponent> getComponents() {
        return this.components;
    }

    public Set<WeightedUnbakedModel> getModels() {
        HashSet<WeightedUnbakedModel> set = Sets.newHashSet();
        for (MultipartModelComponent multipartModelComponent : this.components) {
            set.add(multipartModelComponent.getModel());
        }
        return set;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof MultipartUnbakedModel) {
            MultipartUnbakedModel multipartUnbakedModel = (MultipartUnbakedModel)object;
            return Objects.equals(this.stateFactory, multipartUnbakedModel.stateFactory) && Objects.equals(this.components, multipartUnbakedModel.components);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.stateFactory, this.components);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return this.getComponents().stream().flatMap(multipartModelComponent -> multipartModelComponent.getModel().getModelDependencies().stream()).collect(Collectors.toSet());
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> set) {
        return this.getComponents().stream().flatMap(multipartModelComponent -> multipartModelComponent.getModel().getTextureDependencies(function, set).stream()).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
        MultipartBakedModel.Builder builder = new MultipartBakedModel.Builder();
        for (MultipartModelComponent multipartModelComponent : this.getComponents()) {
            BakedModel bakedModel = multipartModelComponent.getModel().bake(modelLoader, function, modelBakeSettings, identifier);
            if (bakedModel == null) continue;
            builder.addComponent(multipartModelComponent.getPredicate(this.stateFactory), bakedModel);
        }
        return builder.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<MultipartUnbakedModel> {
        private final ModelVariantMap.DeserializationContext context;

        public Deserializer(ModelVariantMap.DeserializationContext deserializationContext) {
            this.context = deserializationContext;
        }

        @Override
        public MultipartUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new MultipartUnbakedModel(this.context.getStateFactory(), this.deserializeComponents(jsonDeserializationContext, jsonElement.getAsJsonArray()));
        }

        private List<MultipartModelComponent> deserializeComponents(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
            ArrayList<MultipartModelComponent> list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                list.add((MultipartModelComponent)jsonDeserializationContext.deserialize(jsonElement, (Type)((Object)MultipartModelComponent.class)));
            }
            return list;
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

