/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BakedModel {
    public List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, Random var3);

    public boolean useAmbientOcclusion();

    public boolean hasDepthInGui();

    public boolean isBuiltin();

    public Sprite getSprite();

    public ModelTransformation getTransformation();

    public ModelItemPropertyOverrideList getItemPropertyOverrides();
}

