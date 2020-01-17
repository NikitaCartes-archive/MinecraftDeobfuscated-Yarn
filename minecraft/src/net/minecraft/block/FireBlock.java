package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;

public class FireBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
	public static final BooleanProperty EAST = ConnectingBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectingBlock.WEST;
	public static final BooleanProperty UP = ConnectingBlock.UP;
	private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectingBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(Util.toMap());
	private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap<>();
	private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap<>();

	protected FireBlock(Block.Settings settings) {
		super(settings);
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
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return this.canPlaceAt(state, world, pos) ? this.getStateForPosition(world, pos).with(AGE, state.get(AGE)) : Blocks.AIR.getDefaultState();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getStateForPosition(ctx.getWorld(), ctx.getBlockPos());
	}

	public BlockState getStateForPosition(BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
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
		BlockPos blockPos = pos.down();
		return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP) || this.areBlocksAroundFlammable(world, pos);
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return 30;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			if (!state.canPlaceAt(world, pos)) {
				world.removeBlock(pos, false);
			}

			Block block = world.getBlockState(pos.down()).getBlock();
			boolean bl = world.dimension instanceof TheEndDimension && block == Blocks.BEDROCK || block == Blocks.NETHERRACK || block == Blocks.MAGMA_BLOCK;
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
					world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world) + random.nextInt(10));
					if (!this.areBlocksAroundFlammable(world, pos)) {
						BlockPos blockPos = pos.down();
						if (!world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, Direction.UP) || i > 3) {
							world.removeBlock(pos, false);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(world.getBlockState(pos.down()))) {
						world.removeBlock(pos, false);
						return;
					}
				}

				boolean bl2 = world.hasHighHumidity(pos);
				int k = bl2 ? -50 : 0;
				this.trySpreadingFire(world, pos.east(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.west(), 300 + k, random, i);
				this.trySpreadingFire(world, pos.down(), 250 + k, random, i);
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

								mutable.set(pos).setOffset(l, n, m);
								int p = this.getBurnChance(world, mutable);
								if (p > 0) {
									int q = (p + 40 + world.getDifficulty().getId() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!world.isRaining() || !this.isRainingAround(world, mutable))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										world.setBlockState(mutable, this.getStateForPosition(world, mutable).with(AGE, Integer.valueOf(r)), 3);
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
				world.setBlockState(pos, this.getStateForPosition(world, pos).with(AGE, Integer.valueOf(j)), 3);
			} else {
				world.removeBlock(pos, false);
			}

			Block block = blockState.getBlock();
			if (block instanceof TntBlock) {
				TntBlock.primeTnt(world, pos);
			}
		}
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

	public boolean isFlammable(BlockState blockState) {
		return this.getBurnChance(blockState) > 0;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		if (oldState.getBlock() != state.getBlock()) {
			if (world.dimension.getType() != DimensionType.OVERWORLD && world.dimension.getType() != DimensionType.THE_NETHER
				|| !((NetherPortalBlock)Blocks.NETHER_PORTAL).createPortalAt(world, pos)) {
				if (!state.canPlaceAt(world, pos)) {
					world.removeBlock(pos, false);
				} else {
					world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world) + world.random.nextInt(10));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(24) == 0) {
			world.playSound(
				(double)((float)pos.getX() + 0.5F),
				(double)((float)pos.getY() + 0.5F),
				(double)((float)pos.getZ() + 0.5F),
				SoundEvents.BLOCK_FIRE_AMBIENT,
				SoundCategory.BLOCKS,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
			if (this.isFlammable(world.getBlockState(pos.west()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble() * 0.1F;
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.east()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)(pos.getX() + 1) - random.nextDouble() * 0.1F;
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.north()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)pos.getZ() + random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.south()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)pos.getY() + random.nextDouble();
					double f = (double)(pos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(pos.up()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)pos.getX() + random.nextDouble();
					double e = (double)(pos.getY() + 1) - random.nextDouble() * 0.1F;
					double f = (double)pos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)pos.getX() + random.nextDouble();
				double e = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)pos.getZ() + random.nextDouble();
				world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
	}

	public void registerFlammableBlock(Block block, int burnChance, int spreadChance) {
		this.burnChances.put(block, burnChance);
		this.spreadChances.put(block, spreadChance);
	}

	public static void registerDefaultFlammables() {
		FireBlock fireBlock = (FireBlock)Blocks.FIRE;
		fireBlock.registerFlammableBlock(Blocks.OAK_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_PLANKS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_SLAB, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_FENCE_GATE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_FENCE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_STAIRS, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_WOOD, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.OAK_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.SPRUCE_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BIRCH_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.JUNGLE_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.ACACIA_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.DARK_OAK_LEAVES, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BOOKSHELF, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.TNT, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.GRASS, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.FERN, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.DEAD_BUSH, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.SUNFLOWER, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.LILAC, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.ROSE_BUSH, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.PEONY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.TALL_GRASS, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.LARGE_FERN, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.DANDELION, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.POPPY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.BLUE_ORCHID, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.ALLIUM, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.AZURE_BLUET, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.RED_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.ORANGE_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.WHITE_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.PINK_TULIP, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.OXEYE_DAISY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.CORNFLOWER, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.LILY_OF_THE_VALLEY, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.WITHER_ROSE, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.WHITE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.ORANGE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.MAGENTA_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_BLUE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.YELLOW_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.LIME_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.PINK_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.GRAY_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_GRAY_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.CYAN_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.PURPLE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BLUE_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BROWN_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.GREEN_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.RED_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BLACK_WOOL, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.VINE, 15, 100);
		fireBlock.registerFlammableBlock(Blocks.COAL_BLOCK, 5, 5);
		fireBlock.registerFlammableBlock(Blocks.HAY_BLOCK, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.WHITE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.ORANGE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.MAGENTA_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_BLUE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.YELLOW_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.LIME_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.PINK_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.GRAY_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.LIGHT_GRAY_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.CYAN_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.PURPLE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.BLUE_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.BROWN_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.GREEN_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.RED_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.BLACK_CARPET, 60, 20);
		fireBlock.registerFlammableBlock(Blocks.DRIED_KELP_BLOCK, 30, 60);
		fireBlock.registerFlammableBlock(Blocks.BAMBOO, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.SCAFFOLDING, 60, 60);
		fireBlock.registerFlammableBlock(Blocks.LECTERN, 30, 20);
		fireBlock.registerFlammableBlock(Blocks.COMPOSTER, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.SWEET_BERRY_BUSH, 60, 100);
		fireBlock.registerFlammableBlock(Blocks.BEEHIVE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BEE_NEST, 30, 20);
	}
}
