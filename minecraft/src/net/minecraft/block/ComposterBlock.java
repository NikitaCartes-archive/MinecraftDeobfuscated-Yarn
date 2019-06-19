package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ComposterBlock extends Block implements InventoryProvider {
	public static final IntProperty LEVEL = Properties.LEVEL_8;
	public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<>();
	public static final VoxelShape RAY_TRACE_SHAPE = VoxelShapes.fullCube();
	private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = SystemUtil.consume(
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
		registerCompostableItem(0.3F, Items.field_8309);
		registerCompostableItem(0.3F, Items.field_8551);
		registerCompostableItem(0.3F, Items.GRASS);
		registerCompostableItem(0.3F, Items.KELP);
		registerCompostableItem(0.3F, Items.field_8188);
		registerCompostableItem(0.3F, Items.field_8706);
		registerCompostableItem(0.3F, Items.SEAGRASS);
		registerCompostableItem(0.3F, Items.field_16998);
		registerCompostableItem(0.3F, Items.field_8317);
		registerCompostableItem(0.5F, Items.DRIED_KELP_BLOCK);
		registerCompostableItem(0.5F, Items.TALL_GRASS);
		registerCompostableItem(0.5F, Items.CACTUS);
		registerCompostableItem(0.5F, Items.SUGAR_CANE);
		registerCompostableItem(0.5F, Items.VINE);
		registerCompostableItem(0.5F, Items.field_8497);
		registerCompostableItem(0.65F, Items.SEA_PICKLE);
		registerCompostableItem(0.65F, Items.LILY_PAD);
		registerCompostableItem(0.65F, Items.PUMPKIN);
		registerCompostableItem(0.65F, Items.CARVED_PUMPKIN);
		registerCompostableItem(0.65F, Items.MELON);
		registerCompostableItem(0.65F, Items.field_8279);
		registerCompostableItem(0.65F, Items.field_8186);
		registerCompostableItem(0.65F, Items.field_8179);
		registerCompostableItem(0.65F, Items.field_8116);
		registerCompostableItem(0.65F, Items.field_8567);
		registerCompostableItem(0.65F, Items.field_8861);
		registerCompostableItem(0.65F, Items.BROWN_MUSHROOM);
		registerCompostableItem(0.65F, Items.RED_MUSHROOM);
		registerCompostableItem(0.65F, Items.MUSHROOM_STEM);
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
		registerCompostableItem(0.85F, Items.field_8229);
		registerCompostableItem(0.85F, Items.field_8512);
		registerCompostableItem(0.85F, Items.field_8423);
		registerCompostableItem(1.0F, Items.CAKE);
		registerCompostableItem(1.0F, Items.field_8741);
	}

	private static void registerCompostableItem(float f, ItemConvertible itemConvertible) {
		ITEM_TO_LEVEL_INCREASE_CHANCE.put(itemConvertible.asItem(), f);
	}

	public ComposterBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(LEVEL, Integer.valueOf(0)));
	}

	@Environment(EnvType.CLIENT)
	public static void playEffects(World world, BlockPos blockPos, boolean bl) {
		BlockState blockState = world.getBlockState(blockPos);
		world.playSound(
			(double)blockPos.getX(),
			(double)blockPos.getY(),
			(double)blockPos.getZ(),
			bl ? SoundEvents.field_17608 : SoundEvents.field_17607,
			SoundCategory.field_15245,
			1.0F,
			1.0F,
			false
		);
		double d = blockState.getOutlineShape(world, blockPos).method_1102(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
		double e = 0.13125F;
		double f = 0.7375F;
		Random random = world.getRandom();

		for (int i = 0; i < 10; i++) {
			double g = random.nextGaussian() * 0.02;
			double h = random.nextGaussian() * 0.02;
			double j = random.nextGaussian() * 0.02;
			world.addParticle(
				ParticleTypes.field_17741,
				(double)blockPos.getX() + 0.13125F + 0.7375F * (double)random.nextFloat(),
				(double)blockPos.getY() + d + (double)random.nextFloat() * (1.0 - d),
				(double)blockPos.getZ() + 0.13125F + 0.7375F * (double)random.nextFloat(),
				g,
				h,
				j
			);
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return LEVEL_TO_COLLISION_SHAPE[blockState.get(LEVEL)];
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return RAY_TRACE_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return LEVEL_TO_COLLISION_SHAPE[0];
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if ((Integer)blockState.get(LEVEL) == 7) {
			world.getBlockTickScheduler().schedule(blockPos, blockState.getBlock(), 20);
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		int i = (Integer)blockState.get(LEVEL);
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem())) {
			if (i < 7 && !world.isClient) {
				boolean bl = addToComposter(blockState, world, blockPos, itemStack);
				world.playLevelEvent(1500, blockPos, bl ? 1 : 0);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			}

			return true;
		} else if (i == 8) {
			if (!world.isClient) {
				float f = 0.7F;
				double d = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
				double e = (double)(world.random.nextFloat() * 0.7F) + 0.060000002F + 0.6;
				double g = (double)(world.random.nextFloat() * 0.7F) + 0.15F;
				ItemEntity itemEntity = new ItemEntity(
					world, (double)blockPos.getX() + d, (double)blockPos.getY() + e, (double)blockPos.getZ() + g, new ItemStack(Items.field_8324)
				);
				itemEntity.setToDefaultPickupDelay();
				world.spawnEntity(itemEntity);
			}

			emptyComposter(blockState, world, blockPos);
			world.playSound(null, blockPos, SoundEvents.field_17606, SoundCategory.field_15245, 1.0F, 1.0F);
			return true;
		} else {
			return false;
		}
	}

	private static void emptyComposter(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		iWorld.setBlockState(blockPos, blockState.with(LEVEL, Integer.valueOf(0)), 3);
	}

	private static boolean addToComposter(BlockState blockState, IWorld iWorld, BlockPos blockPos, ItemStack itemStack) {
		int i = (Integer)blockState.get(LEVEL);
		float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(itemStack.getItem());
		if ((i != 0 || !(f > 0.0F)) && !(iWorld.getRandom().nextDouble() < (double)f)) {
			return false;
		} else {
			int j = i + 1;
			iWorld.setBlockState(blockPos, blockState.with(LEVEL, Integer.valueOf(j)), 3);
			if (j == 7) {
				iWorld.getBlockTickScheduler().schedule(blockPos, blockState.getBlock(), 20);
			}

			return true;
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if ((Integer)blockState.get(LEVEL) == 7) {
			world.setBlockState(blockPos, blockState.cycle(LEVEL), 3);
			world.playSound(null, blockPos, SoundEvents.field_17609, SoundCategory.field_15245, 1.0F, 1.0F);
		}

		super.onScheduledTick(blockState, world, blockPos, random);
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return (Integer)blockState.get(LEVEL);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public SidedInventory getInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
		int i = (Integer)blockState.get(LEVEL);
		if (i == 8) {
			return new ComposterBlock.FullComposterInventory(blockState, iWorld, blockPos, new ItemStack(Items.field_8324));
		} else {
			return (SidedInventory)(i < 7 ? new ComposterBlock.ComposterInventory(blockState, iWorld, blockPos) : new ComposterBlock.DummyInventory());
		}
	}

	static class ComposterInventory extends BasicInventory implements SidedInventory {
		private final BlockState state;
		private final IWorld world;
		private final BlockPos pos;
		private boolean dirty;

		public ComposterInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
			super(1);
			this.state = blockState;
			this.world = iWorld;
			this.pos = blockPos;
		}

		@Override
		public int getInvMaxStackAmount() {
			return 1;
		}

		@Override
		public int[] getInvAvailableSlots(Direction direction) {
			return direction == Direction.field_11036 ? new int[]{0} : new int[0];
		}

		@Override
		public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
			return !this.dirty && direction == Direction.field_11036 && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem());
		}

		@Override
		public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
			return false;
		}

		@Override
		public void markDirty() {
			ItemStack itemStack = this.getInvStack(0);
			if (!itemStack.isEmpty()) {
				this.dirty = true;
				ComposterBlock.addToComposter(this.state, this.world, this.pos, itemStack);
				this.removeInvStack(0);
			}
		}
	}

	static class DummyInventory extends BasicInventory implements SidedInventory {
		public DummyInventory() {
			super(0);
		}

		@Override
		public int[] getInvAvailableSlots(Direction direction) {
			return new int[0];
		}

		@Override
		public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
			return false;
		}

		@Override
		public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
			return false;
		}
	}

	static class FullComposterInventory extends BasicInventory implements SidedInventory {
		private final BlockState state;
		private final IWorld world;
		private final BlockPos pos;
		private boolean dirty;

		public FullComposterInventory(BlockState blockState, IWorld iWorld, BlockPos blockPos, ItemStack itemStack) {
			super(itemStack);
			this.state = blockState;
			this.world = iWorld;
			this.pos = blockPos;
		}

		@Override
		public int getInvMaxStackAmount() {
			return 1;
		}

		@Override
		public int[] getInvAvailableSlots(Direction direction) {
			return direction == Direction.field_11033 ? new int[]{0} : new int[0];
		}

		@Override
		public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
			return false;
		}

		@Override
		public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
			return !this.dirty && direction == Direction.field_11033 && itemStack.getItem() == Items.field_8324;
		}

		@Override
		public void markDirty() {
			ComposterBlock.emptyComposter(this.state, this.world, this.pos);
			this.dirty = true;
		}
	}
}
