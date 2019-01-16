package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.Hopper;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperBlock extends BlockWithEntity {
	public static final DirectionProperty field_11129 = Properties.FACING_HOPPER;
	public static final BooleanProperty field_11126 = Properties.ENABLED;
	private static final VoxelShape field_11131 = Block.createCubeShape(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
	private static final VoxelShape field_11127 = Block.createCubeShape(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
	private static final VoxelShape field_11121 = VoxelShapes.union(field_11127, field_11131);
	private static final VoxelShape field_11132 = VoxelShapes.combine(field_11121, Hopper.SHAPE_INSIDE, BooleanBiFunction.ONLY_FIRST);
	private static final VoxelShape field_11120 = VoxelShapes.union(field_11132, Block.createCubeShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
	private static final VoxelShape field_11134 = VoxelShapes.union(field_11132, Block.createCubeShape(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
	private static final VoxelShape field_11124 = VoxelShapes.union(field_11132, Block.createCubeShape(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
	private static final VoxelShape field_11122 = VoxelShapes.union(field_11132, Block.createCubeShape(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
	private static final VoxelShape field_11130 = VoxelShapes.union(field_11132, Block.createCubeShape(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
	private static final VoxelShape field_11125 = Hopper.SHAPE_INSIDE;
	private static final VoxelShape field_11133 = VoxelShapes.union(Hopper.SHAPE_INSIDE, Block.createCubeShape(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
	private static final VoxelShape field_11123 = VoxelShapes.union(Hopper.SHAPE_INSIDE, Block.createCubeShape(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
	private static final VoxelShape field_11128 = VoxelShapes.union(Hopper.SHAPE_INSIDE, Block.createCubeShape(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
	private static final VoxelShape field_11135 = VoxelShapes.union(Hopper.SHAPE_INSIDE, Block.createCubeShape(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));

	public HopperBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11129, Direction.DOWN).with(field_11126, Boolean.valueOf(true)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch ((Direction)blockState.get(field_11129)) {
			case DOWN:
				return field_11120;
			case NORTH:
				return field_11124;
			case SOUTH:
				return field_11122;
			case WEST:
				return field_11130;
			case EAST:
				return field_11134;
			default:
				return field_11132;
		}
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		switch ((Direction)blockState.get(field_11129)) {
			case DOWN:
				return field_11125;
			case NORTH:
				return field_11123;
			case SOUTH:
				return field_11128;
			case WEST:
				return field_11135;
			case EAST:
				return field_11133;
			default:
				return Hopper.SHAPE_INSIDE;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getFacing().getOpposite();
		return this.getDefaultState()
			.with(field_11129, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction)
			.with(field_11126, Boolean.valueOf(true));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new HopperBlockEntity();
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasDisplayName()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				((HopperBlockEntity)blockEntity).setCustomName(itemStack.getDisplayName());
			}
		}
	}

	@Override
	public boolean hasSolidTopSurface(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_10217(world, blockPos, blockState);
		}
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				playerEntity.openContainer((HopperBlockEntity)blockEntity);
				playerEntity.increaseStat(Stats.field_15366);
			}

			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		this.method_10217(world, blockPos, blockState);
	}

	private void method_10217(World world, BlockPos blockPos, BlockState blockState) {
		boolean bl = !world.isReceivingRedstonePower(blockPos);
		if (bl != (Boolean)blockState.get(field_11126)) {
			world.setBlockState(blockPos, blockState.with(field_11126, Boolean.valueOf(bl)), 4);
		}
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof HopperBlockEntity) {
				ItemScatterer.spawn(world, blockPos, (HopperBlockEntity)blockEntity);
				world.updateHorizontalAdjacent(blockPos, this);
			}

			super.onBlockRemoved(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return Container.calculateComparatorOutput(world.getBlockEntity(blockPos));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.MIPPED_CUTOUT;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_11129, rotation.method_10503(blockState.get(field_11129)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.getRotation(blockState.get(field_11129)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11129, field_11126);
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof HopperBlockEntity) {
			((HopperBlockEntity)blockEntity).onEntityCollided(entity);
		}
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
