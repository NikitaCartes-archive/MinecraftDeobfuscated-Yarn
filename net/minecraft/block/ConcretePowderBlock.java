/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock
extends FallingBlock {
    private final BlockState hardenedState;

    public ConcretePowderBlock(Block hardened, Block.Settings settings) {
        super(settings);
        this.hardenedState = hardened.getDefaultState();
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
        if (ConcretePowderBlock.method_24279(world, pos, currentStateInPos)) {
            world.setBlockState(pos, this.hardenedState, 3);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState;
        BlockPos blockPos;
        World blockView = ctx.getWorld();
        if (ConcretePowderBlock.method_24279(blockView, blockPos = ctx.getBlockPos(), blockState = blockView.getBlockState(blockPos))) {
            return this.hardenedState;
        }
        return super.getPlacementState(ctx);
    }

    private static boolean method_24279(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        return ConcretePowderBlock.hardensIn(blockState) || ConcretePowderBlock.hardensOnAnySide(blockView, blockPos);
    }

    private static boolean hardensOnAnySide(BlockView world, BlockPos pos) {
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        for (Direction direction : Direction.values()) {
            BlockState blockState = world.getBlockState(mutable);
            if (direction == Direction.DOWN && !ConcretePowderBlock.hardensIn(blockState)) continue;
            mutable.set(pos).setOffset(direction);
            blockState = world.getBlockState(mutable);
            if (!ConcretePowderBlock.hardensIn(blockState) || blockState.isSideSolidFullSquare(world, pos, direction.getOpposite())) continue;
            bl = true;
            break;
        }
        return bl;
    }

    private static boolean hardensIn(BlockState state) {
        return state.getFluidState().matches(FluidTags.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (ConcretePowderBlock.hardensOnAnySide(world, pos)) {
            return this.hardenedState;
        }
        return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getColor(BlockState state) {
        return this.materialColor.color;
    }
}

