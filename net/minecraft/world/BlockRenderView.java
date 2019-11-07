/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.level.ColorResolver;

public interface BlockRenderView
extends BlockView {
    public LightingProvider getLightingProvider();

    @Environment(value=EnvType.CLIENT)
    public int method_23752(BlockPos var1, ColorResolver var2);

    default public int getLightLevel(LightType lightType, BlockPos blockPos) {
        return this.getLightingProvider().get(lightType).getLightLevel(blockPos);
    }

    default public int getBaseLightLevel(BlockPos blockPos, int i) {
        return this.getLightingProvider().getLight(blockPos, i);
    }

    default public boolean isSkyVisible(BlockPos blockPos) {
        return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
    }
}

