/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrostedIceBlock
extends IceBlock {
    public static final IntProperty AGE = Properties.AGE_3;

    public FrostedIceBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(AGE, 0));
    }

    @Override
    public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if ((random.nextInt(3) == 0 || this.canMelt(serverWorld, blockPos, 4)) && serverWorld.method_22339(blockPos) > 11 - blockState.get(AGE) - blockState.getLightSubtracted(serverWorld, blockPos) && this.increaseAge(blockState, serverWorld, blockPos)) {
            try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
                for (Direction direction : Direction.values()) {
                    pooledMutable.method_10114(blockPos).method_10118(direction);
                    BlockState blockState2 = serverWorld.getBlockState(pooledMutable);
                    if (blockState2.getBlock() != this || this.increaseAge(blockState2, serverWorld, pooledMutable)) continue;
                    serverWorld.method_14196().schedule(pooledMutable, this, MathHelper.nextInt(random, 20, 40));
                }
            }
            return;
        }
        serverWorld.method_14196().schedule(blockPos, this, MathHelper.nextInt(random, 20, 40));
    }

    private boolean increaseAge(BlockState blockState, World world, BlockPos blockPos) {
        int i = blockState.get(AGE);
        if (i < 3) {
            world.setBlockState(blockPos, (BlockState)blockState.with(AGE, i + 1), 2);
            return false;
        }
        this.melt(blockState, world, blockPos);
        return true;
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (block == this && this.canMelt(world, blockPos, 2)) {
            this.melt(blockState, world, blockPos);
        }
        super.neighborUpdate(blockState, world, blockPos, block, blockPos2, bl);
    }

    private boolean canMelt(BlockView blockView, BlockPos blockPos, int i) {
        int j = 0;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (Direction direction : Direction.values()) {
                pooledMutable.method_10114(blockPos).method_10118(direction);
                if (blockView.getBlockState(pooledMutable).getBlock() != this || ++j < i) continue;
                boolean bl = false;
                return bl;
            }
        }
        return true;
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }
}

