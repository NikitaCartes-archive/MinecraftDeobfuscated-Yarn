/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeagrassBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;

public class SeagrassFeature
extends Feature<SeagrassFeatureConfig> {
    public SeagrassFeature(Function<Dynamic<?>, ? extends SeagrassFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, StructureAccessor structureAccessor, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, SeagrassFeatureConfig seagrassFeatureConfig) {
        int i = 0;
        for (int j = 0; j < seagrassFeatureConfig.count; ++j) {
            BlockState blockState;
            int k = random.nextInt(8) - random.nextInt(8);
            int l = random.nextInt(8) - random.nextInt(8);
            int m = iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR, blockPos.getX() + k, blockPos.getZ() + l);
            BlockPos blockPos2 = new BlockPos(blockPos.getX() + k, m, blockPos.getZ() + l);
            if (!iWorld.getBlockState(blockPos2).isOf(Blocks.WATER)) continue;
            boolean bl = random.nextDouble() < seagrassFeatureConfig.tallSeagrassProbability;
            BlockState blockState2 = blockState = bl ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
            if (!blockState.canPlaceAt(iWorld, blockPos2)) continue;
            if (bl) {
                BlockState blockState22 = (BlockState)blockState.with(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
                BlockPos blockPos3 = blockPos2.up();
                if (iWorld.getBlockState(blockPos3).isOf(Blocks.WATER)) {
                    iWorld.setBlockState(blockPos2, blockState, 2);
                    iWorld.setBlockState(blockPos3, blockState22, 2);
                }
            } else {
                iWorld.setBlockState(blockPos2, blockState, 2);
            }
            ++i;
        }
        return i > 0;
    }
}

