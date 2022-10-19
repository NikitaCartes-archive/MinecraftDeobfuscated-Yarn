/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.SignType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class HangingSignBlock
extends AbstractSignBlock {
    public static final IntProperty ROTATION = Properties.ROTATION;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    protected static final float field_40302 = 5.0f;
    protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final Map<Integer, VoxelShape> SHAPES_FOR_ROTATION = Maps.newHashMap(ImmutableMap.of(0, Block.createCuboidShape(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), 4, Block.createCuboidShape(7.0, 0.0, 1.0, 9.0, 10.0, 15.0), 8, Block.createCuboidShape(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), 12, Block.createCuboidShape(7.0, 0.0, 1.0, 9.0, 10.0, 15.0)));

    public HangingSignBlock(AbstractBlock.Settings settings, SignType signType) {
        super(settings, signType);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ROTATION, 0)).with(ATTACHED, false)).with(WATERLOGGED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignBlockEntity) {
            SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
            ItemStack itemStack = player.getStackInHand(hand);
            if (!signBlockEntity.shouldRunCommand(player) && itemStack.getItem() instanceof BlockItem) {
                return ActionResult.PASS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.up()).isSideSolid(world, pos.up(), Direction.DOWN, SideShapeType.CENTER);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean bl2;
        World world = ctx.getWorld();
        FluidState fluidState = world.getFluidState(ctx.getBlockPos());
        BlockPos blockPos = ctx.getBlockPos().up();
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isIn(BlockTags.ALL_HANGING_SIGNS);
        Direction direction = Direction.fromRotation(ctx.getPlayerYaw());
        boolean bl3 = bl2 = !Block.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.DOWN) || ctx.shouldCancelInteraction();
        if (bl && !ctx.shouldCancelInteraction()) {
            Optional<Direction> optional;
            if (blockState.contains(WallHangingSignBlock.FACING)) {
                Direction direction2 = blockState.get(WallHangingSignBlock.FACING);
                if (direction2.getAxis().test(direction)) {
                    bl2 = false;
                }
            } else if (blockState.contains(ROTATION) && (optional = RotationPropertyHelper.toDirection(blockState.get(ROTATION))).isPresent() && optional.get().getAxis().test(direction)) {
                bl2 = false;
            }
        }
        int i = !bl2 ? RotationPropertyHelper.fromDirection(direction) : RotationPropertyHelper.fromYaw(ctx.getPlayerYaw());
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(ATTACHED, bl2)).with(ROTATION, i)).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = SHAPES_FOR_ROTATION.get(state.get(ROTATION));
        return voxelShape == null ? DEFAULT_SHAPE : voxelShape;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(ROTATION, rotation.rotate(state.get(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with(ROTATION, mirror.mirror(state.get(ROTATION), 16));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, ATTACHED, WATERLOGGED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HangingSignBlockEntity(pos, state);
    }
}

