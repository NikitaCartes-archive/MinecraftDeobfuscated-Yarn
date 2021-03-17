/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.SetBlockStateFlags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ConcretePowderBlock
extends FallingBlock {
    private final BlockState hardenedState;

    public ConcretePowderBlock(Block hardened, AbstractBlock.Settings settings) {
        super(settings);
        this.hardenedState = hardened.getDefaultState();
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
        if (ConcretePowderBlock.shouldHarden(world, pos, currentStateInPos)) {
            world.setBlockState(pos, this.hardenedState, SetBlockStateFlags.DEFAULT);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState;
        BlockPos blockPos;
        World blockView = ctx.getWorld();
        if (ConcretePowderBlock.shouldHarden(blockView, blockPos = ctx.getBlockPos(), blockState = blockView.getBlockState(blockPos))) {
            return this.hardenedState;
        }
        return super.getPlacementState(ctx);
    }

    private static boolean shouldHarden(BlockView world, BlockPos pos, BlockState state) {
        return ConcretePowderBlock.hardensIn(state) || ConcretePowderBlock.hardensOnAnySide(world, pos);
    }

    private static boolean hardensOnAnySide(BlockView world, BlockPos pos) {
        boolean bl = false;
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : Direction.values()) {
            BlockState blockState = world.getBlockState(mutable);
            if (direction == Direction.DOWN && !ConcretePowderBlock.hardensIn(blockState)) continue;
            mutable.set(pos, direction);
            blockState = world.getBlockState(mutable);
            if (!ConcretePowderBlock.hardensIn(blockState) || blockState.isSideSolidFullSquare(world, pos, direction.getOpposite())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    private static boolean hardensIn(BlockState state) {
        return state.getFluidState().isIn(FluidTags.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (ConcretePowderBlock.hardensOnAnySide(world, pos)) {
            return this.hardenedState;
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return state.getMapColor((BlockView)world, (BlockPos)pos).color;
    }
}

