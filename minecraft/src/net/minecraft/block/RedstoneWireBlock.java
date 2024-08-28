package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.DefaultRedstoneController;
import net.minecraft.world.ExperimentalRedstoneController;
import net.minecraft.world.RedstoneController;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import org.joml.Vector3f;

public class RedstoneWireBlock extends Block {
	public static final MapCodec<RedstoneWireBlock> CODEC = createCodec(RedstoneWireBlock::new);
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
	public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
	public static final IntProperty POWER = Properties.POWER;
	public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST
		)
	);
	protected static final int field_31222 = 1;
	protected static final int field_31223 = 3;
	protected static final int field_31224 = 13;
	protected static final int field_31225 = 3;
	protected static final int field_31226 = 13;
	private static final VoxelShape DOT_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
	private static final Map<Direction, VoxelShape> DIRECTION_TO_SIDE_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
			Direction.SOUTH,
			Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
			Direction.EAST,
			Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
			Direction.WEST,
			Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)
		)
	);
	private static final Map<Direction, VoxelShape> DIRECTION_TO_UP_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			VoxelShapes.union((VoxelShape)DIRECTION_TO_SIDE_SHAPE.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
			Direction.SOUTH,
			VoxelShapes.union((VoxelShape)DIRECTION_TO_SIDE_SHAPE.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
			Direction.EAST,
			VoxelShapes.union((VoxelShape)DIRECTION_TO_SIDE_SHAPE.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
			Direction.WEST,
			VoxelShapes.union((VoxelShape)DIRECTION_TO_SIDE_SHAPE.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))
		)
	);
	private static final Map<BlockState, VoxelShape> SHAPES = Maps.<BlockState, VoxelShape>newHashMap();
	private static final Vector3f[] COLORS = Util.make(new Vector3f[16], vector3fs -> {
		for (int i = 0; i <= 15; i++) {
			float f = (float)i / 15.0F;
			float g = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
			float h = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
			float j = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);
			vector3fs[i] = new Vector3f(g, h, j);
		}
	});
	private static final float field_31221 = 0.2F;
	private final BlockState dotState;
	private final RedstoneController redstoneController = new DefaultRedstoneController(this);
	private boolean wiresGivePower = true;

	@Override
	public MapCodec<RedstoneWireBlock> getCodec() {
		return CODEC;
	}

	public RedstoneWireBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(WIRE_CONNECTION_NORTH, WireConnection.NONE)
				.with(WIRE_CONNECTION_EAST, WireConnection.NONE)
				.with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)
				.with(WIRE_CONNECTION_WEST, WireConnection.NONE)
				.with(POWER, Integer.valueOf(0))
		);
		this.dotState = this.getDefaultState()
			.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)
			.with(WIRE_CONNECTION_EAST, WireConnection.SIDE)
			.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE)
			.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);

		for (BlockState blockState : this.getStateManager().getStates()) {
			if ((Integer)blockState.get(POWER) == 0) {
				SHAPES.put(blockState, this.getShapeForState(blockState));
			}
		}
	}

	private VoxelShape getShapeForState(BlockState state) {
		VoxelShape voxelShape = DOT_SHAPE;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			WireConnection wireConnection = state.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
			if (wireConnection == WireConnection.SIDE) {
				voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)DIRECTION_TO_SIDE_SHAPE.get(direction));
			} else if (wireConnection == WireConnection.UP) {
				voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)DIRECTION_TO_UP_SHAPE.get(direction));
			}
		}

		return voxelShape;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)SHAPES.get(state.with(POWER, Integer.valueOf(0)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos());
	}

	private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
		boolean bl = isNotConnected(state);
		state = this.getDefaultWireState(world, this.getDefaultState().with(POWER, (Integer)state.get(POWER)), pos);
		if (bl && isNotConnected(state)) {
			return state;
		} else {
			boolean bl2 = ((WireConnection)state.get(WIRE_CONNECTION_NORTH)).isConnected();
			boolean bl3 = ((WireConnection)state.get(WIRE_CONNECTION_SOUTH)).isConnected();
			boolean bl4 = ((WireConnection)state.get(WIRE_CONNECTION_EAST)).isConnected();
			boolean bl5 = ((WireConnection)state.get(WIRE_CONNECTION_WEST)).isConnected();
			boolean bl6 = !bl2 && !bl3;
			boolean bl7 = !bl4 && !bl5;
			if (!bl5 && bl6) {
				state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
			}

			if (!bl4 && bl6) {
				state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
			}

			if (!bl2 && bl7) {
				state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
			}

			if (!bl3 && bl7) {
				state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
			}

			return state;
		}
	}

	private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
		boolean bl = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (!((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) {
				WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, bl);
				state = state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
			}
		}

		return state;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (direction == Direction.DOWN) {
			return !this.canRunOnTop(world, neighborPos, neighborState) ? Blocks.AIR.getDefaultState() : state;
		} else if (direction == Direction.UP) {
			return this.getPlacementState(world, state, pos);
		} else {
			WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
			return wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
					&& !isFullyConnected(state)
				? state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection)
				: this.getPlacementState(
					world, this.dotState.with(POWER, (Integer)state.get(POWER)).with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos
				);
		}
	}

	private static boolean isFullyConnected(BlockState state) {
		return ((WireConnection)state.get(WIRE_CONNECTION_NORTH)).isConnected()
			&& ((WireConnection)state.get(WIRE_CONNECTION_SOUTH)).isConnected()
			&& ((WireConnection)state.get(WIRE_CONNECTION_EAST)).isConnected()
			&& ((WireConnection)state.get(WIRE_CONNECTION_WEST)).isConnected();
	}

	private static boolean isNotConnected(BlockState state) {
		return !((WireConnection)state.get(WIRE_CONNECTION_NORTH)).isConnected()
			&& !((WireConnection)state.get(WIRE_CONNECTION_SOUTH)).isConnected()
			&& !((WireConnection)state.get(WIRE_CONNECTION_EAST)).isConnected()
			&& !((WireConnection)state.get(WIRE_CONNECTION_WEST)).isConnected();
	}

	@Override
	protected void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			WireConnection wireConnection = state.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
			if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
				mutable.move(Direction.DOWN);
				BlockState blockState = world.getBlockState(mutable);
				if (blockState.isOf(this)) {
					BlockPos blockPos = mutable.offset(direction.getOpposite());
					world.replaceWithStateForNeighborUpdate(direction.getOpposite(), mutable, blockPos, world.getBlockState(blockPos), flags, maxUpdateDepth);
				}

				mutable.set(pos, direction).move(Direction.UP);
				BlockState blockState2 = world.getBlockState(mutable);
				if (blockState2.isOf(this)) {
					BlockPos blockPos2 = mutable.offset(direction.getOpposite());
					world.replaceWithStateForNeighborUpdate(direction.getOpposite(), mutable, blockPos2, world.getBlockState(blockPos2), flags, maxUpdateDepth);
				}
			}
		}
	}

	private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
		return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
	}

	private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction, boolean bl) {
		BlockPos blockPos = pos.offset(direction);
		BlockState blockState = world.getBlockState(blockPos);
		if (bl) {
			boolean bl2 = blockState.getBlock() instanceof TrapdoorBlock || this.canRunOnTop(world, blockPos, blockState);
			if (bl2 && connectsTo(world.getBlockState(blockPos.up()))) {
				if (blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
					return WireConnection.UP;
				}

				return WireConnection.SIDE;
			}
		}

		return !connectsTo(blockState, direction) && (blockState.isSolidBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down())))
			? WireConnection.NONE
			: WireConnection.SIDE;
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return this.canRunOnTop(world, blockPos, blockState);
	}

	private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
		return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
	}

	private void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
		if (areRedstoneExperimentsEnabled(world)) {
			new ExperimentalRedstoneController(this).update(world, pos, state, orientation, blockAdded);
		} else {
			this.redstoneController.update(world, pos, state, orientation, blockAdded);
		}
	}

	public int getStrongPower(World world, BlockPos pos) {
		this.wiresGivePower = false;
		int i = world.getReceivedRedstonePower(pos);
		this.wiresGivePower = true;
		return i;
	}

	private void updateNeighbors(World world, BlockPos pos) {
		if (world.getBlockState(pos).isOf(this)) {
			world.updateNeighborsAlways(pos, this);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	@Override
	protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock()) && !world.isClient) {
			this.update(world, pos, state, null, true);

			for (Direction direction : Direction.Type.VERTICAL) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}

			this.updateOffsetNeighbors(world, pos);
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			super.onStateReplaced(state, world, pos, newState, moved);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.updateNeighborsAlways(pos.offset(direction), this);
				}

				this.update(world, pos, state, null, false);
				this.updateOffsetNeighbors(world, pos);
			}
		}
	}

	private void updateOffsetNeighbors(World world, BlockPos pos) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			this.updateNeighbors(world, pos.offset(direction));
		}

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
				this.updateNeighbors(world, blockPos.up());
			} else {
				this.updateNeighbors(world, blockPos.down());
			}
		}
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
		if (!world.isClient) {
			if (sourceBlock != this || !areRedstoneExperimentsEnabled(world)) {
				if (state.canPlaceAt(world, pos)) {
					this.update(world, pos, state, wireOrientation, false);
				} else {
					dropStacks(state, world, pos);
					world.removeBlock(pos, false);
				}
			}
		}
	}

	private static boolean areRedstoneExperimentsEnabled(World world) {
		return world.getEnabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS);
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (this.wiresGivePower && direction != Direction.DOWN) {
			int i = (Integer)state.get(POWER);
			if (i == 0) {
				return 0;
			} else {
				return direction != Direction.UP
						&& !((WireConnection)this.getPlacementState(world, state, pos).get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite())))
							.isConnected()
					? 0
					: i;
			}
		} else {
			return 0;
		}
	}

	protected static boolean connectsTo(BlockState state) {
		return connectsTo(state, null);
	}

	protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
		if (state.isOf(Blocks.REDSTONE_WIRE)) {
			return true;
		} else if (state.isOf(Blocks.REPEATER)) {
			Direction direction = state.get(RepeaterBlock.FACING);
			return direction == dir || direction.getOpposite() == dir;
		} else {
			return state.isOf(Blocks.OBSERVER) ? dir == state.get(ObserverBlock.FACING) : state.emitsRedstonePower() && dir != null;
		}
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return this.wiresGivePower;
	}

	public static int getWireColor(int powerLevel) {
		Vector3f vector3f = COLORS[powerLevel];
		return ColorHelper.fromFloats(0.0F, vector3f.x(), vector3f.y(), vector3f.z());
	}

	private void addPoweredParticles(
		World world, Random random, BlockPos pos, Vector3f color, Direction perpendicular, Direction direction, float minOffset, float maxOffset
	) {
		float f = maxOffset - minOffset;
		if (!(random.nextFloat() >= 0.2F * f)) {
			float g = 0.4375F;
			float h = minOffset + f * random.nextFloat();
			double d = 0.5 + (double)(0.4375F * (float)perpendicular.getOffsetX()) + (double)(h * (float)direction.getOffsetX());
			double e = 0.5 + (double)(0.4375F * (float)perpendicular.getOffsetY()) + (double)(h * (float)direction.getOffsetY());
			double i = 0.5 + (double)(0.4375F * (float)perpendicular.getOffsetZ()) + (double)(h * (float)direction.getOffsetZ());
			world.addParticle(new DustParticleEffect(color, 1.0F), (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + i, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		int i = (Integer)state.get(POWER);
		if (i != 0) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				WireConnection wireConnection = state.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
				switch (wireConnection) {
					case UP:
						this.addPoweredParticles(world, random, pos, COLORS[i], direction, Direction.UP, -0.5F, 0.5F);
					case SIDE:
						this.addPoweredParticles(world, random, pos, COLORS[i], Direction.DOWN, direction, 0.0F, 0.5F);
						break;
					case NONE:
					default:
						this.addPoweredParticles(world, random, pos, COLORS[i], Direction.DOWN, direction, 0.0F, 0.3F);
				}
			}
		}
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(WIRE_CONNECTION_NORTH, (WireConnection)state.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_EAST, (WireConnection)state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_NORTH))
					.with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(WIRE_CONNECTION_NORTH, (WireConnection)state.get(WIRE_CONNECTION_EAST))
					.with(WIRE_CONNECTION_EAST, (WireConnection)state.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_NORTH));
			case CLOCKWISE_90:
				return state.with(WIRE_CONNECTION_NORTH, (WireConnection)state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_EAST, (WireConnection)state.get(WIRE_CONNECTION_NORTH))
					.with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_EAST))
					.with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_SOUTH));
			default:
				return state;
		}
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(WIRE_CONNECTION_NORTH, (WireConnection)state.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_SOUTH, (WireConnection)state.get(WIRE_CONNECTION_NORTH));
			case FRONT_BACK:
				return state.with(WIRE_CONNECTION_EAST, (WireConnection)state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_WEST, (WireConnection)state.get(WIRE_CONNECTION_EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!player.getAbilities().allowModifyWorld) {
			return ActionResult.PASS;
		} else {
			if (isFullyConnected(state) || isNotConnected(state)) {
				BlockState blockState = isFullyConnected(state) ? this.getDefaultState() : this.dotState;
				blockState = blockState.with(POWER, (Integer)state.get(POWER));
				blockState = this.getPlacementState(world, blockState, pos);
				if (blockState != state) {
					world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
					this.updateForNewState(world, pos, state, blockState);
					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		}
	}

	private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, null, Direction.UP);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (((WireConnection)oldState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
					!= ((WireConnection)newState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
				&& world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
				world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite(), OrientationHelper.withFrontNullable(wireOrientation, direction));
			}
		}
	}
}
