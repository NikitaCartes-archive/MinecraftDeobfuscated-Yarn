/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractRailBlock
extends Block {
    protected static final VoxelShape STRAIGHT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape ASCENDING_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final boolean allowCurves;

    public static boolean isRail(World world, BlockPos blockPos) {
        return AbstractRailBlock.isRail(world.getBlockState(blockPos));
    }

    public static boolean isRail(BlockState blockState) {
        return blockState.matches(BlockTags.RAILS);
    }

    protected AbstractRailBlock(boolean bl, Block.Settings settings) {
        super(settings);
        this.allowCurves = bl;
    }

    public boolean canMakeCurves() {
        return this.allowCurves;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        RailShape railShape;
        RailShape railShape2 = railShape = blockState.getBlock() == this ? blockState.get(this.getShapeProperty()) : null;
        if (railShape != null && railShape.isAscending()) {
            return ASCENDING_SHAPE;
        }
        return STRAIGHT_SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
        return AbstractRailBlock.topCoversMediumSquare(arg, blockPos.down());
    }

    @Override
    public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.getBlock() == blockState.getBlock()) {
            return;
        }
        if (!world.isClient) {
            blockState = this.updateBlockState(world, blockPos, blockState, true);
            if (this.allowCurves) {
                blockState.neighborUpdate(world, blockPos, this, blockPos, bl);
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (world.isClient) {
            return;
        }
        RailShape railShape = blockState.get(this.getShapeProperty());
        boolean bl2 = false;
        BlockPos blockPos3 = blockPos.down();
        if (!AbstractRailBlock.topCoversMediumSquare(world, blockPos3)) {
            bl2 = true;
        }
        BlockPos blockPos4 = blockPos.east();
        if (railShape == RailShape.ASCENDING_EAST && !AbstractRailBlock.topCoversMediumSquare(world, blockPos4)) {
            bl2 = true;
        } else {
            BlockPos blockPos5 = blockPos.west();
            if (railShape == RailShape.ASCENDING_WEST && !AbstractRailBlock.topCoversMediumSquare(world, blockPos5)) {
                bl2 = true;
            } else {
                BlockPos blockPos6 = blockPos.north();
                if (railShape == RailShape.ASCENDING_NORTH && !AbstractRailBlock.topCoversMediumSquare(world, blockPos6)) {
                    bl2 = true;
                } else {
                    BlockPos blockPos7 = blockPos.south();
                    if (railShape == RailShape.ASCENDING_SOUTH && !AbstractRailBlock.topCoversMediumSquare(world, blockPos7)) {
                        bl2 = true;
                    }
                }
            }
        }
        if (bl2 && !world.isAir(blockPos)) {
            if (!bl) {
                AbstractRailBlock.dropStacks(blockState, world, blockPos);
            }
            world.removeBlock(blockPos, bl);
        } else {
            this.updateBlockState(blockState, world, blockPos, block);
        }
    }

    protected void updateBlockState(BlockState blockState, World world, BlockPos blockPos, Block block) {
    }

    protected BlockState updateBlockState(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
        if (world.isClient) {
            return blockState;
        }
        return new RailPlacementHelper(world, blockPos, blockState).updateBlockState(world.isReceivingRedstonePower(blockPos), bl).getBlockState();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState) {
        return PistonBehavior.NORMAL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl) {
            return;
        }
        super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
        if (blockState.get(this.getShapeProperty()).isAscending()) {
            world.updateNeighborsAlways(blockPos.up(), this);
        }
        if (this.allowCurves) {
            world.updateNeighborsAlways(blockPos, this);
            world.updateNeighborsAlways(blockPos.down(), this);
        }
    }

    public abstract Property<RailShape> getShapeProperty();
}

