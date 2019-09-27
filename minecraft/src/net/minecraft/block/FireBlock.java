package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4538;
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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;

public class FireBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	public static final BooleanProperty NORTH = ConnectedPlantBlock.NORTH;
	public static final BooleanProperty EAST = ConnectedPlantBlock.EAST;
	public static final BooleanProperty SOUTH = ConnectedPlantBlock.SOUTH;
	public static final BooleanProperty WEST = ConnectedPlantBlock.WEST;
	public static final BooleanProperty UP = ConnectedPlantBlock.UP;
	private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = (Map<Direction, BooleanProperty>)ConnectedPlantBlock.FACING_PROPERTIES
		.entrySet()
		.stream()
		.filter(entry -> entry.getKey() != Direction.DOWN)
		.collect(SystemUtil.toMap());
	private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap<>();
	private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap<>();

	protected FireBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
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
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.empty();
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return this.canPlaceAt(blockState, iWorld, blockPos)
			? this.getStateForPosition(iWorld, blockPos).with(AGE, blockState.get(AGE))
			: Blocks.AIR.getDefaultState();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getStateForPosition(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos());
	}

	public BlockState getStateForPosition(BlockView blockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10074();
		BlockState blockState = blockView.getBlockState(blockPos2);
		if (!this.isFlammable(blockState) && !blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP)) {
			BlockState blockState2 = this.getDefaultState();

			for (Direction direction : Direction.values()) {
				BooleanProperty booleanProperty = (BooleanProperty)DIRECTION_PROPERTIES.get(direction);
				if (booleanProperty != null) {
					blockState2 = blockState2.with(booleanProperty, Boolean.valueOf(this.isFlammable(blockView.getBlockState(blockPos.offset(direction)))));
				}
			}

			return blockState2;
		} else {
			return this.getDefaultState();
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, class_4538 arg, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.method_10074();
		return arg.getBlockState(blockPos2).isSideSolidFullSquare(arg, blockPos2, Direction.UP) || this.areBlocksAroundFlammable(arg, blockPos);
	}

	@Override
	public int getTickRate(class_4538 arg) {
		return 30;
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (serverWorld.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
			if (!blockState.canPlaceAt(serverWorld, blockPos)) {
				serverWorld.removeBlock(blockPos, false);
			}

			Block block = serverWorld.getBlockState(blockPos.method_10074()).getBlock();
			boolean bl = serverWorld.dimension instanceof TheEndDimension && block == Blocks.BEDROCK || block == Blocks.NETHERRACK || block == Blocks.MAGMA_BLOCK;
			int i = (Integer)blockState.get(AGE);
			if (!bl && serverWorld.isRaining() && this.isRainingAround(serverWorld, blockPos) && random.nextFloat() < 0.2F + (float)i * 0.03F) {
				serverWorld.removeBlock(blockPos, false);
			} else {
				int j = Math.min(15, i + random.nextInt(3) / 2);
				if (i != j) {
					blockState = blockState.with(AGE, Integer.valueOf(j));
					serverWorld.setBlockState(blockPos, blockState, 4);
				}

				if (!bl) {
					serverWorld.method_14196().schedule(blockPos, this, this.getTickRate(serverWorld) + random.nextInt(10));
					if (!this.areBlocksAroundFlammable(serverWorld, blockPos)) {
						BlockPos blockPos2 = blockPos.method_10074();
						if (!serverWorld.getBlockState(blockPos2).isSideSolidFullSquare(serverWorld, blockPos2, Direction.UP) || i > 3) {
							serverWorld.removeBlock(blockPos, false);
						}

						return;
					}

					if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(serverWorld.getBlockState(blockPos.method_10074()))) {
						serverWorld.removeBlock(blockPos, false);
						return;
					}
				}

				boolean bl2 = serverWorld.hasHighHumidity(blockPos);
				int k = bl2 ? -50 : 0;
				this.trySpreadingFire(serverWorld, blockPos.east(), 300 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.west(), 300 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.method_10074(), 250 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.up(), 250 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.north(), 300 + k, random, i);
				this.trySpreadingFire(serverWorld, blockPos.south(), 300 + k, random, i);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = -1; l <= 1; l++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 4; n++) {
							if (l != 0 || n != 0 || m != 0) {
								int o = 100;
								if (n > 1) {
									o += (n - 1) * 100;
								}

								mutable.set(blockPos).setOffset(l, n, m);
								int p = this.getBurnChance(serverWorld, mutable);
								if (p > 0) {
									int q = (p + 40 + serverWorld.getDifficulty().getId() * 7) / (i + 30);
									if (bl2) {
										q /= 2;
									}

									if (q > 0 && random.nextInt(o) <= q && (!serverWorld.isRaining() || !this.isRainingAround(serverWorld, mutable))) {
										int r = Math.min(15, i + random.nextInt(5) / 4);
										serverWorld.setBlockState(mutable, this.getStateForPosition(serverWorld, mutable).with(AGE, Integer.valueOf(r)), 3);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isRainingAround(World world, BlockPos blockPos) {
		return world.hasRain(blockPos)
			|| world.hasRain(blockPos.west())
			|| world.hasRain(blockPos.east())
			|| world.hasRain(blockPos.north())
			|| world.hasRain(blockPos.south());
	}

	private int getSpreadChance(BlockState blockState) {
		return blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? 0 : this.spreadChances.getInt(blockState.getBlock());
	}

	private int getBurnChance(BlockState blockState) {
		return blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED) ? 0 : this.burnChances.getInt(blockState.getBlock());
	}

	private void trySpreadingFire(World world, BlockPos blockPos, int i, Random random, int j) {
		int k = this.getSpreadChance(world.getBlockState(blockPos));
		if (random.nextInt(i) < k) {
			BlockState blockState = world.getBlockState(blockPos);
			if (random.nextInt(j + 10) < 5 && !world.hasRain(blockPos)) {
				int l = Math.min(j + random.nextInt(5) / 4, 15);
				world.setBlockState(blockPos, this.getStateForPosition(world, blockPos).with(AGE, Integer.valueOf(l)), 3);
			} else {
				world.removeBlock(blockPos, false);
			}

			Block block = blockState.getBlock();
			if (block instanceof TntBlock) {
				TntBlock.primeTnt(world, blockPos);
			}
		}
	}

	private boolean areBlocksAroundFlammable(BlockView blockView, BlockPos blockPos) {
		for (Direction direction : Direction.values()) {
			if (this.isFlammable(blockView.getBlockState(blockPos.offset(direction)))) {
				return true;
			}
		}

		return false;
	}

	private int getBurnChance(class_4538 arg, BlockPos blockPos) {
		if (!arg.isAir(blockPos)) {
			return 0;
		} else {
			int i = 0;

			for (Direction direction : Direction.values()) {
				BlockState blockState = arg.getBlockState(blockPos.offset(direction));
				i = Math.max(this.getBurnChance(blockState), i);
			}

			return i;
		}
	}

	public boolean isFlammable(BlockState blockState) {
		return this.getBurnChance(blockState) > 0;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			if (world.dimension.getType() != DimensionType.OVERWORLD && world.dimension.getType() != DimensionType.THE_NETHER
				|| !((PortalBlock)Blocks.NETHER_PORTAL).createPortalAt(world, blockPos)) {
				if (!blockState.canPlaceAt(world, blockPos)) {
					world.removeBlock(blockPos, false);
				} else {
					world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world) + world.random.nextInt(10));
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(24) == 0) {
			world.playSound(
				(double)((float)blockPos.getX() + 0.5F),
				(double)((float)blockPos.getY() + 0.5F),
				(double)((float)blockPos.getZ() + 0.5F),
				SoundEvents.BLOCK_FIRE_AMBIENT,
				SoundCategory.BLOCKS,
				1.0F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}

		BlockPos blockPos2 = blockPos.method_10074();
		BlockState blockState2 = world.getBlockState(blockPos2);
		if (!this.isFlammable(blockState2) && !blockState2.isSideSolidFullSquare(world, blockPos2, Direction.UP)) {
			if (this.isFlammable(world.getBlockState(blockPos.west()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble() * 0.1F;
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)blockPos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.east()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)(blockPos.getX() + 1) - random.nextDouble() * 0.1F;
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)blockPos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.north()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble();
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)blockPos.getZ() + random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.south()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble();
					double e = (double)blockPos.getY() + random.nextDouble();
					double f = (double)(blockPos.getZ() + 1) - random.nextDouble() * 0.1F;
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}

			if (this.isFlammable(world.getBlockState(blockPos.up()))) {
				for (int i = 0; i < 2; i++) {
					double d = (double)blockPos.getX() + random.nextDouble();
					double e = (double)(blockPos.getY() + 1) - random.nextDouble() * 0.1F;
					double f = (double)blockPos.getZ() + random.nextDouble();
					world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
				}
			}
		} else {
			for (int i = 0; i < 3; i++) {
				double d = (double)blockPos.getX() + random.nextDouble();
				double e = (double)blockPos.getY() + random.nextDouble() * 0.5 + 0.5;
				double f = (double)blockPos.getZ() + random.nextDouble();
				world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
	}

	public void registerFlammableBlock(Block block, int i, int j) {
		this.burnChances.put(block, i);
		this.spreadChances.put(block, j);
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
		fireBlock.registerFlammableBlock(Blocks.BEE_HIVE, 5, 20);
		fireBlock.registerFlammableBlock(Blocks.BEE_NEST, 30, 20);
	}
}
