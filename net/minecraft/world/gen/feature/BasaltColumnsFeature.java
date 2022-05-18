/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.jetbrains.annotations.Nullable;

public class BasaltColumnsFeature
extends Feature<BasaltColumnsFeatureConfig> {
    private static final ImmutableList<Block> CANNOT_REPLACE_BLOCKS = ImmutableList.of(Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
    private static final int field_31495 = 5;
    private static final int field_31496 = 50;
    private static final int field_31497 = 8;
    private static final int field_31498 = 15;

    public BasaltColumnsFeature(Codec<BasaltColumnsFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<BasaltColumnsFeatureConfig> context) {
        int i = context.getGenerator().getSeaLevel();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        BasaltColumnsFeatureConfig basaltColumnsFeatureConfig = context.getConfig();
        if (!BasaltColumnsFeature.canPlaceAt(structureWorldAccess, i, blockPos.mutableCopy())) {
            return false;
        }
        int j = basaltColumnsFeatureConfig.getHeight().get(random);
        boolean bl = random.nextFloat() < 0.9f;
        int k = Math.min(j, bl ? 5 : 8);
        int l = bl ? 50 : 15;
        boolean bl2 = false;
        for (BlockPos blockPos2 : BlockPos.iterateRandomly(random, l, blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k)) {
            int m = j - blockPos2.getManhattanDistance(blockPos);
            if (m < 0) continue;
            bl2 |= this.placeBasaltColumn(structureWorldAccess, i, blockPos2, m, basaltColumnsFeatureConfig.getReach().get(random));
        }
        return bl2;
    }

    private boolean placeBasaltColumn(WorldAccess world, int seaLevel, BlockPos pos, int height, int reach) {
        boolean bl = false;
        block0: for (BlockPos blockPos : BlockPos.iterate(pos.getX() - reach, pos.getY(), pos.getZ() - reach, pos.getX() + reach, pos.getY(), pos.getZ() + reach)) {
            BlockPos blockPos2;
            int i = blockPos.getManhattanDistance(pos);
            BlockPos blockPos3 = blockPos2 = BasaltColumnsFeature.isAirOrLavaOcean(world, seaLevel, blockPos) ? BasaltColumnsFeature.moveDownToGround(world, seaLevel, blockPos.mutableCopy(), i) : BasaltColumnsFeature.moveUpToAir(world, blockPos.mutableCopy(), i);
            if (blockPos2 == null) continue;
            BlockPos.Mutable mutable = blockPos2.mutableCopy();
            for (int j = height - i / 2; j >= 0; --j) {
                if (BasaltColumnsFeature.isAirOrLavaOcean(world, seaLevel, mutable)) {
                    this.setBlockState(world, mutable, Blocks.BASALT.getDefaultState());
                    mutable.move(Direction.UP);
                    bl = true;
                    continue;
                }
                if (!world.getBlockState(mutable).isOf(Blocks.BASALT)) continue block0;
                mutable.move(Direction.UP);
            }
        }
        return bl;
    }

    @Nullable
    private static BlockPos moveDownToGround(WorldAccess world, int seaLevel, BlockPos.Mutable mutablePos, int distance) {
        while (mutablePos.getY() > world.getBottomY() + 1 && distance > 0) {
            --distance;
            if (BasaltColumnsFeature.canPlaceAt(world, seaLevel, mutablePos)) {
                return mutablePos;
            }
            mutablePos.move(Direction.DOWN);
        }
        return null;
    }

    private static boolean canPlaceAt(WorldAccess world, int seaLevel, BlockPos.Mutable mutablePos) {
        if (BasaltColumnsFeature.isAirOrLavaOcean(world, seaLevel, mutablePos)) {
            BlockState blockState = world.getBlockState(mutablePos.move(Direction.DOWN));
            mutablePos.move(Direction.UP);
            return !blockState.isAir() && !CANNOT_REPLACE_BLOCKS.contains(blockState.getBlock());
        }
        return false;
    }

    @Nullable
    private static BlockPos moveUpToAir(WorldAccess world, BlockPos.Mutable mutablePos, int distance) {
        while (mutablePos.getY() < world.getTopY() && distance > 0) {
            --distance;
            BlockState blockState = world.getBlockState(mutablePos);
            if (CANNOT_REPLACE_BLOCKS.contains(blockState.getBlock())) {
                return null;
            }
            if (blockState.isAir()) {
                return mutablePos;
            }
            mutablePos.move(Direction.UP);
        }
        return null;
    }

    private static boolean isAirOrLavaOcean(WorldAccess world, int seaLevel, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isAir() || blockState.isOf(Blocks.LAVA) && pos.getY() <= seaLevel;
    }
}

