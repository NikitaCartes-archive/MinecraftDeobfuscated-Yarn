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
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LecternBlock extends BlockWithEntity {
	public static final DirectionProperty field_16404 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_17365 = Properties.field_12484;
	public static final BooleanProperty field_17366 = Properties.field_17393;
	public static final VoxelShape field_16406 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	public static final VoxelShape field_16405 = Block.method_9541(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
	public static final VoxelShape field_16403 = VoxelShapes.method_1084(field_16406, field_16405);
	public static final VoxelShape field_17367 = Block.method_9541(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
	public static final VoxelShape field_17368 = VoxelShapes.method_1084(field_16403, field_17367);
	public static final VoxelShape field_17369 = VoxelShapes.method_17786(
		Block.method_9541(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0),
		Block.method_9541(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0),
		Block.method_9541(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0),
		field_16403
	);
	public static final VoxelShape field_17370 = VoxelShapes.method_17786(
		Block.method_9541(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333),
		Block.method_9541(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667),
		Block.method_9541(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0),
		field_16403
	);
	public static final VoxelShape field_17371 = VoxelShapes.method_17786(
		Block.method_9541(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0),
		Block.method_9541(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0),
		Block.method_9541(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0),
		field_16403
	);
	public static final VoxelShape field_17372 = VoxelShapes.method_17786(
		Block.method_9541(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667),
		Block.method_9541(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333),
		Block.method_9541(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0),
		field_16403
	);

	protected LecternBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_16404, Direction.NORTH)
				.method_11657(field_17365, Boolean.valueOf(false))
				.method_11657(field_17366, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public VoxelShape method_9571(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_16403;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_16404, itemPlacementContext.method_8042().getOpposite());
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_17368;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch ((Direction)blockState.method_11654(field_16404)) {
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
	public BlockState method_9598(BlockState blockState, Rotation rotation) {
		return blockState.method_11657(field_16404, rotation.method_10503(blockState.method_11654(field_16404)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.method_10345(blockState.method_11654(field_16404)));
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_16404, field_17365, field_17366);
	}

	@Nullable
	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new LecternBlockEntity();
	}

	public static boolean method_17472(World world, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		if (!(Boolean)blockState.method_11654(field_17366)) {
			if (!world.isClient) {
				method_17475(world, blockPos, blockState, itemStack);
			}

			return true;
		} else {
			return false;
		}
	}

	private static void method_17475(World world, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
			lecternBlockEntity.setBook(itemStack.split(1));
			method_17473(world, blockPos, blockState, true);
			world.method_8396(null, blockPos, SoundEvents.field_17482, SoundCategory.field_15245, 1.0F, 1.0F);
		}
	}

	public static void method_17473(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		world.method_8652(blockPos, blockState.method_11657(field_17365, Boolean.valueOf(false)).method_11657(field_17366, Boolean.valueOf(bl)), 3);
		method_17474(world, blockPos, blockState);
	}

	public static void method_17471(World world, BlockPos blockPos, BlockState blockState) {
		method_17476(world, blockPos, blockState, true);
		world.method_8397().method_8676(blockPos, blockState.getBlock(), 2);
		world.method_8535(1043, blockPos, 0);
	}

	private static void method_17476(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		world.method_8652(blockPos, blockState.method_11657(field_17365, Boolean.valueOf(bl)), 3);
		method_17474(world, blockPos, blockState);
	}

	private static void method_17474(World world, BlockPos blockPos, BlockState blockState) {
		world.method_8452(blockPos.down(), blockState.getBlock());
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			method_17476(world, blockPos, blockState, false);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			if ((Boolean)blockState.method_11654(field_17366)) {
				this.method_17477(blockState, world, blockPos);
			}

			if ((Boolean)blockState.method_11654(field_17365)) {
				world.method_8452(blockPos.down(), this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	private void method_17477(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
			Direction direction = blockState.method_11654(field_16404);
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
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_17365) ? 15 : 0;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.UP && blockState.method_11654(field_17365) ? 15 : 0;
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		if ((Boolean)blockState.method_11654(field_17366)) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof LecternBlockEntity) {
				return ((LecternBlockEntity)blockEntity).getComparatorOutput();
			}
		}

		return 0;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if ((Boolean)blockState.method_11654(field_17366)) {
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
		return !blockState.method_11654(field_17366) ? null : super.method_17454(blockState, world, blockPos);
	}

	private void method_17470(World world, BlockPos blockPos, PlayerEntity playerEntity) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof LecternBlockEntity) {
			playerEntity.openContainer((LecternBlockEntity)blockEntity);
			playerEntity.method_7281(Stats.field_17485);
		}
	}
}
