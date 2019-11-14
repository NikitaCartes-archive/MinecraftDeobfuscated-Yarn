package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ChestBlock extends BlockWithEntity implements Waterloggable {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape DOUBLE_NORTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_SOUTH_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
	protected static final VoxelShape DOUBLE_WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	protected static final VoxelShape DOUBLE_EAST_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
	protected static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
	private static final ChestBlock.PropertyRetriever<Inventory> INVENTORY_RETRIEVER = new ChestBlock.PropertyRetriever<Inventory>() {
		public Inventory method_17461(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			return new DoubleInventory(chestBlockEntity, chestBlockEntity2);
		}

		public Inventory method_17460(ChestBlockEntity chestBlockEntity) {
			return chestBlockEntity;
		}
	};
	private static final ChestBlock.PropertyRetriever<NameableContainerProvider> NAME_RETRIEVER = new ChestBlock.PropertyRetriever<NameableContainerProvider>() {
		public NameableContainerProvider method_17463(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			final Inventory inventory = new DoubleInventory(chestBlockEntity, chestBlockEntity2);
			return new NameableContainerProvider() {
				@Nullable
				@Override
				public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
					if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
						chestBlockEntity.checkLootInteraction(playerInventory.player);
						chestBlockEntity2.checkLootInteraction(playerInventory.player);
						return GenericContainer.createGeneric9x6(syncId, playerInventory, inventory);
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
			};
		}

		public NameableContainerProvider method_17462(ChestBlockEntity chestBlockEntity) {
			return chestBlockEntity;
		}
	};
	private static final ChestBlock.PropertyRetriever<Float2FloatFunction> field_21581 = new ChestBlock.PropertyRetriever<Float2FloatFunction>() {
		public Float2FloatFunction method_23899(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
			return f -> Math.max(chestBlockEntity.getAnimationProgress(f), chestBlockEntity2.getAnimationProgress(f));
		}

		public Float2FloatFunction method_23898(ChestBlockEntity chestBlockEntity) {
			return chestBlockEntity::getAnimationProgress;
		}
	};

	protected ChestBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CHEST_TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (neighborState.getBlock() == this && facing.getAxis().isHorizontal()) {
			ChestType chestType = neighborState.get(CHEST_TYPE);
			if (state.get(CHEST_TYPE) == ChestType.SINGLE
				&& chestType != ChestType.SINGLE
				&& state.get(FACING) == neighborState.get(FACING)
				&& getFacing(neighborState) == facing.getOpposite()) {
				return state.with(CHEST_TYPE, chestType.getOpposite());
			}
		} else if (getFacing(state) == facing) {
			return state.with(CHEST_TYPE, ChestType.SINGLE);
		}

		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
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
		return blockState.getBlock() == this && blockState.get(CHEST_TYPE) == ChestType.SINGLE ? blockState.get(FACING) : null;
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
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateHorizontalAdjacent(pos, this);
			}

			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			NameableContainerProvider nameableContainerProvider = this.createContainerProvider(state, world, pos);
			if (nameableContainerProvider != null) {
				player.openContainer(nameableContainerProvider);
				player.incrementStat(this.getOpenStat());
			}

			return ActionResult.SUCCESS;
		}
	}

	protected Stat<Identifier> getOpenStat() {
		return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
	}

	@Nullable
	public static <T> T retrieve(BlockState state, IWorld world, BlockPos pos, boolean allowBlockedChests, ChestBlock.PropertyRetriever<T> propertyRetriever) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof ChestBlockEntity)) {
			return null;
		} else if (!allowBlockedChests && isChestBlocked(world, pos)) {
			return null;
		} else {
			ChestBlockEntity chestBlockEntity = (ChestBlockEntity)blockEntity;
			ChestType chestType = state.get(CHEST_TYPE);
			if (chestType == ChestType.SINGLE) {
				return propertyRetriever.getFromSingleChest(chestBlockEntity);
			} else {
				BlockPos blockPos = pos.offset(getFacing(state));
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() == state.getBlock()) {
					ChestType chestType2 = blockState.get(CHEST_TYPE);
					if (chestType2 != ChestType.SINGLE && chestType != chestType2 && blockState.get(FACING) == state.get(FACING)) {
						if (!allowBlockedChests && isChestBlocked(world, blockPos)) {
							return null;
						}

						BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
						if (blockEntity2 instanceof ChestBlockEntity) {
							ChestBlockEntity chestBlockEntity2 = chestType == ChestType.RIGHT ? chestBlockEntity : (ChestBlockEntity)blockEntity2;
							ChestBlockEntity chestBlockEntity3 = chestType == ChestType.RIGHT ? (ChestBlockEntity)blockEntity2 : chestBlockEntity;
							return propertyRetriever.getFromDoubleChest(chestBlockEntity2, chestBlockEntity3);
						}
					}
				}

				return propertyRetriever.getFromSingleChest(chestBlockEntity);
			}
		}
	}

	@Nullable
	public static Inventory getInventory(BlockState blockState, World world, BlockPos blockPos, boolean bl) {
		return retrieve(blockState, world, blockPos, bl, INVENTORY_RETRIEVER);
	}

	@Nullable
	@Override
	public NameableContainerProvider createContainerProvider(BlockState state, World world, BlockPos pos) {
		return retrieve(state, world, pos, false, NAME_RETRIEVER);
	}

	@Environment(EnvType.CLIENT)
	public static float method_23897(ChestAnimationProgress chestAnimationProgress, BlockState blockState, World world, BlockPos blockPos, float f) {
		Float2FloatFunction float2FloatFunction = retrieve(blockState, world, blockPos, true, field_21581);
		return float2FloatFunction == null ? chestAnimationProgress.getAnimationProgress(f) : float2FloatFunction.get(f);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new ChestBlockEntity();
	}

	private static boolean isChestBlocked(IWorld world, BlockPos pos) {
		return hasBlockOnTop(world, pos) || hasOcelotOnTop(world, pos);
	}

	private static boolean hasBlockOnTop(BlockView view, BlockPos pos) {
		BlockPos blockPos = pos.up();
		return view.getBlockState(blockPos).isSimpleFullBlock(view, blockPos);
	}

	private static boolean hasOcelotOnTop(IWorld world, BlockPos pos) {
		List<CatEntity> list = world.getNonSpectatingEntities(
			CatEntity.class,
			new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))
		);
		if (!list.isEmpty()) {
			for (CatEntity catEntity : list) {
				if (catEntity.isSitting()) {
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
		return Container.calculateComparatorOutput(getInventory(state, world, pos, false));
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
	public boolean canPlaceAtSide(BlockState world, BlockView view, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}

	interface PropertyRetriever<T> {
		T getFromDoubleChest(ChestBlockEntity rightChest, ChestBlockEntity leftChest);

		T getFromSingleChest(ChestBlockEntity chest);
	}
}
