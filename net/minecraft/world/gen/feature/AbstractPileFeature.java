/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class AbstractPileFeature
extends Feature<BlockPileFeatureConfig> {
    public AbstractPileFeature(Function<Dynamic<?>, ? extends BlockPileFeatureConfig> function) {
        super(function);
    }

    public boolean method_16709(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig) {
        if (blockPos.getY() < 5) {
            return false;
        }
        int i = 2 + random.nextInt(2);
        int j = 2 + random.nextInt(2);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, 0, -j), blockPos.add(i, 1, j))) {
            int l;
            int k = blockPos.getX() - blockPos2.getX();
            if ((float)(k * k + (l = blockPos.getZ() - blockPos2.getZ()) * l) <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.addPileBlock(iWorld, blockPos2, random, blockPileFeatureConfig);
                continue;
            }
            if (!((double)random.nextFloat() < 0.031)) continue;
            this.addPileBlock(iWorld, blockPos2, random, blockPileFeatureConfig);
        }
        return true;
    }

    private boolean canPlacePileBlock(IWorld iWorld, BlockPos blockPos, Random random) {
        BlockPos blockPos2 = blockPos.method_10074();
        BlockState blockState = iWorld.getBlockState(blockPos2);
        if (blockState.getBlock() == Blocks.GRASS_PATH) {
            return random.nextBoolean();
        }
        return blockState.isSideSolidFullSquare(iWorld, blockPos2, Direction.UP);
    }

    private void addPileBlock(IWorld iWorld, BlockPos blockPos, Random random, BlockPileFeatureConfig blockPileFeatureConfig) {
        if (iWorld.isAir(blockPos) && this.canPlacePileBlock(iWorld, blockPos, random)) {
            iWorld.setBlockState(blockPos, blockPileFeatureConfig.field_21229.getBlockState(random, blockPos), 4);
        }
    }
}

