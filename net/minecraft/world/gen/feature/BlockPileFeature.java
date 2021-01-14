/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class BlockPileFeature
extends Feature<BlockPileFeatureConfig> {
    public BlockPileFeature(Codec<BlockPileFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockPileFeatureConfig blockPileFeatureConfig) {
        if (blockPos.getY() < 5) {
            return false;
        }
        int i = 2 + random.nextInt(2);
        int j = 2 + random.nextInt(2);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, 0, -j), blockPos.add(i, 1, j))) {
            int l;
            int k = blockPos.getX() - blockPos2.getX();
            if ((float)(k * k + (l = blockPos.getZ() - blockPos2.getZ()) * l) <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.addPileBlock(structureWorldAccess, blockPos2, random, blockPileFeatureConfig);
                continue;
            }
            if (!((double)random.nextFloat() < 0.031)) continue;
            this.addPileBlock(structureWorldAccess, blockPos2, random, blockPileFeatureConfig);
        }
        return true;
    }

    private boolean canPlace(WorldAccess world, BlockPos pos, Random random) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.GRASS_PATH)) {
            return random.nextBoolean();
        }
        return blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
    }

    private void addPileBlock(WorldAccess world, BlockPos pos, Random random, BlockPileFeatureConfig config) {
        if (world.isAir(pos) && this.canPlace(world, pos, random)) {
            world.setBlockState(pos, config.stateProvider.getBlockState(random, pos), 4);
        }
    }
}

