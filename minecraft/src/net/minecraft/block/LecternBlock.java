package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.VerticalEntityPosition;
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
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LecternBlock extends BlockWithEntity {
	public static final DirectionProperty field_16404 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_17365 = Properties.POWERED;
	public static final BooleanProperty field_17366 = Properties.HAS_BOOK;
	public static final VoxelShape field_16406 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final VoxelShape field_16405 = Block.createCubeShape(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
	public static final VoxelShape field_16403 = VoxelShapes.union(field_16406, field_16405);
	public static final VoxelShape field_17367 = Block.createCubeShape(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
	public static final VoxelShape field_17368 = VoxelShapes.union(field_16403, field_17367);
	public static final VoxelShape field_17369 = VoxelShapes.method_17786(
		Block.createCubeShape(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0),
		Block.createCubeShape(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0),
		Block.createCubeShape(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0),
		field_16403
	);
	public static final VoxelShape field_17370 = VoxelShapes.method_17786(
		Block.createCubeShape(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333),
		Block.createCubeShape(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667),
		Block.createCubeShape(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0),
		field_16403
	);
	public static final VoxelShape field_17371 = VoxelShapes.method_17786(
		Block.createCubeShape(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0),
		Block.createCubeShape(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0),
		Block.createCubeShape(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0),
		field_16403
	);
	public static final VoxelShape field_17372 = VoxelShapes.method_17786(
		Block.createCubeShape(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667),
		Block.createCubeShape(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333),
		Block.createCubeShape(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0),
		field_16403
	);

	protected LecternBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(field_16404, Direction.NORTH).with(field_17365, Boolean.valueOf(false)).with(field_17366, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_16404, itemPlacementContext.getPlayerHorizontalFacing().getOpposite());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_17368;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch ((Direction)blockState.get(field_16404)) {
			case NORTH:
				return field_17370;
			case SOUTH:
				return field_17372;
			case EAST:
				return field_17371;
			case WEST:
				return field_17369;
			default:
				return field_16403;
		}
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_16404, rotation.method_10503(blockState.get(field_16404)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_16404)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16404, field_17365, field_17366);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new LecternBlockEntity();
	}

	public static boolean method_17472(World world, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		if (!(Boolean)blockState.get(field_17366)) {
			if (!world.isClient) {
				method_17475(world, blockPos, blockState, itemStack);
			}

			return true;
		} else {
			return false;
		}
	}

	private static void method_17475(World world, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
			lecternBlockEntity.method_17513(itemStack.split(1));
			method_17473(world, blockPos, blockState, true);
			world.playSound(null, blockPos, SoundEvents.field_17482, SoundCategory.field_15245, 1.0F, 1.0F);
		}
	}

	public static void method_17473(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		world.setBlockState(blockPos, blockState.with(field_17365, Boolean.valueOf(false)).with(field_17366, Boolean.valueOf(bl)), 3);
		method_17474(world, blockPos, blockState);
	}

	public static void method_17471(World world, BlockPos blockPos, BlockState blockState) {
		method_17476(world, blockPos, blockState, true);
		world.getBlockTickScheduler().schedule(blockPos, blockState.getBlock(), 2);
		world.fireWorldEvent(1043, blockPos, 0);
	}

	private static void method_17476(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		world.setBlockState(blockPos, blockState.with(field_17365, Boolean.valueOf(bl)), 3);
		method_17474(world, blockPos, blockState);
	}

	private static void method_17474(World world, BlockPos blockPos, BlockState blockState) {
		world.updateNeighborsAlways(blockPos.down(), blockState.getBlock());
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			method_17476(world, blockPos, blockState, false);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.get(field_17366)) {
				this.method_17477(blockState, world, blockPos);
			}

			if ((Boolean)blockState.get(field_17365)) {
				world.updateNeighborsAlways(blockPos.down(), this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	private void method_17477(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
			Direction direction = blockState.get(field_16404);
			ItemStack itemStack = lecternBlockEntity.method_17520().copy();
			float f = 0.25F * (float)direction.getOffsetX();
			float g = 0.25F * (float)direction.getOffsetZ();
			ItemEntity itemEntity = new ItemEntity(
				world, (double)blockPos.getX() + 0.5 + (double)f, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5 + (double)g, itemStack
			);
			itemEntity.method_6988();
			world.spawnEntity(itemEntity);
			lecternBlockEntity.clearInv();
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState blockState) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.get(field_17365) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.UP && blockState.get(field_17365) ? 15 : 0;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		if ((Boolean)blockState.get(field_17366)) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof LecternBlockEntity) {
				return ((LecternBlockEntity)blockEntity).method_17524();
			}
		}

		return 0;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.get(field_17366)) {
			if (!world.isClient) {
				this.method_17470(world, blockPos, playerEntity);
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	public NameableContainerProvider method_17454(BlockState blockState, World world, BlockPos blockPos) {
		return !blockState.get(field_17366) ? null : super.method_17454(blockState, world, blockPos);
	}

	private void method_17470(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			playerEntity.openContainer((LecternBlockEntity)blockEntity);
			playerEntity.increaseStat(Stats.field_17485);
		}
	}
}
