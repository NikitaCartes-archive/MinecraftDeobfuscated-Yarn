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
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleParameters;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class RedstoneWireBlock extends Block {
	public static final EnumProperty<WireConnection> field_11440 = Properties.WIRE_CONNECTION_NORTH;
	public static final EnumProperty<WireConnection> field_11436 = Properties.WIRE_CONNECTION_EAST;
	public static final EnumProperty<WireConnection> field_11437 = Properties.WIRE_CONNECTION_SOUTH;
	public static final EnumProperty<WireConnection> field_11439 = Properties.WIRE_CONNECTION_WEST;
	public static final IntegerProperty field_11432 = Properties.POWER;
	public static final Map<Direction, EnumProperty<WireConnection>> field_11435 = Maps.newEnumMap(
		ImmutableMap.of(Direction.NORTH, field_11440, Direction.EAST, field_11436, Direction.SOUTH, field_11437, Direction.WEST, field_11439)
	);
	protected static final VoxelShape[] field_11433 = new VoxelShape[]{
		Block.createCubeShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCubeShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.createCubeShape(0.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.createCubeShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.createCubeShape(3.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.createCubeShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.createCubeShape(3.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.createCubeShape(0.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.createCubeShape(3.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.createCubeShape(3.0, 0.0, 0.0, 16.0, 1.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
	};
	private boolean wiresGivePower = true;
	private final Set<BlockPos> field_11434 = Sets.<BlockPos>newHashSet();

	public RedstoneWireBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_11440, WireConnection.field_12687)
				.with(field_11436, WireConnection.field_12687)
				.with(field_11437, WireConnection.field_12687)
				.with(field_11439, WireConnection.field_12687)
				.with(field_11432, Integer.valueOf(0))
		);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11433[method_10480(blockState)];
	}

	private static int method_10480(BlockState blockState) {
		int i = 0;
		boolean bl = blockState.get(field_11440) != WireConnection.field_12687;
		boolean bl2 = blockState.get(field_11436) != WireConnection.field_12687;
		boolean bl3 = blockState.get(field_11437) != WireConnection.field_12687;
		boolean bl4 = blockState.get(field_11439) != WireConnection.field_12687;
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
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getPos();
		return this.getDefaultState()
			.with(field_11439, this.getRenderConnectionType(blockView, blockPos, Direction.WEST))
			.with(field_11436, this.getRenderConnectionType(blockView, blockPos, Direction.EAST))
			.with(field_11440, this.getRenderConnectionType(blockView, blockPos, Direction.NORTH))
			.with(field_11437, this.getRenderConnectionType(blockView, blockPos, Direction.SOUTH));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.DOWN) {
			return blockState;
		} else {
			return direction == Direction.UP
				? blockState.with(field_11439, this.getRenderConnectionType(iWorld, blockPos, Direction.WEST))
					.with(field_11436, this.getRenderConnectionType(iWorld, blockPos, Direction.EAST))
					.with(field_11440, this.getRenderConnectionType(iWorld, blockPos, Direction.NORTH))
					.with(field_11437, this.getRenderConnectionType(iWorld, blockPos, Direction.SOUTH))
				: blockState.with((Property)field_11435.get(direction), this.getRenderConnectionType(iWorld, blockPos, direction));
		}
	}

	@Override
	public void method_9517(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				WireConnection wireConnection = blockState.get((Property<WireConnection>)field_11435.get(direction));
				if (wireConnection != WireConnection.field_12687 && iWorld.getBlockState(pooledMutable.set(blockPos).setOffset(direction)).getBlock() != this) {
					pooledMutable.setOffset(Direction.DOWN);
					BlockState blockState2 = iWorld.getBlockState(pooledMutable);
					if (blockState2.getBlock() != Blocks.field_10282) {
						BlockPos blockPos2 = pooledMutable.offset(direction.getOpposite());
						BlockState blockState3 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos2), iWorld, pooledMutable, blockPos2);
						replaceBlock(blockState2, blockState3, iWorld, pooledMutable, i);
					}

					pooledMutable.set(blockPos).setOffset(direction).setOffset(Direction.UP);
					BlockState blockState4 = iWorld.getBlockState(pooledMutable);
					if (blockState4.getBlock() != Blocks.field_10282) {
						BlockPos blockPos3 = pooledMutable.offset(direction.getOpposite());
						BlockState blockState5 = blockState4.getStateForNeighborUpdate(direction.getOpposite(), iWorld.getBlockState(blockPos3), iWorld, pooledMutable, blockPos3);
						replaceBlock(blockState4, blockState5, iWorld, pooledMutable, i);
					}
				}
			}
		}
	}

	private WireConnection getRenderConnectionType(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		BlockPos blockPos3 = blockPos.up();
		BlockState blockState2 = blockView.getBlockState(blockPos3);
		if (!blockState2.isSimpleFullBlock(blockView, blockPos3)) {
			boolean bl = blockView.getBlockState(blockPos2).hasSolidTopSurface(blockView, blockPos2)
				|| blockView.getBlockState(blockPos2).getBlock() == Blocks.field_10171;
			if (bl && method_10484(blockView.getBlockState(blockPos2.up()))) {
				if (blockState.method_11603(blockView, blockPos2)) {
					return WireConnection.field_12686;
				}

				return WireConnection.field_12689;
			}
		}

		return !method_10482(blockView.getBlockState(blockPos2), direction)
				&& (blockState.isSimpleFullBlock(blockView, blockPos2) || !method_10484(blockView.getBlockState(blockPos2.down())))
			? WireConnection.field_12687
			: WireConnection.field_12689;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		return blockState2.hasSolidTopSurface(viewableWorld, blockPos2) || blockState2.getBlock() == Blocks.field_10171;
	}

	private BlockState method_10485(World world, BlockPos blockPos, BlockState blockState) {
		blockState = this.method_10481(world, blockPos, blockState);
		List<BlockPos> list = Lists.<BlockPos>newArrayList(this.field_11434);
		this.field_11434.clear();

		for (BlockPos blockPos2 : list) {
			world.updateNeighborsAlways(blockPos2, this);
		}

		return blockState;
	}

	private BlockState method_10481(World world, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = blockState;
		int i = (Integer)blockState.get(field_11432);
		this.wiresGivePower = false;
		int j = world.getReceivedRedstonePower(blockPos);
		this.wiresGivePower = true;
		int k = 0;
		if (j < 15) {
			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState3 = world.getBlockState(blockPos2);
				k = this.method_10486(k, blockState3);
				BlockPos blockPos3 = blockPos.up();
				if (blockState3.isSimpleFullBlock(world, blockPos2) && !world.getBlockState(blockPos3).isSimpleFullBlock(world, blockPos3)) {
					k = this.method_10486(k, world.getBlockState(blockPos2.up()));
				} else if (!blockState3.isSimpleFullBlock(world, blockPos2)) {
					k = this.method_10486(k, world.getBlockState(blockPos2.down()));
				}
			}
		}

		int l = k - 1;
		if (j > l) {
			l = j;
		}

		if (i != l) {
			blockState = blockState.with(field_11432, Integer.valueOf(l));
			if (world.getBlockState(blockPos) == blockState2) {
				world.setBlockState(blockPos, blockState, 2);
			}

			this.field_11434.add(blockPos);

			for (Direction direction2 : Direction.values()) {
				this.field_11434.add(blockPos.offset(direction2));
			}
		}

		return blockState;
	}

	private void method_10479(World world, BlockPos blockPos) {
		if (world.getBlockState(blockPos).getBlock() == this) {
			world.updateNeighborsAlways(blockPos, this);

			for (Direction direction : Direction.values()) {
				world.updateNeighborsAlways(blockPos.offset(direction), this);
			}
		}
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock() && !world.isClient) {
			this.method_10485(world, blockPos, blockState);

			for (Direction direction : Direction.class_2353.VERTICAL) {
				world.updateNeighborsAlways(blockPos.offset(direction), this);
			}

			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				this.method_10479(world, blockPos.offset(direction));
			}

			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				BlockPos blockPos2 = blockPos.offset(direction);
				if (world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
					this.method_10479(world, blockPos2.up());
				} else {
					this.method_10479(world, blockPos2.down());
				}
			}
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.updateNeighborsAlways(blockPos.offset(direction), this);
				}

				this.method_10485(world, blockPos, blockState);

				for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
					this.method_10479(world, blockPos.offset(direction2));
				}

				for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
					BlockPos blockPos2 = blockPos.offset(direction2);
					if (world.getBlockState(blockPos2).isSimpleFullBlock(world, blockPos2)) {
						this.method_10479(world, blockPos2.up());
					} else {
						this.method_10479(world, blockPos2.down());
					}
				}
			}
		}
	}

	private int method_10486(int i, BlockState blockState) {
		if (blockState.getBlock() != this) {
			return i;
		} else {
			int j = (Integer)blockState.get(field_11432);
			return j > i ? j : i;
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			if (blockState.canPlaceAt(world, blockPos)) {
				this.method_10485(world, blockPos, blockState);
			} else {
				dropStacks(blockState, world, blockPos);
				world.clearBlockState(blockPos);
			}
		}
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return !this.wiresGivePower ? 0 : blockState.getWeakRedstonePower(blockView, blockPos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!this.wiresGivePower) {
			return 0;
		} else {
			int i = (Integer)blockState.get(field_11432);
			if (i == 0) {
				return 0;
			} else if (direction == Direction.UP) {
				return i;
			} else {
				EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);

				for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
					if (this.method_10478(blockView, blockPos, direction2)) {
						enumSet.add(direction2);
					}
				}

				if (direction.getAxis().isHorizontal() && enumSet.isEmpty()) {
					return i;
				} else {
					return enumSet.contains(direction) && !enumSet.contains(direction.rotateYCounterclockwise()) && !enumSet.contains(direction.rotateYClockwise()) ? i : 0;
				}
			}
		}
	}

	private boolean method_10478(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.offset(direction);
		BlockState blockState = blockView.getBlockState(blockPos2);
		boolean bl = blockState.isSimpleFullBlock(blockView, blockPos2);
		BlockPos blockPos3 = blockPos.up();
		boolean bl2 = blockView.getBlockState(blockPos3).isSimpleFullBlock(blockView, blockPos3);
		if (!bl2 && bl && method_10483(blockView, blockPos2.up())) {
			return true;
		} else if (method_10482(blockState, direction)) {
			return true;
		} else {
			return blockState.getBlock() == Blocks.field_10450
					&& blockState.get(AbstractRedstoneGateBlock.field_10911)
					&& blockState.get(AbstractRedstoneGateBlock.field_11177) == direction
				? true
				: !bl && method_10483(blockView, blockPos2.down());
		}
	}

	protected static boolean method_10483(BlockView blockView, BlockPos blockPos) {
		return method_10484(blockView.getBlockState(blockPos));
	}

	protected static boolean method_10484(BlockState blockState) {
		return method_10482(blockState, null);
	}

	protected static boolean method_10482(BlockState blockState, @Nullable Direction direction) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10091) {
			return true;
		} else if (blockState.getBlock() == Blocks.field_10450) {
			Direction direction2 = blockState.get(RepeaterBlock.field_11177);
			return direction2 == direction || direction2.getOpposite() == direction;
		} else {
			return Blocks.field_10282 == blockState.getBlock()
				? direction == blockState.get(ObserverBlock.field_10927)
				: blockState.emitsRedstonePower() && direction != null;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return this.wiresGivePower;
	}

	@Environment(EnvType.CLIENT)
	public static int getWireColor(int i) {
		float f = (float)i / 15.0F;
		float g = f * 0.6F + 0.4F;
		if (i == 0) {
			g = 0.3F;
		}

		float h = f * f * 0.7F - 0.5F;
		float j = f * f * 0.6F - 0.7F;
		if (h < 0.0F) {
			h = 0.0F;
		}

		if (j < 0.0F) {
			j = 0.0F;
		}

		int k = MathHelper.clamp((int)(g * 255.0F), 0, 255);
		int l = MathHelper.clamp((int)(h * 255.0F), 0, 255);
		int m = MathHelper.clamp((int)(j * 255.0F), 0, 255);
		return 0xFF000000 | k << 16 | l << 8 | m;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = (Integer)blockState.get(field_11432);
		if (i != 0) {
			double d = (double)blockPos.getX() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			double e = (double)((float)blockPos.getY() + 0.0625F);
			double f = (double)blockPos.getZ() + 0.5 + ((double)random.nextFloat() - 0.5) * 0.2;
			float g = (float)i / 15.0F;
			float h = g * 0.6F + 0.4F;
			float j = Math.max(0.0F, g * g * 0.7F - 0.5F);
			float k = Math.max(0.0F, g * g * 0.6F - 0.7F);
			world.method_8406(new DustParticleParameters(h, j, k, 1.0F), d, e, f, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				return blockState.with(field_11440, blockState.get(field_11437))
					.with(field_11436, blockState.get(field_11439))
					.with(field_11437, blockState.get(field_11440))
					.with(field_11439, blockState.get(field_11436));
			case ROT_270:
				return blockState.with(field_11440, blockState.get(field_11436))
					.with(field_11436, blockState.get(field_11437))
					.with(field_11437, blockState.get(field_11439))
					.with(field_11439, blockState.get(field_11440));
			case ROT_90:
				return blockState.with(field_11440, blockState.get(field_11439))
					.with(field_11436, blockState.get(field_11440))
					.with(field_11437, blockState.get(field_11436))
					.with(field_11439, blockState.get(field_11437));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return blockState.with(field_11440, blockState.get(field_11437)).with(field_11437, blockState.get(field_11440));
			case FRONT_BACK:
				return blockState.with(field_11436, blockState.get(field_11439)).with(field_11439, blockState.get(field_11436));
			default:
				return super.applyMirror(blockState, mirror);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11440, field_11436, field_11437, field_11439, field_11432);
	}
}
