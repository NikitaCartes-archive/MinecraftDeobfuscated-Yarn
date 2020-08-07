package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FireBlock extends AbstractFireBlock {
	public static final IntProperty AGE = Properties.AGE_15;
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.field_11033)
		.collect(Util.toMap());
	private static final VoxelShape field_26653 = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape field_26654 = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
	private static final VoxelShape field_26655 = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape field_26656 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
	private static final VoxelShape field_26657 = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
	private final Map<BlockState, VoxelShape> field_26658;
	private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap<>();
	private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap<>();

	public FireBlock(AbstractBlock.Settings settings) {
		super(settings, 1.0F);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(AGE, Integer.valueOf(0))
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(UP, Boolean.valueOf(false))
		);
		this.field_26658 = ImmutableMap.copyOf(
			(Map<? extends BlockState, ? extends VoxelShape>)this.stateManager
				.getStates()
				.stream()
				.filter(blockState -> (Integer)blockState.get(AGE) == 0)
				.collect(Collectors.toMap(Function.identity(), FireBlock::method_31016))
		);
	}

	private static VoxelShape method_31016(BlockState blockState) {
		VoxelShape voxelShape = VoxelShapes.empty();
		if ((Boolean)blockState.get(UP)) {
			voxelShape = field_26653;
		}

		if ((Boolean)blockState.get(NORTH)) {
			voxelShape = VoxelShapes.union(voxelShape, field_26656);
		}

		if ((Boolean)blockState.get(SOUTH)) {
			voxelShape = VoxelShapes.union(voxelShape, field_26657);
		}

		if ((Boolean)blockState.get(EAST)) {
			voxelShape = VoxelShapes.union(voxelShape, field_26655);
		}

		if ((Boolean)blockState.get(WEST)) {
			voxelShape = VoxelShapes.union(voxelShape, field_26654);
		}

		return voxelShape.isEmpty() ? BASE_SHAPE : voxelShape;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		return this.canPlaceAt(state, world, pos) ? this.method_24855(world, pos, (Integer)state.get(AGE)) : Blocks.field_10124.getDefaultState();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.field_26658.get(state.with(AGE, Integer.valueOf(0)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getStateForPosition(ctx.getWorld(), ctx.getBlockPos());
	}

	protected BlockState getStateForPosition(BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.method_10074();
		BlockState blockState = world.getBlockState(blockPos);
		if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.field_11036)) {
			BlockState blockState2 = this.getDefaultState();

			for (Direction direction : Direction.values()) {
				BooleanProperty booleanProperty = (BooleanProperty)DIRECTION_PROPERTIES.get(direction);
				if (booleanProperty != null) {
					blockState2 = blockState2.with(booleanProperty, Boolean.valueOf(this.isFlammable(world.getBlockState(pos.offset(direction)))));
				}
			}

			return blockState2;
		} else {
			return this.getDefaultState();
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.method_10074();
		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.field_11036) || this.areBlocksAroundFlammable(world, pos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.method_14196().schedule(pos, this, method_26155(world.random));
		if (world.getGameRules().getBoolean(GameRules.field_19387)) {
			if (!state.canPlaceAt(world, pos)) {
				world.removeBlock(pos, false);
			}

			BlockState blockState = world.getBlockState(pos.method_10074());
			boolean bl = blockState.isIn(world.getDimension().getInfiniburnBlocks());
			int i = (Integer)state.get(AGE);
			if (!bl && world.isRaining() && this.isRainingAround(world, pos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
				world.removeBlock(pos, false);
			} else {
				int j = Math.min(15, i + random.nextInt(3) / 2);
				if (i != j) {
					state = state.with(AGE, Integer.valueOf(j));
					world.setBlockState(pos, state, 4);
				}

				if (!bl) {
					if (!this.areBlocksAroundFlammable(world, pos)) {
						BlockPos blockPos = pos.method_10074();
						if (!world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.field_11036) || i > 3) {
							world.removeBlock(pos, false);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(world.getBlockState(pos.method_10074()))) {
						world.removeBlock(pos, false);
						return;
					}
				}

				boolean bl2 = world.hasHighHumidity(pos);
				int k = bl2 ? -50 : 0;
				this.trySpreadingFire(world, pos.east(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.west(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.method_10074(), 250 + k, random, i);
				this.trySpreadingFire(world, pos.up(), 250 + k, random, i);
				this.trySpreadingFire(world, pos.north(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.south(), 300 + k, random, i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 4; n++) {
							if (l != 0 || n != 0 || m != 0) {
								int o = 100;
								if (n > 1) {
									o += (n - 1) * 100;
								}

								mutable.set(pos, l, n, m);
								int p = this.getBurnChance(world, mutable);
								if (p > 0) {
									int q = (p + 40 + world.getDifficulty().getId() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.isRainingAround(world, mutable))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										world.setBlockState(mutable, this.method_24855(world, mutable, r), 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isRainingAround(World world, BlockPos pos) {
		return world.hasRain(pos) || world.hasRain(pos.west()) || world.hasRain(pos.east()) || world.hasRain(pos.north()) || world.hasRain(pos.south());
	}

	private int getSpreadChance(BlockState state) {
		return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? 0 : this.spreadChances.getInt(state.getBlock());
	}

	private int getBurnChance(BlockState state) {
		return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? 0 : this.burnChances.getInt(state.getBlock());
	}

	private void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge) {
		int i = this.getSpreadChance(world.getBlockState(pos));
		if (rand.nextInt(spreadFactor) < i) {
			BlockState blockState = world.getBlockState(pos);
			if (rand.nextInt(currentAge + 10) < 5 && !world.hasRain(pos)) {
				int j = Math.min(currentAge + rand.nextInt(5) / 4, 15);
				world.setBlockState(pos, this.method_24855(world, pos, j), 3);
			} else {
				world.removeBlock(pos, false);
			}

			Block block = blockState.getBlock();
			if (block instanceof TntBlock) {
				TntBlock.primeTnt(world, pos);
			}
		}
	}

	private BlockState method_24855(WorldAccess worldAccess, BlockPos blockPos, int i) {
		BlockState blockState = getState(worldAccess, blockPos);
		return blockState.isOf(Blocks.field_10036) ? blockState.with(AGE, Integer.valueOf(i)) : blockState;
	}

	private boolean areBlocksAroundFlammable(BlockView world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.isFlammable(world.getBlockState(pos.offset(direction)))) {
				return true;
			}
		}

		return false;
	}

	private int getBurnChance(WorldView worldView, BlockPos pos) {
		if (!worldView.isAir(pos)) {
			return 0;
		} else {
			int i = 0;

			for (Direction direction : Direction.values()) {
				BlockState blockState = worldView.getBlockState(pos.offset(direction));
				i = Math.max(this.getBurnChance(blockState), i);
			}

			return i;
		}
	}

	@Override
	protected boolean isFlammable(BlockState state) {
		return this.getBurnChance(state) > 0;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.getBlockTickScheduler().schedule(pos, this, method_26155(world.random));
	}

	private static int method_26155(Random random) {
		return 30 + random.nextInt(10);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
	}

	private void registerFlammableBlock(Block block, int burnChance, int spreadChance) {
		this.burnChances.put(block, burnChance);
		this.spreadChances.put(block, spreadChance);
	}

	public static void registerDefaultFlammables() {
		FireBlock fireBlock = (FireBlock)Blocks.field_10036;
		fireBlock.registerFlammableBlock(Blocks.field_10161, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_9975, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10148, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10334, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10218, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10075, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10119, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10071, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10257, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10617, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10031, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10500, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10188, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10291, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10513, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10041, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10196, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10457, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10620, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10020, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10299, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10319, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10132, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10144, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10563, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10408, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10569, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10122, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10256, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10616, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10431, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10037, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10511, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10306, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10533, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10010, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10519, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10436, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10366, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10254, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10622, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10244, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10250, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10558, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10204, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10084, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10103, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10374, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10126, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10155, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10307, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10303, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_9999, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10178, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10503, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_9988, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10539, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10335, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10098, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10035, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10504, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10375, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10479, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10112, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10428, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10583, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10378, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10430, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10003, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10214, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10313, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10182, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10449, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10086, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10226, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10573, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10270, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10048, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10156, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10315, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10554, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_9995, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10548, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10606, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10446, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10095, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10215, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10294, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10490, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10028, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10459, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10423, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10222, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10619, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10259, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10514, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10113, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10170, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10314, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10146, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10597, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.field_10381, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.field_10359, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_22422, 15, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10466, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_9977, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10482, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10290, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10512, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10040, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10393, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10591, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10209, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10433, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10510, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10043, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10473, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10338, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10536, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10106, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.field_10342, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.field_10211, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.field_16492, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.field_16330, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.field_17563, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_16999, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.field_20422, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.field_20421, 30, 20);
	}
}
