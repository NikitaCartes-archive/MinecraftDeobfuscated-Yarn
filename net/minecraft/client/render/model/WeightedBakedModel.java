/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WeightedBakedModel
implements BakedModel {
    private final int totalWeight;
    private final List<Entry> models;
    private final BakedModel defaultModel;

    public WeightedBakedModel(List<Entry> list) {
        this.models = list;
        this.totalWeight = WeightedPicker.getWeightSum(list);
        this.defaultModel = list.get((int)0).model;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
        return WeightedPicker.getAt(this.models, (int)(Math.abs((int)((int)random.nextLong())) % this.totalWeight)).model.getQuads(blockState, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean hasDepthInGui() {
        return this.defaultModel.hasDepthInGui();
    }

    @Override
    public boolean isBuiltin() {
        return this.defaultModel.isBuiltin();
    }

    @Override
    public Sprite getSprite() {
        return this.defaultModel.getSprite();
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.defaultModel.getTransformation();
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.defaultModel.getItemPropertyOverrides();
    }

    @Environment(value=EnvType.CLIENT)
    static class Entry
    extends WeightedPicker.Entry {
        protected final BakedModel model;

        public Entry(BakedModel bakedModel, int i) {
            super(i);
            this.model = bakedModel;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final List<Entry> models = Lists.newArrayList();

        public Builder add(@Nullable BakedModel bakedModel, int i) {
            if (bakedModel != null) {
                this.models.add(new Entry(bakedModel, i));
            }
            return this;
        }

        @Nullable
        public BakedModel getFirst() {
            if (this.models.isEmpty()) {
                return null;
            }
            if (this.models.size() == 1) {
                return this.models.get((int)0).model;
            }
            return new WeightedBakedModel(this.models);
        }
    }
}

