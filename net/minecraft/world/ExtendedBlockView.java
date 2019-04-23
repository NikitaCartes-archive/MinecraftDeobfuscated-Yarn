/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

public interface ExtendedBlockView
extends BlockView {
    public Biome getBiome(BlockPos var1);

    public int getLightLevel(LightType var1, BlockPos var2);

    default public boolean isSkyVisible(BlockPos blockPos) {
        return this.getLightLevel(LightType.SKY, blockPos) >= this.getMaxLightLevel();
    }

    @Environment(value=EnvType.CLIENT)
    default public int getLightmapIndex(BlockPos blockPos, int i) {
        int j = this.getLightLevel(LightType.SKY, blockPos);
        int k = this.getLightLevel(LightType.BLOCK, blockPos);
        if (k < i) {
            k = i;
        }
        return j << 20 | k << 4;
    }
}

