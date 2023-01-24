/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelOverrideList {
    public static final ModelOverrideList EMPTY = new ModelOverrideList();
    public static final float field_42072 = Float.NEGATIVE_INFINITY;
    private final BakedOverride[] overrides;
    private final Identifier[] conditionTypes;

    private ModelOverrideList() {
        this.overrides = new BakedOverride[0];
        this.conditionTypes = new Identifier[0];
    }

    public ModelOverrideList(Baker baker, JsonUnbakedModel parent, List<ModelOverride> overrides) {
        this.conditionTypes = (Identifier[])overrides.stream().flatMap(ModelOverride::streamConditions).map(ModelOverride.Condition::getType).distinct().toArray(Identifier[]::new);
        Object2IntOpenHashMap<Identifier> object2IntMap = new Object2IntOpenHashMap<Identifier>();
        for (int i = 0; i < this.conditionTypes.length; ++i) {
            object2IntMap.put(this.conditionTypes[i], i);
        }
        ArrayList<BakedOverride> list = Lists.newArrayList();
        for (int j = overrides.size() - 1; j >= 0; --j) {
            ModelOverride modelOverride = overrides.get(j);
            BakedModel bakedModel = this.bakeOverridingModel(baker, parent, modelOverride);
            InlinedCondition[] inlinedConditions = (InlinedCondition[])modelOverride.streamConditions().map(condition -> {
                int i = object2IntMap.getInt(condition.getType());
                return new InlinedCondition(i, condition.getThreshold());
            }).toArray(InlinedCondition[]::new);
            list.add(new BakedOverride(inlinedConditions, bakedModel));
        }
        this.overrides = list.toArray(new BakedOverride[0]);
    }

    @Nullable
    private BakedModel bakeOverridingModel(Baker baker, JsonUnbakedModel parent, ModelOverride override) {
        UnbakedModel unbakedModel = baker.getOrLoadModel(override.getModelId());
        if (Objects.equals(unbakedModel, parent)) {
            return null;
        }
        return baker.bake(override.getModelId(), ModelRotation.X0_Y0);
    }

    @Nullable
    public BakedModel apply(BakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (this.overrides.length != 0) {
            Item item = stack.getItem();
            int i = this.conditionTypes.length;
            float[] fs = new float[i];
            for (int j = 0; j < i; ++j) {
                Identifier identifier = this.conditionTypes[j];
                ModelPredicateProvider modelPredicateProvider = ModelPredicateProviderRegistry.get(item, identifier);
                fs[j] = modelPredicateProvider != null ? modelPredicateProvider.call(stack, world, entity, seed) : Float.NEGATIVE_INFINITY;
            }
            for (BakedOverride bakedOverride : this.overrides) {
                if (!bakedOverride.test(fs)) continue;
                BakedModel bakedModel = bakedOverride.model;
                if (bakedModel == null) {
                    return model;
                }
                return bakedModel;
            }
        }
        return model;
    }

    @Environment(value=EnvType.CLIENT)
    static class BakedOverride {
        private final InlinedCondition[] conditions;
        @Nullable
        final BakedModel model;

        BakedOverride(InlinedCondition[] conditions, @Nullable BakedModel model) {
            this.conditions = conditions;
            this.model = model;
        }

        boolean test(float[] values) {
            for (InlinedCondition inlinedCondition : this.conditions) {
                float f = values[inlinedCondition.index];
                if (!(f < inlinedCondition.threshold)) continue;
                return false;
            }
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class InlinedCondition {
        public final int index;
        public final float threshold;

        InlinedCondition(int index, float threshold) {
            this.index = index;
            this.threshold = threshold;
        }
    }
}

