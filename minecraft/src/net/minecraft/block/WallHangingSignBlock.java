package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class WallHangingSignBlock extends AbstractSignBlock {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final VoxelShape NORTH_SOUTH_COLLISION_SHAPE = Block.createCuboidShape(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
	public static final VoxelShape EAST_WEST_COLLISION_SHAPE = Block.createCuboidShape(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);
	public static final VoxelShape NORTH_SOUTH_SHAPE = VoxelShapes.union(NORTH_SOUTH_COLLISION_SHAPE, Block.createCuboidShape(1.0, 0.0, 7.0, 15.0, 10.0, 9.0));
	public static final VoxelShape EAST_WEST_SHAPE = VoxelShapes.union(EAST_WEST_COLLISION_SHAPE, Block.createCuboidShape(7.0, 0.0, 1.0, 9.0, 10.0, 15.0));
	private static final Map<Direction, VoxelShape> OUTLINE_SHAPES = Maps.newEnumMap(
		ImmutableMap.of(Direction.NORTH, NORTH_SOUTH_SHAPE, Direction.SOUTH, NORTH_SOUTH_SHAPE, Direction.EAST, EAST_WEST_SHAPE, Direction.WEST, EAST_WEST_SHAPE)
	);

	public WallHangingSignBlock(AbstractBlock.Settings settings, WoodType woodType) {
		super(settings.sounds(woodType.hangingSignSoundType()), woodType);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
			ItemStack itemStack = player.getStackInHand(hand);
			if (!signBlockEntity.shouldRunCommand(player) && itemStack.getItem() instanceof BlockItem) {
				return ActionResult.PASS;
			}
		}

		return super.onUse(state, world, pos, player, hand, hit);
	}

	@Override
	public String getTranslationKey() {
		return this.asItem().getTranslationKey();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)OUTLINE_SHAPES.get(state.get(FACING));
	}

	@Override
	public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
		return this.getOutlineShape(state, world, pos, ShapeContext.absent());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction)state.get(FACING)) {
			case EAST:
			case WEST:
				return EAST_WEST_COLLISION_SHAPE;
			default:
				return NORTH_SOUTH_COLLISION_SHAPE;
		}
	}

	public boolean canAttachAt(BlockState state, WorldView world, BlockPos pos) {
		Direction direction = ((Direction)state.get(FACING)).rotateYClockwise();
		Direction direction2 = ((Direction)state.get(FACING)).rotateYCounterclockwise();
		return this.canAttachTo(world, state, pos.offset(direction), direction2) || this.canAttachTo(world, state, pos.offset(direction2), direction);
	}

	public boolean canAttachTo(WorldView world, BlockState state, BlockPos toPos, Direction direction) {
		BlockState blockState = world.getBlockState(toPos);
		return blockState.isIn(BlockTags.WALL_HANGING_SIGNS)
			? ((Direction)blockState.get(FACING)).getAxis().test(state.get(FACING))
			: blockState.isSideSolid(world, toPos, direction, SideShapeType.FULL);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = this.getDefaultState();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		WorldView worldView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();

		for (Direction direction : ctx.getPlacementDirections()) {
			if (direction.getAxis().isHorizontal() && !direction.getAxis().test(ctx.getSide())) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.with(FACING, direction2);
				if (blockState.canPlaceAt(worldView, blockPos) && this.canAttachAt(blockState, worldView, blockPos)) {
					return blockState.with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
				}
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction.getAxis() == ((Direction)state.get(FACING)).rotateYClockwise().getAxis() && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new HangingSignBlockEntity(pos, state);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
