/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ForestRockFeature
extends Feature<BoulderFeatureConfig> {
    public ForestRockFeature(Function<Dynamic<?>, ? extends BoulderFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, BoulderFeatureConfig boulderFeatureConfig) {
        Block block;
        while (blockPos.getY() > 3 && (iWorld.isAir(blockPos.down()) || !ForestRockFeature.isDirt(block = iWorld.getBlockState(blockPos.down()).getBlock()) && !ForestRockFeature.isStone(block))) {
            blockPos = blockPos.down();
        }
        if (blockPos.getY() <= 3) {
            return false;
        }
        int i = boulderFeatureConfig.startRadius;
        for (int j = 0; i >= 0 && j < 3; ++j) {
            int k = i + random.nextInt(2);
            int l = i + random.nextInt(2);
            int m = i + random.nextInt(2);
            float f = (float)(k + l + m) * 0.333f + 0.5f;
            for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-k, -l, -m), blockPos.add(k, l, m))) {
                if (!(blockPos2.getSquaredDistance(blockPos) <= (double)(f * f))) continue;
                iWorld.setBlockState(blockPos2, boulderFeatureConfig.state, 4);
            }
            blockPos = blockPos.add(-(i + 1) + random.nextInt(2 + i * 2), 0 - random.nextInt(2), -(i + 1) + random.nextInt(2 + i * 2));
        }
        return true;
    }
}

