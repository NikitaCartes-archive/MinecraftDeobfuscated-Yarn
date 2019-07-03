/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BambooBlock
extends Block
implements Fertilizable {
    protected static final VoxelShape SMALL_LEAVES_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape LARGE_LEAVES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape NO_LEAVES_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    public static final IntProperty AGE = Properties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = Properties.BAMBOO_LEAVES;
    public static final IntProperty STAGE = Properties.STAGE;

    public BambooBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateFactory.getDefaultState()).with(AGE, 0)).with(LEAVES, BambooLeaves.NONE)).with(STAGE, 0));
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(AGE, LEAVES, STAGE);
    }

    @Override
    public Block.OffsetType getOffsetType() {
        return Block.OffsetType.XZ;
    }

    @Override
    public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return true;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        VoxelShape voxelShape = blockState.get(LEAVES) == BambooLeaves.LARGE ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
        Vec3d vec3d = blockState.getOffsetPos(blockView, blockPos);
        return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
        return false;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
        Vec3d vec3d = blockState.getOffsetPos(blockView, blockPos);
        return NO_LEAVES_SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
        if (!fluidState.isEmpty()) {
            return null;
        }
        BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos().down());
        if (blockState.matches(BlockTags.BAMBOO_PLANTABLE_ON)) {
            Block block = blockState.getBlock();
            if (block == Blocks.BAMBOO_SAPLING) {
                return (BlockState)this.getDefaultState().with(AGE, 0);
            }
            if (block == Blocks.BAMBOO) {
                int i = blockState.get(AGE) > 0 ? 1 : 0;
                return (BlockState)this.getDefaultState().with(AGE, i);
            }
            return Blocks.BAMBOO_SAPLING.getDefaultState();
        }
        return null;
    }

    @Override
    public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        int i;
        if (!blockState.canPlaceAt(world, blockPos)) {
            world.breakBlock(blockPos, true);
            return;
        }
        if (blockState.get(STAGE) != 0) {
            return;
        }
        if (random.nextInt(3) == 0 && world.isAir(blockPos.up()) && world.getLightLevel(blockPos.up(), 0) >= 9 && (i = this.countBambooBelow(world, blockPos) + 1) < 16) {
            this.updateLeaves(blockState, world, blockPos, random, i);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
        return viewableWorld.getBlockState(blockPos.down()).matches(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canPlaceAt(iWorld, blockPos)) {
            iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
        }
        if (direction == Direction.UP && blockState2.getBlock() == Blocks.BAMBOO && blockState2.get(AGE) > blockState.get(AGE)) {
            iWorld.setBlockState(blockPos, (BlockState)blockState.cycle(AGE), 2);
        }
        return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
    }

    @Override
    public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
        int j;
        int i = this.countBambooAbove(blockView, blockPos);
        return i + (j = this.countBambooBelow(blockView, blockPos)) + 1 < 16 && blockView.getBlockState(blockPos.up(i)).get(STAGE) != 1;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
        int i = this.countBambooAbove(world, blockPos);
        int j = this.countBambooBelow(world, blockPos);
        int k = i + j + 1;
        int l = 1 + random.nextInt(2);
        for (int m = 0; m < l; ++m) {
            BlockPos blockPos2 = blockPos.up(i);
            BlockState blockState2 = world.getBlockState(blockPos2);
            if (k >= 16 || blockState2.get(STAGE) == 1 || !world.isAir(blockPos2.up())) {
                return;
            }
            this.updateLeaves(blockState2, world, blockPos2, random, k);
            ++i;
            ++k;
        }
    }

    @Override
    public float calcBlockBreakingDelta(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
        if (playerEntity.getMainHandStack().getItem() instanceof SwordItem) {
            return 1.0f;
        }
        return super.calcBlockBreakingDelta(blockState, playerEntity, blockView, blockPos);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    protected void updateLeaves(BlockState blockState, World world, BlockPos blockPos, Random random, int i) {
        BlockState blockState2 = world.getBlockState(blockPos.down());
        BlockPos blockPos2 = blockPos.down(2);
        BlockState blockState3 = world.getBlockState(blockPos2);
        BambooLeaves bambooLeaves = BambooLeaves.NONE;
        if (i >= 1) {
            if (blockState2.getBlock() != Blocks.BAMBOO || blockState2.get(LEAVES) == BambooLeaves.NONE) {
                bambooLeaves = BambooLeaves.SMALL;
            } else if (blockState2.getBlock() == Blocks.BAMBOO && blockState2.get(LEAVES) != BambooLeaves.NONE) {
                bambooLeaves = BambooLeaves.LARGE;
                if (blockState3.getBlock() == Blocks.BAMBOO) {
                    world.setBlockState(blockPos.down(), (BlockState)blockState2.with(LEAVES, BambooLeaves.SMALL), 3);
                    world.setBlockState(blockPos2, (BlockState)blockState3.with(LEAVES, BambooLeaves.NONE), 3);
                }
            }
        }
        int j = blockState.get(AGE) == 1 || blockState3.getBlock() == Blocks.BAMBOO ? 1 : 0;
        int k = i >= 11 && random.nextFloat() < 0.25f || i == 15 ? 1 : 0;
        world.setBlockState(blockPos.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with(AGE, j)).with(LEAVES, bambooLeaves)).with(STAGE, k), 3);
    }

    protected int countBambooAbove(BlockView blockView, BlockPos blockPos) {
        int i;
        for (i = 0; i < 16 && blockView.getBlockState(blockPos.up(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {
        }
        return i;
    }

    protected int countBambooBelow(BlockView blockView, BlockPos blockPos) {
        int i;
        for (i = 0; i < 16 && blockView.getBlockState(blockPos.down(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {
        }
        return i;
    }
}

