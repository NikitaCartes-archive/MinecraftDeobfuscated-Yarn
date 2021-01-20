package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class AbstractLichenBlock extends Block {
	private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
	private static final Map<Direction, VoxelShape> field_28420 = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
		enumMap.put(Direction.NORTH, SOUTH_SHAPE);
		enumMap.put(Direction.EAST, WEST_SHAPE);
		enumMap.put(Direction.SOUTH, NORTH_SHAPE);
		enumMap.put(Direction.WEST, EAST_SHAPE);
		enumMap.put(Direction.UP, UP_SHAPE);
		enumMap.put(Direction.DOWN, DOWN_SHAPE);
	});
	private static final Direction[] DIRECTIONS = Direction.values();
	private final ImmutableMap<BlockState, VoxelShape> field_28422;
	private final boolean field_28423;
	private final boolean field_28424;
	private final boolean field_28425;

	public AbstractLichenBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(method_33368(this.stateManager));
		this.field_28422 = method_33373(this.stateManager);
		this.field_28423 = Direction.Type.HORIZONTAL.stream().allMatch(this::method_33369);
		this.field_28424 = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::method_33369).count() % 2L == 0L;
		this.field_28425 = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::method_33369).count() % 2L == 0L;
	}

	protected boolean method_33369(Direction direction) {
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		for (Direction direction : DIRECTIONS) {
			if (this.method_33369(direction)) {
				builder.add(method_33374(direction));
			}
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return method_33366(state, direction) && !method_33358(world, direction, posFrom, newState) ? method_33365(state, method_33374(direction)) : state;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.field_28422.get(state);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		boolean bl = false;

		for (Direction direction : DIRECTIONS) {
			if (method_33366(state, direction)) {
				BlockPos blockPos = pos.offset(direction);
				if (!method_33358(world, direction, blockPos, world.getBlockState(blockPos))) {
					return false;
				}

				bl = true;
			}
		}

		return bl;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return method_33382(state);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		return (BlockState)Arrays.stream(ctx.getPlacementDirections())
			.map(direction -> this.method_33362(blockState, world, blockPos, direction))
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}

	@Nullable
	public BlockState method_33362(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction) {
		if (!this.method_33369(direction)) {
			return null;
		} else {
			BlockState blockState2;
			if (blockState.isOf(this)) {
				if (method_33366(blockState, direction)) {
					return null;
				}

				blockState2 = blockState;
			} else if (this.method_33378() && blockState.getFluidState().isStill()) {
				blockState2 = this.getDefaultState().with(Properties.WATERLOGGED, Boolean.valueOf(true));
			} else {
				blockState2 = this.getDefaultState();
			}

			BlockPos blockPos2 = blockPos.offset(direction);
			return method_33358(worldAccess, direction, blockPos2, worldAccess.getBlockState(blockPos2))
				? blockState2.with(method_33374(direction), Boolean.valueOf(true))
				: null;
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return !this.field_28423 ? state : this.method_33367(state, rotation::rotate);
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		if (mirror == BlockMirror.FRONT_BACK && !this.field_28424) {
			return state;
		} else {
			return mirror == BlockMirror.LEFT_RIGHT && !this.field_28425 ? state : this.method_33367(state, mirror::apply);
		}
	}

	private BlockState method_33367(BlockState blockState, Function<Direction, Direction> function) {
		BlockState blockState2 = blockState;

		for (Direction direction : DIRECTIONS) {
			if (this.method_33369(direction)) {
				blockState2 = blockState2.with(method_33374((Direction)function.apply(direction)), blockState.get(method_33374(direction)));
			}
		}

		return blockState2;
	}

	public boolean method_33375(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		List<Direction> list = Lists.<Direction>newArrayList(DIRECTIONS);
		Collections.shuffle(list);
		return list.stream()
			.filter(direction -> method_33366(blockState, direction))
			.anyMatch(direction -> this.method_33364(blockState, serverWorld, blockPos, direction, random));
	}

	public boolean method_33364(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Random random) {
		List<Direction> list = Arrays.asList(DIRECTIONS);
		Collections.shuffle(list, random);
		return list.stream().anyMatch(direction2 -> this.method_33363(blockState, worldAccess, blockPos, direction, direction2));
	}

	public boolean method_33363(BlockState blockState, WorldAccess worldAccess, BlockPos blockPos, Direction direction, Direction direction2) {
		if (direction2.getAxis() == direction.getAxis() || !method_33366(blockState, direction) || method_33366(blockState, direction2)) {
			return false;
		} else if (this.method_33359(worldAccess, blockPos, direction2)) {
			return true;
		} else {
			return this.method_33359(worldAccess, blockPos.offset(direction2), direction)
				? true
				: this.method_33359(worldAccess, blockPos.offset(direction2).offset(direction), direction2.getOpposite());
		}
	}

	private boolean method_33359(WorldAccess worldAccess, BlockPos blockPos, Direction direction) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		if (!this.method_33379(blockState)) {
			return false;
		} else {
			BlockState blockState2 = this.method_33362(blockState, worldAccess, blockPos, direction);
			return blockState2 != null ? worldAccess.setBlockState(blockPos, blockState2, 2) : false;
		}
	}

	private boolean method_33379(BlockState blockState) {
		return blockState.isAir() || blockState.isOf(this) || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isStill();
	}

	private static boolean method_33366(BlockState blockState, Direction direction) {
		BooleanProperty booleanProperty = method_33374(direction);
		return blockState.contains(booleanProperty) && (Boolean)blockState.get(booleanProperty);
	}

	private static boolean method_33358(BlockView blockView, Direction direction, BlockPos blockPos, BlockState blockState) {
		return Block.isFaceFullSquare(blockState.getCollisionShape(blockView, blockPos), direction.getOpposite());
	}

	private boolean method_33378() {
		return this.stateManager.getProperties().contains(Properties.WATERLOGGED);
	}

	private static BlockState method_33365(BlockState blockState, BooleanProperty booleanProperty) {
		BlockState blockState2 = blockState.with(booleanProperty, Boolean.valueOf(false));
		if (method_33381(blockState2)) {
			return blockState2;
		} else {
			return blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
		}
	}

	public static BooleanProperty method_33374(Direction direction) {
		return (BooleanProperty)FACING_PROPERTIES.get(direction);
	}

	private static BlockState method_33368(StateManager<Block, BlockState> stateManager) {
		BlockState blockState = stateManager.getDefaultState();

		for (BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
			if (blockState.contains(booleanProperty)) {
				blockState = blockState.with(booleanProperty, Boolean.valueOf(false));
			}
		}

		return blockState;
	}

	private static ImmutableMap<BlockState, VoxelShape> method_33373(StateManager<Block, BlockState> stateManager) {
		Map<BlockState, VoxelShape> map = (Map<BlockState, VoxelShape>)stateManager.getStates()
			.stream()
			.collect(Collectors.toMap(Function.identity(), AbstractLichenBlock::method_33380));
		return ImmutableMap.copyOf(map);
	}

	private static VoxelShape method_33380(BlockState blockState) {
		VoxelShape voxelShape = VoxelShapes.empty();

		for (Direction direction : DIRECTIONS) {
			if (method_33366(blockState, direction)) {
				voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)field_28420.get(direction));
			}
		}

		return voxelShape;
	}

	private static boolean method_33381(BlockState blockState) {
		return Arrays.stream(DIRECTIONS).anyMatch(direction -> method_33366(blockState, direction));
	}

	private static boolean method_33382(BlockState blockState) {
		return Arrays.stream(DIRECTIONS).anyMatch(direction -> !method_33366(blockState, direction));
	}
}
