package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.EntityContext;
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
	protected static final VoxelShape[] WIRE_CONNECTIONS_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(3.0, 0.0, 0.0, 16.0, 1.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
	};
	private boolean wiresGivePower = true;
	private final Set<BlockPos> affectedNeighbors = Sets.<BlockPos>newHashSet();

	public RedstoneWireBlock(Block.Settings settings) {
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
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return WIRE_CONNECTIONS_TO_SHAPE[getWireConnectionMask(state)];
	}

	private static int getWireConnectionMask(BlockState state) {
		int i = 0;
		boolean bl = state.get(WIRE_CONNECTION_NORTH) != WireConnection.NONE;
		boolean bl2 = state.get(WIRE_CONNECTION_EAST) != WireConnection.NONE;
		boolean bl3 = state.get(WIRE_CONNECTION_SOUTH) != WireConnection.NONE;
		boolean bl4 = state.get(WIRE_CONNECTION_WEST) != WireConnection.NONE;
		if (bl || bl3 && !bl && !bl2 && !bl4) {
			i |= 1 << Direction.NORTH.getHorizontal();
		}

		if (bl2 || bl4 && !bl && !bl2 && !bl3) {
			i |= 1 << Direction.EAST.getHorizontal();
		}

		if (bl3 || bl && !bl2 && !bl3 && !bl4) {
			i |= 1 << Direction.SOUTH.getHorizontal();
		}

		if (bl4 || bl2 && !bl && !bl3 && !bl4) {
			i |= 1 << Direction.WEST.getHorizontal();
		}

		return i;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		return this.getDefaultState()
			.with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(blockView, blockPos, Direction.WEST))
			.with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(blockView, blockPos, Direction.EAST))
			.with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(blockView, blockPos, Direction.NORTH))
			.with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(blockView, blockPos, Direction.SOUTH));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (facing == Direction.DOWN) {
			return state;
		} else {
			return facing == Direction.UP
				? state.with(WIRE_CONNECTION_WEST, this.getRenderConnectionType(world, pos, Direction.WEST))
					.with(WIRE_CONNECTION_EAST, this.getRenderConnectionType(world, pos, Direction.EAST))
					.with(WIRE_CONNECTION_NORTH, this.getRenderConnectionType(world, pos, Direction.NORTH))
					.with(WIRE_CONNECTION_SOUTH, this.getRenderConnectionType(world, pos, Direction.SOUTH))
				: state.with((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(facing), this.getRenderConnectionType(world, pos, facing));
		}
	}

	@Override
	public void method_9517(BlockState state, IWorld world, BlockPos pos, int flags) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			WireConnection wireConnection = state.get((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
			if (wireConnection != WireConnection.NONE && world.getBlockState(mutable.move(pos, direction)).getBlock() != this) {
				mutable.setOffset(Direction.DOWN);
				BlockState blockState = world.getBlockState(mutable);
				if (blockState.getBlock() != Blocks.OBSERVER) {
					BlockPos blockPos = mutable.offset(direction.getOpposite());
					BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), world, mutable, blockPos);
					replaceBlock(blockState, blockState2, world, mutable, flags);
				}

				mutable.move(pos, direction).setOffset(Direction.UP);
				BlockState blockState3 = world.getBlockState(mutable);
				if (blockState3.getBlock() != Blocks.OBSERVER) {
					BlockPos blockPos2 = mutable.offset(direction.getOpposite());
					BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), world, mutable, blockPos2);
					replaceBlock(blockState3, blockState4, world, mutable, flags);
				}
			}
		}
	}

	private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = world.getBlockState(blockPos);
		BlockPos blockPos2 = pos.up();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (!blockState2.isSimpleFullBlock(world, blockPos2)) {
			boolean bl = blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) || blockState.getBlock() == Blocks.HOPPER;
			if (bl && connectsTo(world.getBlockState(blockPos.up()))) {
				if (blockState.isFullCube(world, blockPos)) {
					return WireConnection.UP;
				}

				return WireConnection.SIDE;
			}
		}

		return !connectsTo(blockState, dir) && (blockState.isSimpleFullBlock(world, blockPos) || !connectsTo(world.getBlockState(blockPos.down())))
			? WireConnection.NONE
			: WireConnection.SIDE;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) || blockState.getBlock() == Blocks.HOPPER;
	}

	private BlockState update(World world, BlockPos pos, BlockState state) {
		state = this.updateLogic(world, pos, state);
		List<BlockPos> list = Lists.<BlockPos>newArrayList(this.affectedNeighbors);
		this.affectedNeighbors.clear();

		for (BlockPos blockPos : list) {
			world.updateNeighborsAlways(blockPos, this);
		}

		return state;
	}

	private BlockState updateLogic(World world, BlockPos pos, BlockState state) {
		BlockState blockState = state;
		int i = (Integer)state.get(POWER);
		this.wiresGivePower = false;
		int j = world.getReceivedRedstonePower(pos);
		this.wiresGivePower = true;
		int k = 0;
		if (j < 15) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				BlockPos blockPos = pos.offset(direction);
				BlockState blockState2 = world.getBlockState(blockPos);
				k = this.increasePower(k, blockState2);
				BlockPos blockPos2 = pos.up();
				if (blockState2.isSimpleFullBlock(world, blockPos) && !world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
					k = this.increasePower(k, world.getBlockState(blockPos.up()));
				} else if (!blockState2.isSimpleFullBlock(world, blockPos)) {
					k = this.increasePower(k, world.getBlockState(blockPos.down()));
				}
			}
		}

		int l = k - 1;
		if (j > l) {
			l = j;
		}

		if (i != l) {
			state = state.with(POWER, Integer.valueOf(l));
			if (world.getBlockState(pos) == blockState) {
				world.setBlockState(pos, state, 2);
			}

			this.affectedNeighbors.add(pos);

			for (Direction direction2 : Direction.values()) {
				this.affectedNeighbors.add(pos.offset(direction2));
			}
		}

		return state;
	}

	private void updateNeighbors(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this) {
			world.updateNeighborsAlways(pos, this);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		if (oldState.getBlock() != state.getBlock() && !world.isClient) {
			this.update(world, pos, state);

			for (Direction direction : Direction.Type.VERTICAL) {
				world.updateNeighborsAlways(pos.offset(direction), this);
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				this.updateNeighbors(world, pos.offset(direction));
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				BlockPos blockPos = pos.offset(direction);
				if (world.getBlockState(blockPos).isSimpleFullBlock(world, blockPos)) {
					this.updateNeighbors(world, blockPos.up());
				} else {
					this.updateNeighbors(world, blockPos.down());
				}
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			super.onBlockRemoved(state, world, pos, newState, moved);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.updateNeighborsAlways(pos.offset(direction), this);
				}

				this.update(world, pos, state);

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					this.updateNeighbors(world, pos.offset(direction2));
				}

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					BlockPos blockPos = pos.offset(direction2);
					if (world.getBlockState(blockPos).isSimpleFullBlock(world, blockPos)) {
						this.updateNeighbors(world, blockPos.up());
					} else {
						this.updateNeighbors(world, blockPos.down());
					}
				}
			}
		}
	}

	private int increasePower(int power, BlockState state) {
		if (state.getBlock() != this) {
			return power;
		} else {
			int i = (Integer)state.get(POWER);
			return i > power ? i : power;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
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
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction facing) {
		return !this.wiresGivePower ? 0 : state.getWeakRedstonePower(world, pos, facing);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction facing) {
		if (!this.wiresGivePower) {
			return 0;
		} else {
			int i = (Integer)state.get(POWER);
			if (i == 0) {
				return 0;
			} else if (facing == Direction.UP) {
				return i;
			} else {
				EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);

				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (this.couldConnectTo(world, pos, direction)) {
						enumSet.add(direction);
					}
				}

				if (facing.getAxis().isHorizontal() && enumSet.isEmpty()) {
					return i;
				} else {
					return enumSet.contains(facing) && !enumSet.contains(facing.rotateYCounterclockwise()) && !enumSet.contains(facing.rotateYClockwise()) ? i : 0;
				}
			}
		}
	}

	private boolean couldConnectTo(BlockView world, BlockPos pos, Direction dir) {
		BlockPos blockPos = pos.offset(dir);
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = blockState.isSimpleFullBlock(world, blockPos);
		BlockPos blockPos2 = pos.up();
		boolean bl2 = world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2);
		if (!bl2 && bl && connectsTo(world, blockPos.up())) {
			return true;
		} else if (connectsTo(blockState, dir)) {
			return true;
		} else {
			return blockState.getBlock() == Blocks.REPEATER
					&& blockState.get(AbstractRedstoneGateBlock.POWERED)
					&& blockState.get(AbstractRedstoneGateBlock.FACING) == dir
				? true
				: !bl && connectsTo(world, blockPos.down());
		}
	}

	protected static boolean connectsTo(BlockView world, BlockPos pos) {
		return connectsTo(world.getBlockState(pos));
	}

	protected static boolean connectsTo(BlockState state) {
		return connectsTo(state, null);
	}

	protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
		Block block = state.getBlock();
		if (block == Blocks.REDSTONE_WIRE) {
			return true;
		} else if (state.getBlock() == Blocks.REPEATER) {
			Direction direction = state.get(RepeaterBlock.FACING);
			return direction == dir || direction.getOpposite() == dir;
		} else {
			return Blocks.OBSERVER == state.getBlock() ? dir == state.get(ObserverBlock.FACING) : state.emitsRedstonePower() && dir != null;
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
