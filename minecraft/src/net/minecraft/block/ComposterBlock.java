package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ComposterBlock extends Block implements InventoryProvider {
	public static final IntProperty LEVEL = Properties.LEVEL_8;
	public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<>();
	private static final VoxelShape RAY_TRACE_SHAPE = VoxelShapes.fullCube();
	private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = Util.make(
		new VoxelShape[9],
		voxelShapes -> {
			for (int i = 0; i < 8; i++) {
				voxelShapes[i] = VoxelShapes.combineAndSimplify(
					RAY_TRACE_SHAPE, Block.createCuboidShape(2.0, (double)Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST
				);
			}

			voxelShapes[8] = voxelShapes[7];
		}
	);

	public static void registerDefaultCompostableItems() {
		ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue(-1.0F);
		float f = 0.3F;
		float g = 0.5F;
		float h = 0.65F;
		float i = 0.85F;
		float j = 1.0F;
		registerCompostableItem(0.3F, Items.JUNGLE_LEAVES);
		registerCompostableItem(0.3F, Items.OAK_LEAVES);
		registerCompostableItem(0.3F, Items.SPRUCE_LEAVES);
		registerCompostableItem(0.3F, Items.DARK_OAK_LEAVES);
		registerCompostableItem(0.3F, Items.ACACIA_LEAVES);
		registerCompostableItem(0.3F, Items.BIRCH_LEAVES);
		registerCompostableItem(0.3F, Items.OAK_SAPLING);
		registerCompostableItem(0.3F, Items.SPRUCE_SAPLING);
		registerCompostableItem(0.3F, Items.BIRCH_SAPLING);
		registerCompostableItem(0.3F, Items.JUNGLE_SAPLING);
		registerCompostableItem(0.3F, Items.ACACIA_SAPLING);
		registerCompostableItem(0.3F, Items.DARK_OAK_SAPLING);
		registerCompostableItem(0.3F, Items.BEETROOT_SEEDS);
		registerCompostableItem(0.3F, Items.DRIED_KELP);
		registerCompostableItem(0.3F, Items.GRASS);
		registerCompostableItem(0.3F, Items.KELP);
		registerCompostableItem(0.3F, Items.MELON_SEEDS);
		registerCompostableItem(0.3F, Items.PUMPKIN_SEEDS);
		registerCompostableItem(0.3F, Items.SEAGRASS);
		registerCompostableItem(0.3F, Items.SWEET_BERRIES);
		registerCompostableItem(0.3F, Items.WHEAT_SEEDS);
		registerCompostableItem(0.5F, Items.DRIED_KELP_BLOCK);
		registerCompostableItem(0.5F, Items.TALL_GRASS);
		registerCompostableItem(0.5F, Items.CACTUS);
		registerCompostableItem(0.5F, Items.SUGAR_CANE);
		registerCompostableItem(0.5F, Items.VINE);
		registerCompostableItem(0.5F, Items.NETHER_SPROUTS);
		registerCompostableItem(0.5F, Items.WEEPING_VINES);
		registerCompostableItem(0.5F, Items.TWISTING_VINES);
		registerCompostableItem(0.5F, Items.MELON_SLICE);
		registerCompostableItem(0.65F, Items.SEA_PICKLE);
		registerCompostableItem(0.65F, Items.LILY_PAD);
		registerCompostableItem(0.65F, Items.PUMPKIN);
		registerCompostableItem(0.65F, Items.CARVED_PUMPKIN);
		registerCompostableItem(0.65F, Items.MELON);
		registerCompostableItem(0.65F, Items.APPLE);
		registerCompostableItem(0.65F, Items.BEETROOT);
		registerCompostableItem(0.65F, Items.CARROT);
		registerCompostableItem(0.65F, Items.COCOA_BEANS);
		registerCompostableItem(0.65F, Items.POTATO);
		registerCompostableItem(0.65F, Items.WHEAT);
		registerCompostableItem(0.65F, Items.BROWN_MUSHROOM);
		registerCompostableItem(0.65F, Items.RED_MUSHROOM);
		registerCompostableItem(0.65F, Items.MUSHROOM_STEM);
		registerCompostableItem(0.65F, Items.CRIMSON_FUNGUS);
		registerCompostableItem(0.65F, Items.WARPED_FUNGUS);
		registerCompostableItem(0.65F, Items.NETHER_WART);
		registerCompostableItem(0.65F, Items.CRIMSON_ROOTS);
		registerCompostableItem(0.65F, Items.WARPED_ROOTS);
		registerCompostableItem(0.65F, Items.SHROOMLIGHT);
		registerCompostableItem(0.65F, Items.DANDELION);
		registerCompostableItem(0.65F, Items.POPPY);
		registerCompostableItem(0.65F, Items.BLUE_ORCHID);
		registerCompostableItem(0.65F, Items.ALLIUM);
		registerCompostableItem(0.65F, Items.AZURE_BLUET);
		registerCompostableItem(0.65F, Items.RED_TULIP);
		registerCompostableItem(0.65F, Items.ORANGE_TULIP);
		registerCompostableItem(0.65F, Items.WHITE_TULIP);
		registerCompostableItem(0.65F, Items.PINK_TULIP);
		registerCompostableItem(0.65F, Items.OXEYE_DAISY);
		registerCompostableItem(0.65F, Items.CORNFLOWER);
		registerCompostableItem(0.65F, Items.LILY_OF_THE_VALLEY);
		registerCompostableItem(0.65F, Items.WITHER_ROSE);
		registerCompostableItem(0.65F, Items.FERN);
		registerCompostableItem(0.65F, Items.SUNFLOWER);
		registerCompostableItem(0.65F, Items.LILAC);
		registerCompostableItem(0.65F, Items.ROSE_BUSH);
		registerCompostableItem(0.65F, Items.PEONY);
		registerCompostableItem(0.65F, Items.LARGE_FERN);
		registerCompostableItem(0.85F, Items.HAY_BLOCK);
		registerCompostableItem(0.85F, Items.BROWN_MUSHROOM_BLOCK);
		registerCompostableItem(0.85F, Items.RED_MUSHROOM_BLOCK);
		registerCompostableItem(0.85F, Items.NETHER_WART_BLOCK);
		registerCompostableItem(0.85F, Items.WARPED_WART_BLOCK);
		registerCompostableItem(0.85F, Items.BREAD);
		registerCompostableItem(0.85F, Items.BAKED_POTATO);
		registerCompostableItem(0.85F, Items.COOKIE);
		registerCompostableItem(1.0F, Items.CAKE);
		registerCompostableItem(1.0F, Items.PUMPKIN_PIE);
	}

	private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
		ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.asItem(), levelIncreaseChance);
	}

	public ComposterBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	public static void playEffects(World world, BlockPos pos, boolean fill) {
		BlockState blockState = world.getBlockState(pos);
		world.playSound(
			(double)pos.getX(),
			(double)pos.getY(),
			(double)pos.getZ(),
			fill ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL,
			SoundCategory.BLOCKS,
			1.0F,
			1.0F,
			false
		);
		double d = blockState.getOutlineShape(world, pos).getEndingCoord(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
		double e = 0.13125F;
		double f = 0.7375F;
		Random random = world.getRandom();

		for (int i = 0; i < 10; i++) {
			double g = random.nextGaussian() * 0.02;
			double h = random.nextGaussian() * 0.02;
			double j = random.nextGaussian() * 0.02;
			world.addParticle(
				ParticleTypes.COMPOSTER,
				(double)pos.getX() + 0.13125F + 0.7375F * (double)random.nextFloat(),
				(double)pos.getY() + d + (double)random.nextFloat() * (1.0 - d),
				(double)pos.getZ() + 0.13125F + 0.7375F * (double)random.nextFloat(),
				g,
				h,
				j
			);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return LEVEL_TO_COLLISION_SHAPE[state.get(LEVEL)];
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
		return RAY_TRACE_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return LEVEL_TO_COLLISION_SHAPE[0];
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if ((Integer)state.get(LEVEL) == 7) {
			world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		int i = (Integer)state.get(LEVEL);
		ItemStack itemStack = player.getStackInHand(hand);
		if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem())) {
			if (i < 7 && !world.isClient) {
				BlockState blockState = addToComposter(state, world, pos, itemStack);
				world.syncWorldEvent(1500, pos, state != blockState ? 1 : 0);
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			}

			return ActionResult.success(world.isClient);
		} else if (i == 8) {
			emptyFullComposter(state, world, pos);
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	public static BlockState compost(BlockState state, ServerWorld world, ItemStack stack, BlockPos pos) {
		int i = (Integer)state.get(LEVEL);
		if (i < 7 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem())) {
			BlockState blockState = addToComposter(state, world, pos, stack);
			stack.decrement(1);
			return blockState;
		} else {
			return state;
		}
	}

	public static BlockState emptyFullComposter(BlockState state, World world, BlockPos pos) {
		if (!world.isClient) {
			float f = 0.7F;
			double d = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
			double e = (double)(world.random.nextFloat() * 0.7F) + 0.060000002F + 0.6;
			double g = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
			ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, new ItemStack(Items.BONE_MEAL));
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
		}

		BlockState blockState = emptyComposter(state, world, pos);
		world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
		return blockState;
	}

	private static BlockState emptyComposter(BlockState state, WorldAccess world, BlockPos pos) {
		BlockState blockState = state.with(LEVEL, Integer.valueOf(0));
		world.setBlockState(pos, blockState, 3);
		return blockState;
	}

	private static BlockState addToComposter(BlockState state, WorldAccess world, BlockPos pos, ItemStack item) {
		int i = (Integer)state.get(LEVEL);
		float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(item.getItem());
		if ((i != 0 || !(f > 0.0F)) && !(world.getRandom().nextDouble() < (double)f)) {
			return state;
		} else {
			int j = i + 1;
			BlockState blockState = state.with(LEVEL, Integer.valueOf(j));
			world.setBlockState(pos, blockState, 3);
			if (j == 7) {
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), 20);
			}

			return blockState;
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Integer)state.get(LEVEL) == 7) {
			world.setBlockState(pos, state.cycle(LEVEL), 3);
			world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (Integer)state.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
		int i = (Integer)state.get(LEVEL);
		if (i == 8) {
			return new ComposterBlock.FullComposterInventory(state, world, pos, new ItemStack(Items.BONE_MEAL));
		} else {
			return (SidedInventory)(i < 7 ? new ComposterBlock.ComposterInventory(state, world, pos) : new ComposterBlock.DummyInventory());
		}
	}

	static class ComposterInventory extends SimpleInventory implements SidedInventory {
		private final BlockState state;
		private final WorldAccess world;
		private final BlockPos pos;
		private boolean dirty;

		public ComposterInventory(BlockState state, WorldAccess world, BlockPos pos) {
			super(1);
			this.state = state;
			this.world = world;
			this.pos = pos;
		}

		@Override
		public int getMaxCountPerStack() {
			return 1;
		}

		@Override
		public int[] getAvailableSlots(Direction side) {
			return side == Direction.UP ? new int[]{0} : new int[0];
		}

		@Override
		public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
			return !this.dirty && dir == Direction.UP && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
		}

		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return false;
		}

		@Override
		public void markDirty() {
			ItemStack itemStack = this.getStack(0);
			if (!itemStack.isEmpty()) {
				this.dirty = true;
				BlockState blockState = ComposterBlock.addToComposter(this.state, this.world, this.pos, itemStack);
				this.world.syncWorldEvent(1500, this.pos, blockState != this.state ? 1 : 0);
				this.removeStack(0);
			}
		}
	}

	static class DummyInventory extends SimpleInventory implements SidedInventory {
		public DummyInventory() {
			super(0);
		}

		@Override
		public int[] getAvailableSlots(Direction side) {
			return new int[0];
		}

		@Override
		public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
			return false;
		}

		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return false;
		}
	}

	static class FullComposterInventory extends SimpleInventory implements SidedInventory {
		private final BlockState state;
		private final WorldAccess world;
		private final BlockPos pos;
		private boolean dirty;

		public FullComposterInventory(BlockState state, WorldAccess world, BlockPos pos, ItemStack outputItem) {
			super(outputItem);
			this.state = state;
			this.world = world;
			this.pos = pos;
		}

		@Override
		public int getMaxCountPerStack() {
			return 1;
		}

		@Override
		public int[] getAvailableSlots(Direction side) {
			return side == Direction.DOWN ? new int[]{0} : new int[0];
		}

		@Override
		public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
			return false;
		}

		@Override
		public boolean canExtract(int slot, ItemStack stack, Direction dir) {
			return !this.dirty && dir == Direction.DOWN && stack.getItem() == Items.BONE_MEAL;
		}

		@Override
		public void markDirty() {
			ComposterBlock.emptyComposter(this.state, this.world, this.pos);
			this.dirty = true;
		}
	}
}
