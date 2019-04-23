/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SpongeBlock
extends Block {
    protected SpongeBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock()) {
            return;
        }
        this.update(world, blockPos);
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        this.update(world, blockPos);
        super.neighborUpdate(blockState, world, blockPos, block, blockPos2, bl);
    }

    protected void update(World world, BlockPos blockPos) {
        if (this.absorbWater(world, blockPos)) {
            world.setBlockState(blockPos, Blocks.WET_SPONGE.getDefaultState(), 2);
            world.playLevelEvent(2001, blockPos, Block.getRawIdFromState(Blocks.WATER.getDefaultState()));
        }
    }

    private boolean absorbWater(World world, BlockPos blockPos) {
        LinkedList<Pair<BlockPos, Integer>> queue = Lists.newLinkedList();
        queue.add(new Pair<BlockPos, Integer>(blockPos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Pair pair = (Pair)queue.poll();
            BlockPos blockPos2 = (BlockPos)pair.getLeft();
            int j = (Integer)pair.getRight();
            for (Direction direction : Direction.values()) {
                BlockPos blockPos3 = blockPos2.offset(direction);
                BlockState blockState = world.getBlockState(blockPos3);
                FluidState fluidState = world.getFluidState(blockPos3);
                Material material = blockState.getMaterial();
                if (!fluidState.matches(FluidTags.WATER)) continue;
                if (blockState.getBlock() instanceof FluidDrainable && ((FluidDrainable)((Object)blockState.getBlock())).tryDrainFluid(world, blockPos3, blockState) != Fluids.EMPTY) {
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(blockPos3, j + 1));
                    continue;
                }
                if (blockState.getBlock() instanceof FluidBlock) {
                    world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 3);
                    ++i;
                    if (j >= 6) continue;
                    queue.add(new Pair<BlockPos, Integer>(blockPos3, j + 1));
                    continue;
                }
                if (material != Material.UNDERWATER_PLANT && material != Material.SEAGRASS) continue;
                BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
                SpongeBlock.dropStacks(blockState, world, blockPos3, blockEntity);
                world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 3);
                ++i;
                if (j >= 6) continue;
                queue.add(new Pair<BlockPos, Integer>(blockPos3, j + 1));
            }
            if (i <= 64) continue;
            break;
        }
        return i > 0;
    }
}

