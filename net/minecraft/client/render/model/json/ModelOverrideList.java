/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelOverrideList {
    public static final ModelOverrideList EMPTY = new ModelOverrideList();
    private final List<ModelOverride> overrides = Lists.newArrayList();
    private final List<BakedModel> models;

    private ModelOverrideList() {
        this.models = Collections.emptyList();
    }

    public ModelOverrideList(ModelLoader modelLoader, JsonUnbakedModel parent, Function<Identifier, UnbakedModel> unbakedModelGetter, List<ModelOverride> overrides) {
        this.models = overrides.stream().map(modelOverride -> {
            UnbakedModel unbakedModel = (UnbakedModel)unbakedModelGetter.apply(modelOverride.getModelId());
            if (Objects.equals(unbakedModel, parent)) {
                return null;
            }
            return modelLoader.bake(modelOverride.getModelId(), ModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.models);
        for (int i = overrides.size() - 1; i >= 0; --i) {
            this.overrides.add(overrides.get(i));
        }
    }

    @Nullable
    public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        if (!this.overrides.isEmpty()) {
            for (int i = 0; i < this.overrides.size(); ++i) {
                ModelOverride modelOverride = this.overrides.get(i);
                if (!modelOverride.matches(stack, world, entity)) continue;
                BakedModel bakedModel = this.models.get(i);
                if (bakedModel == null) {
                    return model;
                }
                return bakedModel;
            }
        }
        return model;
    }
}

