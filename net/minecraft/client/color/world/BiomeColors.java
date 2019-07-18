/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.color.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.biome.Biome;

@Environment(value=EnvType.CLIENT)
public class BiomeColors {
    private static final Provider GRASS_COLOR = Biome::getGrassColorAt;
    private static final Provider FOLIAGE_COLOR = Biome::getFoliageColorAt;
    private static final Provider WATER_COLOR = (biome, blockPos) -> biome.getWaterColor();
    private static final Provider WATER_FOG_COLOR = (biome, blockPos) -> biome.getWaterFogColor();

    private static int getColor(ExtendedBlockView extendedBlockView, BlockPos blockPos, Provider provider) {
        int i = 0;
        int j = 0;
        int k = 0;
        int l = MinecraftClient.getInstance().options.biomeBlendRadius;
        if (l == 0) {
            return provider.getColor(extendedBlockView.getBiome(blockPos), blockPos);
        }
        int m = (l * 2 + 1) * (l * 2 + 1);
        CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(blockPos.getX() - l, blockPos.getY(), blockPos.getZ() - l, blockPos.getX() + l, blockPos.getY(), blockPos.getZ() + l);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        while (cuboidBlockIterator.step()) {
            mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
            int n = provider.getColor(extendedBlockView.getBiome(mutable), mutable);
            i += (n & 0xFF0000) >> 16;
            j += (n & 0xFF00) >> 8;
            k += n & 0xFF;
        }
        return (i / m & 0xFF) << 16 | (j / m & 0xFF) << 8 | k / m & 0xFF;
    }

    public static int getGrassColor(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
        return BiomeColors.getColor(extendedBlockView, blockPos, GRASS_COLOR);
    }

    public static int getFoliageColor(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
        return BiomeColors.getColor(extendedBlockView, blockPos, FOLIAGE_COLOR);
    }

    public static int getWaterColor(ExtendedBlockView extendedBlockView, BlockPos blockPos) {
        return BiomeColors.getColor(extendedBlockView, blockPos, WATER_COLOR);
    }

    @Environment(value=EnvType.CLIENT)
    static interface Provider {
        public int getColor(Biome var1, BlockPos var2);
    }
}

