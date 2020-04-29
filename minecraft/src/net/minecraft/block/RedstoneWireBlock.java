package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RedstoneWireBlock extends Block {
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
	private static final VoxelShape field_24413 = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
	private static final Map<Direction, VoxelShape> field_24414 = Maps.newEnumMap(
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
	private static final Map<Direction, VoxelShape> field_24415 = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.NORTH,
			VoxelShapes.union((VoxelShape)field_24414.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
			Direction.SOUTH,
			VoxelShapes.union((VoxelShape)field_24414.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
			Direction.EAST,
			VoxelShapes.union((VoxelShape)field_24414.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
			Direction.WEST,
			VoxelShapes.union((VoxelShape)field_24414.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))
		)
	);
	private final Map<BlockState, VoxelShape> field_24416 = Maps.<BlockState, VoxelShape>newHashMap();
	private boolean wiresGivePower = true;

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

		for (BlockState blockState : this.getStateManager().getStates()) {
			if ((Integer)blockState.get(POWER) == 0) {
				this.field_24416.put(blockState, this.method_27845(blockState));
			}
		}
	}

	private VoxelShape method_27845(BlockState blockState) {
		VoxelShape voxelShape = field_24413;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			WireConnection wireConnection = blockState.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
			if (wireConnection == WireConnection.SIDE) {
				voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)field_24414.get(direction));
			} else if (wireConnection == WireConnection.UP) {
				voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)field_24415.get(direction));
			}
		}

		return voxelShape;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.field_24416.get(state.with(POWER, Integer.valueOf(0)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.method_27840(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
	}

	private BlockState method_27840(BlockView blockView, BlockState blockState, BlockPos blockPos) {
		blockState = this.method_27843(blockView, this.getDefaultState().with(POWER, blockState.get(POWER)), blockPos);
		boolean bl = ((WireConnection)blockState.get(WIRE_CONNECTION_NORTH)).isConnected();
		boolean bl2 = ((WireConnection)blockState.get(WIRE_CONNECTION_SOUTH)).isConnected();
		boolean bl3 = ((WireConnection)blockState.get(WIRE_CONNECTION_EAST)).isConnected();
		boolean bl4 = ((WireConnection)blockState.get(WIRE_CONNECTION_WEST)).isConnected();
		boolean bl5 = !bl && !bl2;
		boolean bl6 = !bl3 && !bl4;
		if (!bl4 && bl5) {
			blockState = blockState.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
		}

		if (!bl3 && bl5) {
			blockState = blockState.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
		}

		if (!bl && bl6) {
			blockState = blockState.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
		}

		if (!bl2 && bl6) {
			blockState = blockState.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
		}

		return blockState;
	}

	private BlockState method_27843(BlockView blockView, BlockState blockState, BlockPos blockPos) {
		boolean bl = !blockView.getBlockState(blockPos.up()).isSolidBlock(blockView, blockPos);

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (!((WireConnection)blockState.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) {
				WireConnection wireConnection = this.method_27841(blockView, blockPos, direction, bl);
				blockState = blockState.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
			}
		}

		return blockState;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		if (direction == Direction.DOWN) {
			return state;
		} else if (direction == Direction.UP) {
			return this.method_27840(world, state, pos);
		} else {
			WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
			return wireConnection.isConnected() == ((WireConnection)state.get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()
					&& !method_27846(state)
				? state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection)
				: this.method_27840(
					world, this.getDefaultState().with(POWER, state.get(POWER)).with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos
				);
		}
	}

	private static boolean method_27846(BlockState blockState) {
		return ((WireConnection)blockState.get(WIRE_CONNECTION_NORTH)).isConnected()
			&& ((WireConnection)blockState.get(WIRE_CONNECTION_SOUTH)).isConnected()
			&& ((WireConnection)blockState.get(WIRE_CONNECTION_EAST)).isConnected()
			&& ((WireConnection)blockState.get(WIRE_CONNECTION_WEST)).isConnected();
	}

	@Override
	public void prepare(BlockState state, IWorld world, BlockPos pos, int flags) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			WireConnection wireConnection = state.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
			if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
				mutable.move(Direction.DOWN);
				BlockState blockState = world.getBlockState(mutable);
				if (!blockState.isOf(Blocks.OBSERVER)) {
					BlockPos blockPos = mutable.offset(direction.getOpposite());
					BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), world, mutable, blockPos);
					replaceBlock(blockState, blockState2, world, mutable, flags);
				}

				mutable.set(pos, direction).move(Direction.UP);
				BlockState blockState3 = world.getBlockState(mutable);
				if (!blockState3.isOf(Blocks.OBSERVER)) {
					BlockPos blockPos2 = mutable.offset(direction.getOpposite());
					BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), world, mutable, blockPos2);
					replaceBlock(blockState3, blockState4, world, mutable, flags);
				}
			}
		}
	}

	private WireConnection getRenderConnectionType(BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.method_27841(blockView, blockPos, direction, !blockView.getBlockState(blockPos.up()).isSolidBlock(blockView, blockPos));
	}

	private WireConnection method_27841(BlockView blockView, BlockPos blockPos, Direction direction, boolean bl) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (bl) {
			boolean bl2 = blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP) || blockState.isOf(Blocks.HOPPER);
			if (bl2 && connectsTo(blockView.getBlockState(blockPos2.up()))) {
				if (blockState.isFullCube(blockView, blockPos2)) {
					return WireConnection.UP;
				}

				return WireConnection.SIDE;
			}
		}

		return !connectsTo(blockState, direction) && (blockState.isSolidBlock(blockView, blockPos2) || !connectsTo(blockView.getBlockState(blockPos2.down())))
			? WireConnection.NONE
			: WireConnection.SIDE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) || blockState.isOf(Blocks.HOPPER);
	}

	private void update(World world, BlockPos pos, BlockState state) {
		int i = this.method_27842(world, pos);
		if ((Integer)state.get(POWER) != i) {
			if (world.getBlockState(pos) == state) {
				world.setBlockState(pos, state.with(POWER, Integer.valueOf(i)), 2);
			}

			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(pos);

			for (Direction direction : Direction.values()) {
				set.add(pos.offset(direction));
			}

			for (BlockPos blockPos : set) {
				world.updateNeighborsAlways(blockPos, this);
			}
		}
	}

	private int method_27842(World world, BlockPos blockPos) {
		this.wiresGivePower = false;
		int i = world.getReceivedRedstonePower(blockPos);
		this.wiresGivePower = true;
		int j = 0;
		if (i < 15) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState = world.getBlockState(blockPos2);
				j = Math.max(j, this.increasePower(blockState));
				BlockPos blockPos3 = blockPos.up();
				if (blockState.isSolidBlock(world, blockPos2) && !world.getBlockState(blockPos3).isSolidBlock(world, blockPos3)) {
					j = Math.max(j, this.increasePower(world.getBlockState(blockPos2.up())));
				} else if (!blockState.isSolidBlock(world, blockPos2)) {
					j = Math.max(j, this.increasePower(world.getBlockState(blockPos2.down())));
				}
			}
		}

		return Math.max(i, j - 1);
	}

	private int increasePower(BlockState state) {
		return state.isOf(this) ? (Integer)state.get(POWER) : 0;
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
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock()) && !world.isClient) {
			this.update(world, pos, state);

			for (Direction direction : Direction.Type.VERTICAL) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}

			this.method_27844(world, pos);
		}
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!notify && !state.isOf(newState.getBlock())) {
			super.onBlockRemoved(state, world, pos, newState, notify);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.updateNeighborsAlways(pos.offset(direction), this);
				}

				this.update(world, pos, state);
				this.method_27844(world, pos);
			}
		}
	}

	private void method_27844(World world, BlockPos blockPos) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			this.updateNeighbors(world, blockPos.offset(direction));
		}

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
				this.updateNeighbors(world, blockPos2.up());
			} else {
				this.updateNeighbors(world, blockPos2.down());
			}
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			if (state.canPlaceAt(world, pos)) {
				this.update(world, pos, state);
			} else {
				dropStacks(state, world, pos);
				world.removeBlock(pos, false);
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (this.wiresGivePower && direction != Direction.DOWN) {
			int i = (Integer)state.get(POWER);
			if (i == 0) {
				return 0;
			} else {
				return direction != Direction.UP
						&& !((WireConnection)this.method_27840(world, state, pos).get((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite())))
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
	public boolean emitsRedstonePower(BlockState state) {
		return this.wiresGivePower;
	}

	@Environment(EnvType.CLIENT)
	public static int getWireColor(int powerLevel) {
		float f = (float)powerLevel / 15.0F;
		float g = f * 0.6F + 0.4F;
		if (powerLevel == 0) {
			g = 0.3F;
		}

		float h = f * f * 0.7F - 0.5F;
		float i = f * f * 0.6F - 0.7F;
		if (h < 0.0F) {
			h = 0.0F;
		}

		if (i < 0.0F) {
			i = 0.0F;
		}

		int j = MathHelper.clamp((int)(g * 255.0F), 0, 255);
		int k = MathHelper.clamp((int)(h * 255.0F), 0, 255);
		int l = MathHelper.clamp((int)(i * 255.0F), 0, 255);
		return 0xFF000000 | j << 16 | k << 8 | l;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		int i = (Integer)state.get(POWER);
		if (i != 0) {
			double d = (double)pos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)pos.getY() + 0.0625F);
			double f = (double)pos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			world.addParticle(new DustParticleEffect(h, j, k, 1.0F), d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))
					.with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))
					.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))
					.with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
			case CLOCKWISE_90:
				return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))
					.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))
					.with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))
					.with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
			case FRONT_BACK:
				return state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
			default:
				return super.mirror(state, mirror);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
	}
}
