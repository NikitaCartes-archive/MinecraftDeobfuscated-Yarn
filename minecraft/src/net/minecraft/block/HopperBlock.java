package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperBlock extends BlockWithEntity {
	public static final DirectionProperty field_11129 = Properties.field_12545;
	public static final BooleanProperty field_11126 = Properties.field_12515;
	private static final VoxelShape field_11131 = Block.method_9541(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape field_11127 = Block.method_9541(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape field_11121 = VoxelShapes.method_1084(field_11127, field_11131);
	private static final VoxelShape field_11132 = VoxelShapes.method_1072(field_11121, Hopper.field_12025, BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape field_11120 = VoxelShapes.method_1084(field_11132, Block.method_9541(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final VoxelShape field_11134 = VoxelShapes.method_1084(field_11132, Block.method_9541(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final VoxelShape field_11124 = VoxelShapes.method_1084(field_11132, Block.method_9541(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final VoxelShape field_11122 = VoxelShapes.method_1084(field_11132, Block.method_9541(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final VoxelShape field_11130 = VoxelShapes.method_1084(field_11132, Block.method_9541(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final VoxelShape field_11125 = Hopper.field_12025;
	private static final VoxelShape field_11133 = VoxelShapes.method_1084(Hopper.field_12025, Block.method_9541(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
	private static final VoxelShape field_11123 = VoxelShapes.method_1084(Hopper.field_12025, Block.method_9541(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
	private static final VoxelShape field_11128 = VoxelShapes.method_1084(Hopper.field_12025, Block.method_9541(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
	private static final VoxelShape field_11135 = VoxelShapes.method_1084(Hopper.field_12025, Block.method_9541(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));

	public HopperBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11129, Direction.field_11033).method_11657(field_11126, Boolean.valueOf(true)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((Direction)blockState.method_11654(field_11129)) {
			case field_11033:
				return field_11120;
			case field_11043:
				return field_11124;
			case field_11035:
				return field_11122;
			case field_11039:
				return field_11130;
			case field_11034:
				return field_11134;
			default:
				return field_11132;
		}
	}

	@Override
	public VoxelShape method_9584(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		switch ((Direction)blockState.method_11654(field_11129)) {
			case field_11033:
				return field_11125;
			case field_11043:
				return field_11123;
			case field_11035:
				return field_11128;
			case field_11039:
				return field_11135;
			case field_11034:
				return field_11133;
			default:
				return Hopper.field_12025;
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getSide().getOpposite();
		return this.method_9564()
			.method_11657(field_11129, direction.getAxis() == Direction.Axis.Y ? Direction.field_11033 : direction)
			.method_11657(field_11126, Boolean.valueOf(true));
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new HopperBlockEntity();
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				((HopperBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_10217(world, blockPos, blockState);
		}
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				playerEntity.openContainer((HopperBlockEntity)blockEntity);
				playerEntity.incrementStat(Stats.field_15366);
			}

			return true;
		}
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		this.method_10217(world, blockPos, blockState);
	}

	private void method_10217(World world, BlockPos blockPos, BlockState blockState) {
		boolean bl = !world.isReceivingRedstonePower(blockPos);
		if (bl != (Boolean)blockState.method_11654(field_11126)) {
			world.method_8652(blockPos, blockState.method_11657(field_11126, Boolean.valueOf(bl)), 4);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				ItemScatterer.method_5451(world, blockPos, (HopperBlockEntity)blockEntity);
				world.method_8455(blockPos, this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.method_7608(world.method_8321(blockPos));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11129, blockRotation.rotate(blockState.method_11654(field_11129)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11129)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11129, field_11126);
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof HopperBlockEntity) {
			((HopperBlockEntity)blockEntity).onEntityCollided(entity);
		}
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
