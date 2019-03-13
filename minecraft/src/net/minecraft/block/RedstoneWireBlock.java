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
import net.minecraft.entity.VerticalEntityPosition;
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
	public static final EnumProperty<WireConnection> field_11440 = Properties.field_12495;
	public static final EnumProperty<WireConnection> field_11436 = Properties.field_12523;
	public static final EnumProperty<WireConnection> field_11437 = Properties.field_12551;
	public static final EnumProperty<WireConnection> field_11439 = Properties.field_12504;
	public static final IntegerProperty field_11432 = Properties.field_12511;
	public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(
		ImmutableMap.of(Direction.NORTH, field_11440, Direction.EAST, field_11436, Direction.SOUTH, field_11437, Direction.WEST, field_11439)
	);
	protected static final VoxelShape[] field_11433 = new VoxelShape[]{
		Block.method_9541(3.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.method_9541(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.method_9541(0.0, 0.0, 3.0, 13.0, 1.0, 13.0),
		Block.method_9541(0.0, 0.0, 3.0, 13.0, 1.0, 16.0),
		Block.method_9541(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.method_9541(3.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 13.0, 1.0, 13.0),
		Block.method_9541(0.0, 0.0, 0.0, 13.0, 1.0, 16.0),
		Block.method_9541(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.method_9541(3.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.method_9541(0.0, 0.0, 3.0, 16.0, 1.0, 13.0),
		Block.method_9541(0.0, 0.0, 3.0, 16.0, 1.0, 16.0),
		Block.method_9541(3.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.method_9541(3.0, 0.0, 0.0, 16.0, 1.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 1.0, 13.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
	};
	private boolean wiresGivePower = true;
	private final Set<BlockPos> field_11434 = Sets.<BlockPos>newHashSet();

	public RedstoneWireBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11440, WireConnection.field_12687)
				.method_11657(field_11436, WireConnection.field_12687)
				.method_11657(field_11437, WireConnection.field_12687)
				.method_11657(field_11439, WireConnection.field_12687)
				.method_11657(field_11432, Integer.valueOf(0))
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11433[method_10480(blockState)];
	}

	private static int method_10480(BlockState blockState) {
		int i = 0;
		boolean bl = blockState.method_11654(field_11440) != WireConnection.field_12687;
		boolean bl2 = blockState.method_11654(field_11436) != WireConnection.field_12687;
		boolean bl3 = blockState.method_11654(field_11437) != WireConnection.field_12687;
		boolean bl4 = blockState.method_11654(field_11439) != WireConnection.field_12687;
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
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.method_8037();
		return this.method_9564()
			.method_11657(field_11439, this.method_10477(blockView, blockPos, Direction.WEST))
			.method_11657(field_11436, this.method_10477(blockView, blockPos, Direction.EAST))
			.method_11657(field_11440, this.method_10477(blockView, blockPos, Direction.NORTH))
			.method_11657(field_11437, this.method_10477(blockView, blockPos, Direction.SOUTH));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.DOWN) {
			return blockState;
		} else {
			return direction == Direction.UP
				? blockState.method_11657(field_11439, this.method_10477(iWorld, blockPos, Direction.WEST))
					.method_11657(field_11436, this.method_10477(iWorld, blockPos, Direction.EAST))
					.method_11657(field_11440, this.method_10477(iWorld, blockPos, Direction.NORTH))
					.method_11657(field_11437, this.method_10477(iWorld, blockPos, Direction.SOUTH))
				: blockState.method_11657((Property)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), this.method_10477(iWorld, blockPos, direction));
		}
	}

	@Override
	public void method_9517(BlockState blockState, IWorld iWorld, BlockPos blockPos, int i) {
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				WireConnection wireConnection = blockState.method_11654((Property<WireConnection>)DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
				if (wireConnection != WireConnection.field_12687 && iWorld.method_8320(pooledMutable.method_10114(blockPos).method_10118(direction)).getBlock() != this) {
					pooledMutable.method_10118(Direction.DOWN);
					BlockState blockState2 = iWorld.method_8320(pooledMutable);
					if (blockState2.getBlock() != Blocks.field_10282) {
						BlockPos blockPos2 = pooledMutable.method_10093(direction.getOpposite());
						BlockState blockState3 = blockState2.method_11578(direction.getOpposite(), iWorld.method_8320(blockPos2), iWorld, pooledMutable, blockPos2);
						method_9611(blockState2, blockState3, iWorld, pooledMutable, i);
					}

					pooledMutable.method_10114(blockPos).method_10118(direction).method_10118(Direction.UP);
					BlockState blockState4 = iWorld.method_8320(pooledMutable);
					if (blockState4.getBlock() != Blocks.field_10282) {
						BlockPos blockPos3 = pooledMutable.method_10093(direction.getOpposite());
						BlockState blockState5 = blockState4.method_11578(direction.getOpposite(), iWorld.method_8320(blockPos3), iWorld, pooledMutable, blockPos3);
						method_9611(blockState4, blockState5, iWorld, pooledMutable, i);
					}
				}
			}
		}
	}

	private WireConnection method_10477(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState = blockView.method_8320(blockPos2);
		BlockPos blockPos3 = blockPos.up();
		BlockState blockState2 = blockView.method_8320(blockPos3);
		if (!blockState2.method_11621(blockView, blockPos3)) {
			boolean bl = blockView.method_8320(blockPos2).method_11631(blockView, blockPos2) || blockView.method_8320(blockPos2).getBlock() == Blocks.field_10171;
			if (bl && method_10484(blockView.method_8320(blockPos2.up()))) {
				if (blockState.method_11603(blockView, blockPos2)) {
					return WireConnection.field_12686;
				}

				return WireConnection.field_12689;
			}
		}

		return !method_10482(blockView.method_8320(blockPos2), direction)
				&& (blockState.method_11621(blockView, blockPos2) || !method_10484(blockView.method_8320(blockPos2.down())))
			? WireConnection.field_12687
			: WireConnection.field_12689;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		return blockState2.method_11631(viewableWorld, blockPos2) || blockState2.getBlock() == Blocks.field_10171;
	}

	private BlockState method_10485(World world, BlockPos blockPos, BlockState blockState) {
		blockState = this.method_10481(world, blockPos, blockState);
		List<BlockPos> list = Lists.<BlockPos>newArrayList(this.field_11434);
		this.field_11434.clear();

		for (BlockPos blockPos2 : list) {
			world.method_8452(blockPos2, this);
		}

		return blockState;
	}

	private BlockState method_10481(World world, BlockPos blockPos, BlockState blockState) {
		BlockState blockState2 = blockState;
		int i = (Integer)blockState.method_11654(field_11432);
		this.wiresGivePower = false;
		int j = world.method_8482(blockPos);
		this.wiresGivePower = true;
		int k = 0;
		if (j < 15) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				BlockPos blockPos2 = blockPos.method_10093(direction);
				BlockState blockState3 = world.method_8320(blockPos2);
				k = this.method_10486(k, blockState3);
				BlockPos blockPos3 = blockPos.up();
				if (blockState3.method_11621(world, blockPos2) && !world.method_8320(blockPos3).method_11621(world, blockPos3)) {
					k = this.method_10486(k, world.method_8320(blockPos2.up()));
				} else if (!blockState3.method_11621(world, blockPos2)) {
					k = this.method_10486(k, world.method_8320(blockPos2.down()));
				}
			}
		}

		int l = k - 1;
		if (j > l) {
			l = j;
		}

		if (i != l) {
			blockState = blockState.method_11657(field_11432, Integer.valueOf(l));
			if (world.method_8320(blockPos) == blockState2) {
				world.method_8652(blockPos, blockState, 2);
			}

			this.field_11434.add(blockPos);

			for (Direction direction2 : Direction.values()) {
				this.field_11434.add(blockPos.method_10093(direction2));
			}
		}

		return blockState;
	}

	private void method_10479(World world, BlockPos blockPos) {
		if (world.method_8320(blockPos).getBlock() == this) {
			world.method_8452(blockPos, this);

			for (Direction direction : Direction.values()) {
				world.method_8452(blockPos.method_10093(direction), this);
			}
		}
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock() && !world.isClient) {
			this.method_10485(world, blockPos, blockState);

			for (Direction direction : Direction.Type.VERTICAL) {
				world.method_8452(blockPos.method_10093(direction), this);
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				this.method_10479(world, blockPos.method_10093(direction));
			}

			for (Direction direction : Direction.Type.HORIZONTAL) {
				BlockPos blockPos2 = blockPos.method_10093(direction);
				if (world.method_8320(blockPos2).method_11621(world, blockPos2)) {
					this.method_10479(world, blockPos2.up());
				} else {
					this.method_10479(world, blockPos2.down());
				}
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			super.method_9536(blockState, world, blockPos, blockState2, bl);
			if (!world.isClient) {
				for (Direction direction : Direction.values()) {
					world.method_8452(blockPos.method_10093(direction), this);
				}

				this.method_10485(world, blockPos, blockState);

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					this.method_10479(world, blockPos.method_10093(direction2));
				}

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					BlockPos blockPos2 = blockPos.method_10093(direction2);
					if (world.method_8320(blockPos2).method_11621(world, blockPos2)) {
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
			int j = (Integer)blockState.method_11654(field_11432);
			return j > i ? j : i;
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			if (blockState.method_11591(world, blockPos)) {
				this.method_10485(world, blockPos, blockState);
			} else {
				method_9497(blockState, world, blockPos);
				world.method_8650(blockPos);
			}
		}
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return !this.wiresGivePower ? 0 : blockState.method_11597(blockView, blockPos, direction);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!this.wiresGivePower) {
			return 0;
		} else {
			int i = (Integer)blockState.method_11654(field_11432);
			if (i == 0) {
				return 0;
			} else if (direction == Direction.UP) {
				return i;
			} else {
				EnumSet<Direction> enumSet = EnumSet.noneOf(Direction.class);

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
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
		BlockPos blockPos2 = blockPos.method_10093(direction);
		BlockState blockState = blockView.method_8320(blockPos2);
		boolean bl = blockState.method_11621(blockView, blockPos2);
		BlockPos blockPos3 = blockPos.up();
		boolean bl2 = blockView.method_8320(blockPos3).method_11621(blockView, blockPos3);
		if (!bl2 && bl && method_10483(blockView, blockPos2.up())) {
			return true;
		} else if (method_10482(blockState, direction)) {
			return true;
		} else {
			return blockState.getBlock() == Blocks.field_10450
					&& blockState.method_11654(AbstractRedstoneGateBlock.field_10911)
					&& blockState.method_11654(AbstractRedstoneGateBlock.field_11177) == direction
				? true
				: !bl && method_10483(blockView, blockPos2.down());
		}
	}

	protected static boolean method_10483(BlockView blockView, BlockPos blockPos) {
		return method_10484(blockView.method_8320(blockPos));
	}

	protected static boolean method_10484(BlockState blockState) {
		return method_10482(blockState, null);
	}

	protected static boolean method_10482(BlockState blockState, @Nullable Direction direction) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10091) {
			return true;
		} else if (blockState.getBlock() == Blocks.field_10450) {
			Direction direction2 = blockState.method_11654(RepeaterBlock.field_11177);
			return direction2 == direction || direction2.getOpposite() == direction;
		} else {
			return Blocks.field_10282 == blockState.getBlock()
				? direction == blockState.method_11654(ObserverBlock.field_10927)
				: blockState.emitsRedstonePower() && direction != null;
		}
	}

	@Override
	public boolean method_9506(BlockState blockState) {
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
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = (Integer)blockState.method_11654(field_11432);
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
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_180:
				return blockState.method_11657(field_11440, blockState.method_11654(field_11437))
					.method_11657(field_11436, blockState.method_11654(field_11439))
					.method_11657(field_11437, blockState.method_11654(field_11440))
					.method_11657(field_11439, blockState.method_11654(field_11436));
			case ROT_270:
				return blockState.method_11657(field_11440, blockState.method_11654(field_11436))
					.method_11657(field_11436, blockState.method_11654(field_11437))
					.method_11657(field_11437, blockState.method_11654(field_11439))
					.method_11657(field_11439, blockState.method_11654(field_11440));
			case ROT_90:
				return blockState.method_11657(field_11440, blockState.method_11654(field_11439))
					.method_11657(field_11436, blockState.method_11654(field_11440))
					.method_11657(field_11437, blockState.method_11654(field_11436))
					.method_11657(field_11439, blockState.method_11654(field_11437));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return blockState.method_11657(field_11440, blockState.method_11654(field_11437)).method_11657(field_11437, blockState.method_11654(field_11440));
			case FRONT_BACK:
				return blockState.method_11657(field_11436, blockState.method_11654(field_11439)).method_11657(field_11439, blockState.method_11654(field_11436));
			default:
				return super.method_9569(blockState, mirror);
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11440, field_11436, field_11437, field_11439, field_11432);
	}
}
