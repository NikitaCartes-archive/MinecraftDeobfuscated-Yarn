package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ChestBlock extends AbstractChestBlock<ChestBlockEntity> implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final int field_31057 = 1;
	protected static final int field_31058 = 1;
	protected static final int field_31059 = 14;
	protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>>() {
		public Optional<Inventory> getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			return Optional.of(new DoubleInventory(chestBlockEntity, chestBlockEntity2));
		}

		public Optional<Inventory> getFrom(ChestBlockEntity chestBlockEntity) {
			return Optional.of(chestBlockEntity);
		}

		public Optional<Inventory> getFallback() {
			return Optional.empty();
		}
	};
	private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>>() {
		public Optional<NamedScreenHandlerFactory> getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			final Inventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
			return Optional.of(new NamedScreenHandlerFactory() {
				@Nullable
				@Override
				public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
					if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
						chestBlockEntity.checkLootInteraction(playerInventory.player);
						chestBlockEntity2.checkLootInteraction(playerInventory.player);
						return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
					} else {
						return null;
					}
				}

				@Override
				public Text getDisplayName() {
					if (chestBlockEntity.hasCustomName()) {
						return chestBlockEntity.getDisplayName();
					} else {
						return (Text)(chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : new TranslatableText("container.chestDouble"));
					}
				}
			});
		}

		public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
			return Optional.of(chestBlockEntity);
		}

		public Optional<NamedScreenHandlerFactory> getFallback() {
			return Optional.empty();
		}
	};

	protected ChestBlock(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
		super(settings, supplier);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CHEST_TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	public static DoubleBlockProperties.Type getDoubleBlockType(BlockState state) {
		ChestType chestType = state.get(CHEST_TYPE);
		if (chestType == ChestType.SINGLE) {
			return DoubleBlockProperties.Type.SINGLE;
		} else {
			return chestType == ChestType.RIGHT ? DoubleBlockProperties.Type.FIRST : DoubleBlockProperties.Type.SECOND;
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (neighborState.isOf(this) && direction.getAxis().isHorizontal()) {
			ChestType chestType = neighborState.get(CHEST_TYPE);
			if (state.get(CHEST_TYPE) == ChestType.SINGLE
				&& chestType != ChestType.SINGLE
				&& state.get(FACING) == neighborState.get(FACING)
				&& getFacing(neighborState) == direction.getOpposite()) {
				return state.with(CHEST_TYPE, chestType.getOpposite());
			}
		} else if (getFacing(state) == direction) {
			return state.with(CHEST_TYPE, ChestType.SINGLE);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(CHEST_TYPE) == ChestType.SINGLE) {
			return SINGLE_SHAPE;
		} else {
			switch (getFacing(state)) {
				case NORTH:
				default:
					return DOUBLE_NORTH_SHAPE;
				case SOUTH:
					return DOUBLE_SOUTH_SHAPE;
				case WEST:
					return DOUBLE_WEST_SHAPE;
				case EAST:
					return DOUBLE_EAST_SHAPE;
			}
		}
	}

	public static Direction getFacing(BlockState state) {
		Direction direction = state.get(FACING);
		return state.get(CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		ChestType chestType = ChestType.SINGLE;
		Direction direction = ctx.getPlayerFacing().getOpposite();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		boolean bl = ctx.shouldCancelInteraction();
		Direction direction2 = ctx.getSide();
		if (direction2.getAxis().isHorizontal() && bl) {
			Direction direction3 = this.getNeighborChestDirection(ctx, direction2.getOpposite());
			if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
				direction = direction3;
				chestType = direction3.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
			}
		}

		if (chestType == ChestType.SINGLE && !bl) {
			if (direction == this.getNeighborChestDirection(ctx, direction.rotateYClockwise())) {
				chestType = ChestType.LEFT;
			} else if (direction == this.getNeighborChestDirection(ctx, direction.rotateYCounterclockwise())) {
				chestType = ChestType.RIGHT;
			}
		}

		return this.getDefaultState().with(FACING, direction).with(CHEST_TYPE, chestType).with(WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Nullable
	private Direction getNeighborChestDirection(ItemPlacementContext ctx, Direction dir) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
		return blockState.isOf(this) && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ChestBlockEntity) {
				((ChestBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
			if (namedScreenHandlerFactory != null) {
				player.openHandledScreen(namedScreenHandlerFactory);
				player.incrementStat(this.getOpenStat());
				PiglinBrain.onGuardedBlockInteracted(player, true);
			}

			return ActionResult.CONSUME;
		}
	}

	protected Stat<Identifier> getOpenStat() {
		return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
	}

	public BlockEntityType<? extends ChestBlockEntity> getExpectedEntityType() {
		return (BlockEntityType<? extends ChestBlockEntity>)this.entityTypeRetriever.get();
	}

	@Nullable
	public static Inventory getInventory(ChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
		return (Inventory)block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER).orElse(null);
	}

	@Override
	public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(
		BlockState state, World world, BlockPos pos, boolean ignoreBlocked
	) {
		BiPredicate<WorldAccess, BlockPos> biPredicate;
		if (ignoreBlocked) {
			biPredicate = (worldx, posx) -> false;
		} else {
			biPredicate = ChestBlock::isChestBlocked;
		}

		return DoubleBlockProperties.toPropertySource(
			(BlockEntityType<? extends ChestBlockEntity>)this.entityTypeRetriever.get(),
			ChestBlock::getDoubleBlockType,
			ChestBlock::getFacing,
			FACING,
			state,
			world,
			pos,
			biPredicate
		);
	}

	@Nullable
	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return (NamedScreenHandlerFactory)this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER).orElse(null);
	}

	public static DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(
		ChestAnimationProgress chestAnimationProgress
	) {
		return new DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction>() {
			public Float2FloatFunction getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
				return f -> Math.max(chestBlockEntity.getAnimationProgress(f), chestBlockEntity2.getAnimationProgress(f));
			}

			public Float2FloatFunction getFrom(ChestBlockEntity chestBlockEntity) {
				return chestBlockEntity::getAnimationProgress;
			}

			public Float2FloatFunction getFallback() {
				return chestAnimationProgress::getAnimationProgress;
			}
		};
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ChestBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? checkType(type, this.getExpectedEntityType(), ChestBlockEntity::clientTick) : null;
	}

	public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
		return hasBlockOnTop(world, pos) || hasOcelotOnTop(world, pos);
	}

	private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
	}

	private static boolean hasOcelotOnTop(WorldAccess world, BlockPos pos) {
		List<CatEntity> list = world.getNonSpectatingEntities(
			CatEntity.class,
			new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))
		);
		if (!list.isEmpty()) {
			for (CatEntity catEntity : list) {
				if (catEntity.isInSittingPose()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(getInventory(this, state, world, pos, false));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, CHEST_TYPE, WATERLOGGED);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof ChestBlockEntity) {
			((ChestBlockEntity)blockEntity).onScheduledTick();
		}
	}
}
