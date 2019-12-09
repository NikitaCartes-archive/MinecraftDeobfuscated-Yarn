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
    public int getColor(BlockPos var1, ColorResolver var2);

    default public int getLightLevel(LightType type, BlockPos pos) {
        return this.getLightingProvider().get(type).getLightLevel(pos);
    }

    default public int getBaseLightLevel(BlockPos pos, int ambientDarkness) {
        return this.getLightingProvider().getLight(pos, ambientDarkness);
    }

    default public boolean isSkyVisible(BlockPos pos) {
        return this.getLightLevel(LightType.SKY, pos) >= this.getMaxLightLevel();
    }
}

