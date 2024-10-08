package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class PaleMossCarpetBlock extends Block implements Fertilizable {
	public static final MapCodec<PaleMossCarpetBlock> CODEC = createCodec(PaleMossCarpetBlock::new);
	public static final BooleanProperty BOTTOM = Properties.BOTTOM;
	private static final EnumProperty<WallShape> NORTH = Properties.NORTH_WALL_SHAPE;
	private static final EnumProperty<WallShape> EAST = Properties.EAST_WALL_SHAPE;
	private static final EnumProperty<WallShape> SOUTH = Properties.SOUTH_WALL_SHAPE;
	private static final EnumProperty<WallShape> WEST = Properties.WEST_WALL_SHAPE;
	private static final Map<Direction, EnumProperty<WallShape>> WALL_SHAPES = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), map -> {
		map.put(Direction.NORTH, NORTH);
		map.put(Direction.EAST, EAST);
		map.put(Direction.SOUTH, SOUTH);
		map.put(Direction.WEST, WEST);
	}));
	private static final float field_54762 = 1.0F;
	private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
	private static final VoxelShape TALL_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape TALL_EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape TALL_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape TALL_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private static final int field_54768 = 10;
	private static final VoxelShape LOW_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 10.0, 16.0);
	private static final VoxelShape LOW_EAST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 10.0, 16.0);
	private static final VoxelShape LOW_NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 1.0);
	private static final VoxelShape LOW_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 10.0, 16.0);
	private final Map<BlockState, VoxelShape> shapeCache;

	@Override
	public MapCodec<PaleMossCarpetBlock> getCodec() {
		return CODEC;
	}

	public PaleMossCarpetBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(BOTTOM, Boolean.valueOf(true))
				.with(NORTH, WallShape.NONE)
				.with(EAST, WallShape.NONE)
				.with(SOUTH, WallShape.NONE)
				.with(WEST, WallShape.NONE)
		);
		this.shapeCache = ImmutableMap.copyOf(
			(Map<? extends BlockState, ? extends VoxelShape>)this.stateManager
				.getStates()
				.stream()
				.collect(Collectors.toMap(Function.identity(), PaleMossCarpetBlock::getShape))
		);
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state) {
		return VoxelShapes.empty();
	}

	private static VoxelShape getShape(BlockState state) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)state.get(BOTTOM)) {
			voxelShape = BOTTOM_SHAPE;
		}
		voxelShape = switch ((WallShape)state.get(NORTH)) {
			case NONE -> voxelShape;
			case LOW -> VoxelShapes.union(voxelShape, LOW_NORTH_SHAPE);
			case TALL -> VoxelShapes.union(voxelShape, TALL_NORTH_SHAPE);
		};

		voxelShape = switch ((WallShape)state.get(SOUTH)) {
			case NONE -> voxelShape;
			case LOW -> VoxelShapes.union(voxelShape, LOW_SOUTH_SHAPE);
			case TALL -> VoxelShapes.union(voxelShape, TALL_SOUTH_SHAPE);
		};

		voxelShape = switch ((WallShape)state.get(EAST)) {
			case NONE -> voxelShape;
			case LOW -> VoxelShapes.union(voxelShape, LOW_EAST_SHAPE);
			case TALL -> VoxelShapes.union(voxelShape, TALL_EAST_SHAPE);
		};

		voxelShape = switch ((WallShape)state.get(WEST)) {
			case NONE -> voxelShape;
			case LOW -> VoxelShapes.union(voxelShape, LOW_WEST_SHAPE);
			case TALL -> VoxelShapes.union(voxelShape, TALL_WEST_SHAPE);
		};
		return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.shapeCache.get(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(BOTTOM) ? BOTTOM_SHAPE : VoxelShapes.empty();
	}

	@Override
	protected boolean isTransparent(BlockState state) {
		return true;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		return state.get(BOTTOM) ? !blockState.isAir() : blockState.isOf(this) && (Boolean)blockState.get(BOTTOM);
	}

	private static boolean hasAnyShape(BlockState state) {
		if ((Boolean)state.get(BOTTOM)) {
			return true;
		} else {
			for (EnumProperty<WallShape> enumProperty : WALL_SHAPES.values()) {
				if (state.get(enumProperty) != WallShape.NONE) {
					return true;
				}
			}

			return false;
		}
	}

	private static boolean canGrowOnFace(BlockView world, BlockPos pos, Direction face) {
		if (face == Direction.UP) {
			return false;
		} else {
			BlockPos blockPos = pos.offset(face);
			return MultifaceGrowthBlock.canGrowOn(world, face, blockPos, world.getBlockState(blockPos));
		}
	}

	private static BlockState updateState(BlockState state, BlockView world, BlockPos pos, boolean bl) {
		BlockState blockState = null;
		BlockState blockState2 = null;
		bl |= state.get(BOTTOM);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			EnumProperty<WallShape> enumProperty = getWallShape(direction);
			WallShape wallShape = canGrowOnFace(world, pos, direction) ? (bl ? WallShape.LOW : state.get(enumProperty)) : WallShape.NONE;
			if (wallShape == WallShape.LOW) {
				if (blockState == null) {
					blockState = world.getBlockState(pos.up());
				}

				if (blockState.isOf(Blocks.PALE_MOSS_CARPET) && blockState.get(enumProperty) != WallShape.NONE && !(Boolean)blockState.get(BOTTOM)) {
					wallShape = WallShape.TALL;
				}

				if (!(Boolean)state.get(BOTTOM)) {
					if (blockState2 == null) {
						blockState2 = world.getBlockState(pos.down());
					}

					if (blockState2.isOf(Blocks.PALE_MOSS_CARPET) && blockState2.get(enumProperty) == WallShape.NONE) {
						wallShape = WallShape.NONE;
					}
				}
			}

			state = state.with(enumProperty, wallShape);
		}

		return state;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return updateState(this.getDefaultState(), ctx.getWorld(), ctx.getBlockPos(), true);
	}

	public static void placeAt(WorldAccess world, BlockPos pos, Random random, int flags) {
		BlockState blockState = Blocks.PALE_MOSS_CARPET.getDefaultState();
		BlockState blockState2 = updateState(blockState, world, pos, true);
		world.setBlockState(pos, blockState2, Block.NOTIFY_ALL);
		BlockState blockState3 = createUpperState(world, pos, random::nextBoolean);
		if (!blockState3.isAir()) {
			world.setBlockState(pos.up(), blockState3, flags);
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (!world.isClient) {
			Random random = world.getRandom();
			BlockState blockState = createUpperState(world, pos, random::nextBoolean);
			if (!blockState.isAir()) {
				world.setBlockState(pos.up(), blockState, Block.NOTIFY_ALL);
			}
		}
	}

	private static BlockState createUpperState(BlockView world, BlockPos pos, BooleanSupplier booleanSupplier) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = blockState.isOf(Blocks.PALE_MOSS_CARPET);
		if ((!bl || !(Boolean)blockState.get(BOTTOM)) && (bl || blockState.isReplaceable())) {
			BlockState blockState2 = Blocks.PALE_MOSS_CARPET.getDefaultState().with(BOTTOM, Boolean.valueOf(false));
			BlockState blockState3 = updateState(blockState2, world, pos.up(), true);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				EnumProperty<WallShape> enumProperty = getWallShape(direction);
				if (blockState3.get(enumProperty) != WallShape.NONE && !booleanSupplier.getAsBoolean()) {
					blockState3 = blockState3.with(enumProperty, WallShape.NONE);
				}
			}

			return hasAnyShape(blockState3) && blockState3 != blockState ? blockState3 : Blocks.AIR.getDefaultState();
		} else {
			return Blocks.AIR.getDefaultState();
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (!state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			BlockState blockState = updateState(state, world, pos, false);
			return !hasAnyShape(blockState) ? Blocks.AIR.getDefaultState() : blockState;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(BOTTOM, NORTH, EAST, SOUTH, WEST);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return switch (rotation) {
			case CLOCKWISE_180 -> (BlockState)state.with(NORTH, (WallShape)state.get(SOUTH))
			.with(EAST, (WallShape)state.get(WEST))
			.with(SOUTH, (WallShape)state.get(NORTH))
			.with(WEST, (WallShape)state.get(EAST));
			case COUNTERCLOCKWISE_90 -> (BlockState)state.with(NORTH, (WallShape)state.get(EAST))
			.with(EAST, (WallShape)state.get(SOUTH))
			.with(SOUTH, (WallShape)state.get(WEST))
			.with(WEST, (WallShape)state.get(NORTH));
			case CLOCKWISE_90 -> (BlockState)state.with(NORTH, (WallShape)state.get(WEST))
			.with(EAST, (WallShape)state.get(NORTH))
			.with(SOUTH, (WallShape)state.get(EAST))
			.with(WEST, (WallShape)state.get(SOUTH));
			default -> state;
		};
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return switch (mirror) {
			case LEFT_RIGHT -> (BlockState)state.with(NORTH, (WallShape)state.get(SOUTH)).with(SOUTH, (WallShape)state.get(NORTH));
			case FRONT_BACK -> (BlockState)state.with(EAST, (WallShape)state.get(WEST)).with(WEST, (WallShape)state.get(EAST));
			default -> super.mirror(state, mirror);
		};
	}

	@Nullable
	public static EnumProperty<WallShape> getWallShape(Direction face) {
		return (EnumProperty<WallShape>)WALL_SHAPES.get(face);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return (Boolean)state.get(BOTTOM);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return !createUpperState(world, pos, () -> true).isAir();
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		BlockState blockState = createUpperState(world, pos, () -> true);
		if (!blockState.isAir()) {
			world.setBlockState(pos.up(), blockState, Block.NOTIFY_ALL);
		}
	}
}
