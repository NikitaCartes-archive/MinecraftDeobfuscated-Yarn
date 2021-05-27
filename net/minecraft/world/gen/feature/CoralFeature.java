/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadCoralWallFanBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class CoralFeature
extends Feature<DefaultFeatureConfig> {
    public CoralFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        Random random = context.getRandom();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        BlockState blockState = ((Block)BlockTags.CORAL_BLOCKS.getRandom(random)).getDefaultState();
        return this.generateCoral(structureWorldAccess, random, blockPos, blockState);
    }

    protected abstract boolean generateCoral(WorldAccess var1, Random var2, BlockPos var3, BlockState var4);

    protected boolean generateCoralPiece(WorldAccess world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isOf(Blocks.WATER) && !blockState.isIn(BlockTags.CORALS) || !world.getBlockState(blockPos).isOf(Blocks.WATER)) {
            return false;
        }
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        if (random.nextFloat() < 0.25f) {
            world.setBlockState(blockPos, ((Block)BlockTags.CORALS.getRandom(random)).getDefaultState(), Block.NOTIFY_LISTENERS);
        } else if (random.nextFloat() < 0.05f) {
            world.setBlockState(blockPos, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), Block.NOTIFY_LISTENERS);
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos2;
            if (!(random.nextFloat() < 0.2f) || !world.getBlockState(blockPos2 = pos.offset(direction)).isOf(Blocks.WATER)) continue;
            BlockState blockState2 = ((Block)BlockTags.WALL_CORALS.getRandom(random)).getDefaultState();
            if (blockState2.contains(DeadCoralWallFanBlock.FACING)) {
                blockState2 = (BlockState)blockState2.with(DeadCoralWallFanBlock.FACING, direction);
            }
            world.setBlockState(blockPos2, blockState2, Block.NOTIFY_LISTENERS);
        }
        return true;
    }
}

