package net.minecraft.block;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class StrongRootsBlock extends ConnectingBlock implements Waterloggable {
	public static final DataPool<Direction> field_50889 = DataPool.<Direction>builder()
		.add(Direction.DOWN, 10)
		.add(Direction.NORTH)
		.add(Direction.SOUTH)
		.add(Direction.EAST)
		.add(Direction.WEST)
		.build();
	public static final MapCodec<StrongRootsBlock> CODEC = createCodec(StrongRootsBlock::new);
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final Supplier<ItemStack> SILK_TOUCH_NETHERITE_PICKAXE_SUPPLIER = Suppliers.memoize(() -> {
		ItemStack itemStack = new ItemStack(Items.NETHERITE_PICKAXE);
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
		builder.set(Enchantments.SILK_TOUCH, 1);
		itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
		return itemStack;
	});
	private static final Supplier<ItemStack> FORTUNE_3_NETHERITE_PICKAXE_SUPPLIER = Suppliers.memoize(() -> {
		ItemStack itemStack = new ItemStack(Items.NETHERITE_PICKAXE);
		ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
		builder.set(Enchantments.FORTUNE, 3);
		itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
		return itemStack;
	});
	private static final Supplier<ItemStack> UNENCHANTED_NETHERITE_PICKAXE_SUPPLIER = Suppliers.memoize(() -> new ItemStack(Items.NETHERITE_PICKAXE));
	public static final DataPool<Supplier<ItemStack>> field_50892 = DataPool.<Supplier<ItemStack>>builder()
		.add(UNENCHANTED_NETHERITE_PICKAXE_SUPPLIER, 3)
		.add(SILK_TOUCH_NETHERITE_PICKAXE_SUPPLIER)
		.add(FORTUNE_3_NETHERITE_PICKAXE_SUPPLIER)
		.build();
	public static final StrongRootsBlock.class_9568 field_50893 = new StrongRootsBlock.class_9568(Direction.UP, UP);
	private static final List<StrongRootsBlock.class_9568> field_50888 = List.of(
		new StrongRootsBlock.class_9568(Direction.NORTH, NORTH),
		new StrongRootsBlock.class_9568(Direction.SOUTH, SOUTH),
		new StrongRootsBlock.class_9568(Direction.EAST, EAST),
		new StrongRootsBlock.class_9568(Direction.WEST, WEST)
	);

	@Override
	public MapCodec<StrongRootsBlock> getCodec() {
		return CODEC;
	}

	protected StrongRootsBlock(AbstractBlock.Settings settings) {
		super(0.3125F, settings);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(NORTH, Boolean.valueOf(false))
				.with(EAST, Boolean.valueOf(false))
				.with(SOUTH, Boolean.valueOf(false))
				.with(WEST, Boolean.valueOf(false))
				.with(UP, Boolean.valueOf(false))
				.with(DOWN, Boolean.valueOf(false))
				.with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getPlacementState(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
	}

	public static BlockState getPlacementState(BlockView world, BlockPos pos, BlockState state) {
		BlockState blockState = world.getBlockState(pos.down());
		BlockState blockState2 = world.getBlockState(pos.up());
		BlockState blockState3 = world.getBlockState(pos.north());
		BlockState blockState4 = world.getBlockState(pos.east());
		BlockState blockState5 = world.getBlockState(pos.south());
		BlockState blockState6 = world.getBlockState(pos.west());
		Block block = state.getBlock();
		return state.with(WATERLOGGED, Boolean.valueOf(world.getFluidState(pos).getFluid() == Fluids.WATER))
			.withIfExists(DOWN, Boolean.valueOf(blockState.isOf(block) || blockState.isOf(Blocks.POWERFUL_POTATO)))
			.withIfExists(UP, Boolean.valueOf(blockState2.isOf(block) || blockState2.isOf(Blocks.POWERFUL_POTATO)))
			.withIfExists(NORTH, Boolean.valueOf(blockState3.isOf(block) || blockState3.isOf(Blocks.POWERFUL_POTATO)))
			.withIfExists(EAST, Boolean.valueOf(blockState4.isOf(block) || blockState4.isOf(Blocks.POWERFUL_POTATO)))
			.withIfExists(SOUTH, Boolean.valueOf(blockState5.isOf(block) || blockState5.isOf(Blocks.POWERFUL_POTATO)))
			.withIfExists(WEST, Boolean.valueOf(blockState6.isOf(block) || blockState6.isOf(Blocks.POWERFUL_POTATO)));
	}

	private static Optional<BlockPos> method_59151(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random random) {
		int i = 0;
		BlockPos.Mutable mutable = blockPos.mutableCopy();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();
		List<StrongRootsBlock.class_9568> list = new ArrayList(5);
		list.add(field_50893);
		list.addAll(field_50888);
		List<StrongRootsBlock.class_9568> list2 = list.subList(1, 5);
		Util.shuffle(list2, random);

		while (i < 512) {
			for (StrongRootsBlock.class_9568 lv : list) {
				boolean bl = (Boolean)blockState.get(lv.property);
				if (bl) {
					mutable2.set(mutable, lv.direction);
					if (serverWorld.canSetBlock(mutable2)) {
						BlockState blockState2 = serverWorld.getBlockState(mutable2);
						if (blockState2.isOf(Blocks.POWERFUL_POTATO)) {
							return Optional.of(mutable2);
						}

						if (blockState2.isOf(Blocks.STRONG_ROOTS)) {
							mutable.set(mutable2);
							blockState = blockState2;
							i++;
							Util.shuffle(list2, random);
							break;
						}
					}
				}
			}
			break;
		}

		return Optional.empty();
	}

	@Nullable
	private static StrongRootsBlock.class_9567 method_59148(ServerWorld serverWorld, BlockPos blockPos) {
		BlockState blockState = serverWorld.getBlockState(blockPos);
		if (!canReplace(blockState)) {
			return null;
		} else {
			BlockState blockState2 = Blocks.STRONG_ROOTS.getDefaultState();
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				BlockState blockState3 = serverWorld.getBlockState(blockPos.offset(direction));
				boolean bl2 = blockState3.isOf(Blocks.STRONG_ROOTS) || blockState3.isOf(Blocks.POWERFUL_POTATO);
				if (bl2) {
					if (bl) {
						return null;
					}

					bl = true;
					blockState2 = blockState2.withIfExists((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(true));
				}
			}

			return bl ? new StrongRootsBlock.class_9567(blockState2, blockState) : null;
		}
	}

	@Nullable
	public static List<ItemStack> method_59149(ServerWorld serverWorld, BlockPos blockPos, Random random) {
		StrongRootsBlock.class_9567 lv = method_59148(serverWorld, blockPos);
		return lv != null ? lv.method_59159(serverWorld, blockPos, random) : null;
	}

	public static boolean canReplace(BlockState state) {
		if (state.isOf(Blocks.POWERFUL_POTATO)) {
			return false;
		} else {
			return state.isIn(BlockTags.FEATURES_CANNOT_REPLACE) ? false : !state.isOf(Blocks.STRONG_ROOTS);
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		boolean bl = neighborState.isOf(this) || neighborState.isOf(Blocks.POWERFUL_POTATO);
		return state.with((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(bl));
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		int i = 0;

		for (Property<Boolean> property : FACING_PROPERTIES.values()) {
			if ((Boolean)state.get(property)) {
				i++;
			}

			if (i > 3) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		field_50889.getDataOrEmpty(random)
			.ifPresent(
				direction -> {
					BlockPos blockPos2 = pos.offset(direction);
					StrongRootsBlock.class_9567 lv = method_59148(world, blockPos2);
					if (lv != null) {
						method_59151(world, pos, state, random)
							.ifPresent(blockPos2x -> lv.method_59159(world, blockPos2, random).forEach(itemStack -> dropStack(world, blockPos2x, itemStack)));
					}
				}
			);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	public static record class_9567(BlockState newState, BlockState oldState) {
		public List<ItemStack> method_59159(ServerWorld serverWorld, BlockPos blockPos, Random random) {
			boolean bl = this.oldState.getFluidState().getFluid() == Fluids.WATER;
			List<ItemStack> list = Block.getDroppedStacks(
				this.oldState, serverWorld, blockPos, null, null, (ItemStack)StrongRootsBlock.field_50892.getDataOrEmpty(random).map(Supplier::get).orElse(ItemStack.EMPTY)
			);
			serverWorld.setBlockState(blockPos, this.newState.with(StrongRootsBlock.WATERLOGGED, Boolean.valueOf(bl)), Block.NOTIFY_LISTENERS);
			return list;
		}
	}

	static record class_9568(Direction direction, Property<Boolean> property) {
	}
}
