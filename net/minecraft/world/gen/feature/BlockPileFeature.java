/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BlockPileFeature
extends Feature<BlockPileFeatureConfig> {
    public BlockPileFeature(Codec<BlockPileFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<BlockPileFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        AbstractRandom abstractRandom = context.getRandom();
        BlockPileFeatureConfig blockPileFeatureConfig = context.getConfig();
        if (blockPos.getY() < structureWorldAccess.getBottomY() + 5) {
            return false;
        }
        int i = 2 + abstractRandom.nextInt(2);
        int j = 2 + abstractRandom.nextInt(2);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-i, 0, -j), blockPos.add(i, 1, j))) {
            int l;
            int k = blockPos.getX() - blockPos2.getX();
            if ((float)(k * k + (l = blockPos.getZ() - blockPos2.getZ()) * l) <= abstractRandom.nextFloat() * 10.0f - abstractRandom.nextFloat() * 6.0f) {
                this.addPileBlock(structureWorldAccess, blockPos2, abstractRandom, blockPileFeatureConfig);
                continue;
            }
            if (!((double)abstractRandom.nextFloat() < 0.031)) continue;
            this.addPileBlock(structureWorldAccess, blockPos2, abstractRandom, blockPileFeatureConfig);
        }
        return true;
    }

    private boolean canPlace(WorldAccess world, BlockPos pos, AbstractRandom random) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.DIRT_PATH)) {
            return random.nextBoolean();
        }
        return blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
    }

    private void addPileBlock(WorldAccess world, BlockPos pos, AbstractRandom random, BlockPileFeatureConfig config) {
        if (world.isAir(pos) && this.canPlace(world, pos, random)) {
            world.setBlockState(pos, config.stateProvider.getBlockState(random, pos), Block.NO_REDRAW);
        }
    }
}

