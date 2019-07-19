/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public abstract class CoralFeature
extends Feature<DefaultFeatureConfig> {
    public CoralFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig) {
        BlockState blockState = BlockTags.CORAL_BLOCKS.getRandom(random).getDefaultState();
        return this.spawnCoral(iWorld, random, blockPos, blockState);
    }

    protected abstract boolean spawnCoral(IWorld var1, Random var2, BlockPos var3, BlockState var4);

    protected boolean spawnCoralPiece(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
        BlockPos blockPos2 = blockPos.up();
        BlockState blockState2 = iWorld.getBlockState(blockPos);
        if (blockState2.getBlock() != Blocks.WATER && !blockState2.matches(BlockTags.CORALS) || iWorld.getBlockState(blockPos2).getBlock() != Blocks.WATER) {
            return false;
        }
        iWorld.setBlockState(blockPos, blockState, 3);
        if (random.nextFloat() < 0.25f) {
            iWorld.setBlockState(blockPos2, BlockTags.CORALS.getRandom(random).getDefaultState(), 2);
        } else if (random.nextFloat() < 0.05f) {
            iWorld.setBlockState(blockPos2, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos3;
            if (!(random.nextFloat() < 0.2f) || iWorld.getBlockState(blockPos3 = blockPos.offset(direction)).getBlock() != Blocks.WATER) continue;
            BlockState blockState3 = (BlockState)BlockTags.WALL_CORALS.getRandom(random).getDefaultState().with(DeadCoralWallFanBlock.FACING, direction);
            iWorld.setBlockState(blockPos3, blockState3, 2);
        }
        return true;
    }
}

