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
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelItemPropertyOverrideList {
    public static final ModelItemPropertyOverrideList EMPTY = new ModelItemPropertyOverrideList();
    private final List<ModelItemOverride> overrides = Lists.newArrayList();
    private final List<BakedModel> models;

    private ModelItemPropertyOverrideList() {
        this.models = Collections.emptyList();
    }

    public ModelItemPropertyOverrideList(ModelLoader modelLoader, JsonUnbakedModel jsonUnbakedModel, Function<Identifier, UnbakedModel> function, List<ModelItemOverride> list) {
        this.models = list.stream().map(modelItemOverride -> {
            UnbakedModel unbakedModel = (UnbakedModel)function.apply(modelItemOverride.getModelId());
            if (Objects.equals(unbakedModel, jsonUnbakedModel)) {
                return null;
            }
            return modelLoader.bake(modelItemOverride.getModelId(), ModelRotation.X0_Y0);
        }).collect(Collectors.toList());
        Collections.reverse(this.models);
        for (int i = list.size() - 1; i >= 0; --i) {
            this.overrides.add(list.get(i));
        }
    }

    @Nullable
    public BakedModel apply(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        if (!this.overrides.isEmpty()) {
            for (int i = 0; i < this.overrides.size(); ++i) {
                ModelItemOverride modelItemOverride = this.overrides.get(i);
                if (!modelItemOverride.matches(itemStack, world, livingEntity)) continue;
                BakedModel bakedModel2 = this.models.get(i);
                if (bakedModel2 == null) {
                    return bakedModel;
                }
                return bakedModel2;
            }
        }
        return bakedModel;
    }
}

