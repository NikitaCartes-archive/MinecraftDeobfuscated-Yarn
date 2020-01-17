/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WallSkullBlock
extends AbstractSkullBlock {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(4.0, 4.0, 8.0, 12.0, 12.0, 16.0), Direction.SOUTH, Block.createCuboidShape(4.0, 4.0, 0.0, 12.0, 12.0, 8.0), Direction.EAST, Block.createCuboidShape(0.0, 4.0, 4.0, 8.0, 12.0, 12.0), Direction.WEST, Block.createCuboidShape(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)));

    protected WallSkullBlock(SkullBlock.SkullType skullType, Block.Settings settings) {
        super(skullType, settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return FACING_TO_SHAPE.get(state.get(FACING));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] directions;
        BlockState blockState = this.getDefaultState();
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : directions = ctx.getPlacementDirections()) {
            if (!direction.getAxis().isHorizontal()) continue;
            Direction direction2 = direction.getOpposite();
            blockState = (BlockState)blockState.with(FACING, direction2);
            if (blockView.getBlockState(blockPos.offset(direction)).canReplace(ctx)) continue;
            return blockState;
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

