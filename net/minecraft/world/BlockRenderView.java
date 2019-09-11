/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.class_4543;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;

public interface BlockRenderView
extends BlockView {
    public class_4543 method_22385();

    public LightingProvider getLightingProvider();

    default public Biome getBiome(BlockPos blockPos) {
        return this.method_22385().getBiome(blockPos);
    }

    default public int getLightLevel(LightType lightType, BlockPos blockPos) {
        return this.getLightingProvider().get(lightType).getLightLevel(blockPos);
    }

    default public int getBaseLightLevel(BlockPos blockPos, int i) {
        return this.getLightingProvider().getLight(blockPos, i);
    }

    default public boolean isSkyVisible(BlockPos blockPos) {
        return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
    }

    @Environment(value=EnvType.CLIENT)
    default public int getLightmapIndex(BlockPos blockPos) {
        return this.getLightmapIndex(this.getBlockState(blockPos), blockPos);
    }

    @Environment(value=EnvType.CLIENT)
    default public int getLightmapIndex(BlockState blockState, BlockPos blockPos) {
        int k;
        if (blockState.method_22361()) {
            return 0xF000F0;
        }
        int i = this.getLightLevel(LightType.SKY, blockPos);
        int j = this.getLightLevel(LightType.BLOCK, blockPos);
        if (j < (k = blockState.getLuminance())) {
            j = k;
        }
        return i << 20 | j << 4;
    }
}

