package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LecternBlock extends BlockWithEntity {
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
		Block.createCuboidShape(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0),
		Block.createCuboidShape(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0),
		Block.createCuboidShape(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0),
		BASE_SHAPE
	);
	public static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(
		Block.createCuboidShape(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667),
		Block.createCuboidShape(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333),
		Block.createCuboidShape(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0),
		BASE_SHAPE
	);

	protected LecternBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(FACING, Direction.field_11043).with(POWERED, Boolean.valueOf(false)).with(HAS_BOOK, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return BASE_SHAPE;
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return COLLISION_SHAPE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((Direction)blockState.get(FACING)) {
			case field_11043:
				return NORTH_SHAPE;
			case field_11035:
				return SOUTH_SHAPE;
			case field_11034:
				return EAST_SHAPE;
			case field_11039:
				return WEST_SHAPE;
			default:
				return BASE_SHAPE;
		}
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, HAS_BOOK);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new LecternBlockEntity();
	}

	public static boolean putBookIfAbsent(World world, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		if (!(Boolean)blockState.get(HAS_BOOK)) {
			if (!world.isClient) {
				putBook(world, blockPos, blockState, itemStack);
			}

			return true;
		} else {
			return false;
		}
	}

	private static void putBook(World world, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
			lecternBlockEntity.setBook(itemStack.split(1));
			setHasBook(world, blockPos, blockState, true);
			world.playSound(null, blockPos, SoundEvents.field_17482, SoundCategory.field_15245, 1.0F, 1.0F);
		}
	}

	public static void setHasBook(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(false)).with(HAS_BOOK, Boolean.valueOf(bl)), 3);
		updateNeighborAlways(world, blockPos, blockState);
	}

	public static void setPowered(World world, BlockPos blockPos, BlockState blockState) {
		setPowered(world, blockPos, blockState, true);
		world.getBlockTickScheduler().schedule(blockPos, blockState.getBlock(), 2);
		world.playLevelEvent(1043, blockPos, 0);
	}

	private static void setPowered(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		world.setBlockState(blockPos, blockState.with(POWERED, Boolean.valueOf(bl)), 3);
		updateNeighborAlways(world, blockPos, blockState);
	}

	private static void updateNeighborAlways(World world, BlockPos blockPos, BlockState blockState) {
		world.updateNeighborsAlways(blockPos.down(), blockState.getBlock());
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			setPowered(world, blockPos, blockState, false);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.get(HAS_BOOK)) {
				this.dropBook(blockState, world, blockPos);
			}

			if ((Boolean)blockState.get(POWERED)) {
				world.updateNeighborsAlways(blockPos.down(), this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	private void dropBook(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
			Direction direction = blockState.get(FACING);
			ItemStack itemStack = lecternBlockEntity.getBook().copy();
			float f = 0.25F * (float)direction.getOffsetX();
			float g = 0.25F * (float)direction.getOffsetZ();
			ItemEntity itemEntity = new ItemEntity(
				world, (double)blockPos.getX() + 0.5 + (double)f, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5 + (double)g, itemStack
			);
			itemEntity.setToDefaultPickupDelay();
			world.spawnEntity(itemEntity);
			lecternBlockEntity.clear();
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.field_11036 && blockState.get(POWERED) ? 15 : 0;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		if ((Boolean)blockState.get(HAS_BOOK)) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof LecternBlockEntity) {
				return ((LecternBlockEntity)blockEntity).getComparatorOutput();
			}
		}

		return 0;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.get(HAS_BOOK)) {
			if (!world.isClient) {
				this.openContainer(world, blockPos, playerEntity);
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
		return !blockState.get(HAS_BOOK) ? null : super.createContainerProvider(blockState, world, blockPos);
	}

	private void openContainer(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			playerEntity.openContainer((LecternBlockEntity)blockEntity);
			playerEntity.incrementStat(Stats.field_17485);
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
