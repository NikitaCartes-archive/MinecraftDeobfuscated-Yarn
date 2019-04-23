/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import java.util.Collections;
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
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BuiltinBakedModel
implements BakedModel {
    private final ModelTransformation transformation;
    private final ModelItemPropertyOverrideList itemPropertyOverrides;
    private final Sprite sprite;

    public BuiltinBakedModel(ModelTransformation modelTransformation, ModelItemPropertyOverrideList modelItemPropertyOverrideList, Sprite sprite) {
        this.transformation = modelTransformation;
        this.itemPropertyOverrides = modelItemPropertyOverrideList;
        this.sprite = sprite;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepthInGui() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public Sprite getSprite() {
        return this.sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.transformation;
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.itemPropertyOverrides;
    }
}

