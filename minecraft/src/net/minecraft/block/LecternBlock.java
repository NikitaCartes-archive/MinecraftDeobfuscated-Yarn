package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class LecternBlock extends BlockWithEntity {
	public static final MapCodec<LecternBlock> CODEC = createCodec(LecternBlock::new);
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty HAS_BOOK = Properties.HAS_BOOK;
	public static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final VoxelShape MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
	public static final VoxelShape BASE_SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE);
	public static final VoxelShape COLLISION_SHAPE_TOP = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
	public static final VoxelShape COLLISION_SHAPE = VoxelShapes.union(BASE_SHAPE, COLLISION_SHAPE_TOP);
	public static final VoxelShape WEST_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0),
		Block.createCuboidShape(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0),
		Block.createCuboidShape(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0),
		BASE_SHAPE
	);
	public static final VoxelShape NORTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333),
		Block.createCuboidShape(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667),
		Block.createCuboidShape(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0),
		BASE_SHAPE
	);
	public static final VoxelShape EAST_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0),
		Block.createCuboidShape(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0),
		Block.createCuboidShape(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0),
		BASE_SHAPE
	);
	public static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0),
		Block.createCuboidShape(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667),
		Block.createCuboidShape(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333),
		BASE_SHAPE
	);
	private static final int SCHEDULED_TICK_DELAY = 2;

	@Override
	public MapCodec<LecternBlock> getCodec() {
		return CODEC;
	}

	protected LecternBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(HAS_BOOK, Boolean.valueOf(false))
		);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return BASE_SHAPE;
	}

	@Override
	protected boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		ItemStack itemStack = ctx.getStack();
		PlayerEntity playerEntity = ctx.getPlayer();
		boolean bl = false;
		if (!world.isClient && playerEntity != null && playerEntity.isCreativeLevelTwoOp()) {
			NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(itemStack);
			if (nbtCompound != null && nbtCompound.contains("Book")) {
				bl = true;
			}
		}

		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(HAS_BOOK, Boolean.valueOf(bl));
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_SHAPE;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction)state.get(FACING)) {
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case EAST:
				return EAST_SHAPE;
			case WEST:
				return WEST_SHAPE;
			default:
				return BASE_SHAPE;
		}
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, HAS_BOOK);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new LecternBlockEntity(pos, state);
	}

	public static boolean putBookIfAbsent(@Nullable Entity user, World world, BlockPos pos, BlockState state, ItemStack stack) {
		if (!(Boolean)state.get(HAS_BOOK)) {
			if (!world.isClient) {
				putBook(user, world, pos, state, stack);
			}

			return true;
		} else {
			return false;
		}
	}

	private static void putBook(@Nullable Entity user, World world, BlockPos pos, BlockState state, ItemStack stack) {
		if (world.getBlockEntity(pos) instanceof LecternBlockEntity lecternBlockEntity) {
			lecternBlockEntity.setBook(stack.split(1));
			setHasBook(user, world, pos, state, true);
			world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	public static void setHasBook(@Nullable Entity user, World world, BlockPos pos, BlockState state, boolean hasBook) {
		BlockState blockState = state.with(POWERED, Boolean.valueOf(false)).with(HAS_BOOK, Boolean.valueOf(hasBook));
		world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
		updateNeighborAlways(world, pos, state);
	}

	public static void setPowered(World world, BlockPos pos, BlockState state) {
		setPowered(world, pos, state, true);
		world.scheduleBlockTick(pos, state.getBlock(), 2);
		world.syncWorldEvent(WorldEvents.LECTERN_BOOK_PAGE_TURNED, pos, 0);
	}

	private static void setPowered(World world, BlockPos pos, BlockState state, boolean powered) {
		world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(powered)), Block.NOTIFY_ALL);
		updateNeighborAlways(world, pos, state);
	}

	private static void updateNeighborAlways(World world, BlockPos pos, BlockState state) {
		world.updateNeighborsAlways(pos.down(), state.getBlock());
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		setPowered(world, pos, state, false);
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if ((Boolean)state.get(HAS_BOOK)) {
				this.dropBook(state, world, pos);
			}

			if ((Boolean)state.get(POWERED)) {
				world.updateNeighborsAlways(pos.down(), this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	private void dropBook(BlockState state, World world, BlockPos pos) {
		if (world.getBlockEntity(pos) instanceof LecternBlockEntity lecternBlockEntity) {
			Direction direction = state.get(FACING);
			ItemStack itemStack = lecternBlockEntity.getBook().copy();
			float f = 0.25F * (float)direction.getOffsetX();
			float g = 0.25F * (float)direction.getOffsetZ();
			ItemEntity itemEntity = new ItemEntity(
				world, (double)pos.getX() + 0.5 + (double)f, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5 + (double)g, itemStack
			);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
			lecternBlockEntity.clear();
		}
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP && state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if ((Boolean)state.get(HAS_BOOK)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof LecternBlockEntity) {
				return ((LecternBlockEntity)blockEntity).getComparatorOutput();
			}
		}

		return 0;
	}

	@Override
	protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if ((Boolean)state.get(HAS_BOOK)) {
			return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else if (stack.isIn(ItemTags.LECTERN_BOOKS)) {
			return putBookIfAbsent(player, world, pos, state, stack) ? ItemActionResult.success(world.isClient) : ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		} else {
			return stack.isEmpty() && hand == Hand.MAIN_HAND ? ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if ((Boolean)state.get(HAS_BOOK)) {
			if (!world.isClient) {
				this.openScreen(world, pos, player);
			}

			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.CONSUME;
		}
	}

	@Nullable
	@Override
	protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return !state.get(HAS_BOOK) ? null : super.createScreenHandlerFactory(state, world, pos);
	}

	private void openScreen(World world, BlockPos pos, PlayerEntity player) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof LecternBlockEntity) {
			player.openHandledScreen((LecternBlockEntity)blockEntity);
			player.incrementStat(Stats.INTERACT_WITH_LECTERN);
		}
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
