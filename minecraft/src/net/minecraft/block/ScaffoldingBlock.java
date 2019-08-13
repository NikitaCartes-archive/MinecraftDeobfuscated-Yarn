package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class ScaffoldingBlock extends Block implements Waterloggable {
	private static final VoxelShape NORMAL_OUTLINE_SHAPE;
	private static final VoxelShape BOTTOM_OUTLINE_SHAPE;
	private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
	private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0);
	public static final IntProperty DISTANCE = Properties.DISTANCE_0_7;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final BooleanProperty BOTTOM = Properties.BOTTOM;

	protected ScaffoldingBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory.getDefaultState().with(DISTANCE, Integer.valueOf(7)).with(WATERLOGGED, Boolean.valueOf(false)).with(BOTTOM, Boolean.valueOf(false))
		);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(DISTANCE, WATERLOGGED, BOTTOM);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if (!entityContext.isHolding(blockState.getBlock().asItem())) {
			return blockState.get(BOTTOM) ? BOTTOM_OUTLINE_SHAPE : NORMAL_OUTLINE_SHAPE;
		} else {
			return VoxelShapes.fullCube();
		}
	}

	@Override
	public VoxelShape getRayTraceShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return VoxelShapes.fullCube();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getStack().getItem() == this.asItem();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		World world = itemPlacementContext.getWorld();
		int i = calculateDistance(world, blockPos);
		return this.getDefaultState()
			.with(WATERLOGGED, Boolean.valueOf(world.getFluidState(blockPos).getFluid() == Fluids.WATER))
			.with(DISTANCE, Integer.valueOf(i))
			.with(BOTTOM, Boolean.valueOf(this.shouldBeBottom(world, blockPos, i)));
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!world.isClient) {
			world.getBlockTickScheduler().schedule(blockPos, this, 1);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if ((Boolean)blockState.get(WATERLOGGED)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		if (!iWorld.isClient()) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return blockState;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = calculateDistance(world, blockPos);
		BlockState blockState2 = blockState.with(DISTANCE, Integer.valueOf(i)).with(BOTTOM, Boolean.valueOf(this.shouldBeBottom(world, blockPos, i)));
		if ((Integer)blockState2.get(DISTANCE) == 7) {
			if ((Integer)blockState.get(DISTANCE) == 7) {
				world.spawnEntity(
					new FallingBlockEntity(
						world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, blockState2.with(WATERLOGGED, Boolean.valueOf(false))
					)
				);
			} else {
				world.breakBlock(blockPos, true);
			}
		} else if (blockState != blockState2) {
			world.setBlockState(blockPos, blockState2, 3);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return calculateDistance(viewableWorld, blockPos) < 7;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		if (entityContext.isAbove(VoxelShapes.fullCube(), blockPos, true) && !entityContext.isSneaking()) {
			return NORMAL_OUTLINE_SHAPE;
		} else {
			return blockState.get(DISTANCE) != 0 && blockState.get(BOTTOM) && entityContext.isAbove(OUTLINE_SHAPE, blockPos, true)
				? COLLISION_SHAPE
				: VoxelShapes.empty();
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}

	private boolean shouldBeBottom(BlockView blockView, BlockPos blockPos, int i) {
		return i > 0 && blockView.getBlockState(blockPos.down()).getBlock() != this;
	}

	public static int calculateDistance(BlockView blockView, BlockPos blockPos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(Direction.field_11033);
		BlockState blockState = blockView.getBlockState(mutable);
		int i = 7;
		if (blockState.getBlock() == Blocks.field_16492) {
			i = (Integer)blockState.get(DISTANCE);
		} else if (blockState.isSideSolidFullSquare(blockView, mutable, Direction.field_11036)) {
			return 0;
		}

		for (Direction direction : Direction.Type.field_11062) {
			BlockState blockState2 = blockView.getBlockState(mutable.set(blockPos).setOffset(direction));
			if (blockState2.getBlock() == Blocks.field_16492) {
				i = Math.min(i, (Integer)blockState2.get(DISTANCE) + 1);
				if (i == 1) {
					break;
				}
			}
		}

		return i;
	}

	static {
		VoxelShape voxelShape = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
		VoxelShape voxelShape2 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
		VoxelShape voxelShape3 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
		VoxelShape voxelShape4 = Block.createCuboidShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
		VoxelShape voxelShape5 = Block.createCuboidShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
		NORMAL_OUTLINE_SHAPE = VoxelShapes.union(voxelShape, voxelShape2, voxelShape3, voxelShape4, voxelShape5);
		VoxelShape voxelShape6 = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
		VoxelShape voxelShape7 = Block.createCuboidShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
		VoxelShape voxelShape8 = Block.createCuboidShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
		VoxelShape voxelShape9 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
		BOTTOM_OUTLINE_SHAPE = VoxelShapes.union(ScaffoldingBlock.COLLISION_SHAPE, NORMAL_OUTLINE_SHAPE, voxelShape7, voxelShape6, voxelShape9, voxelShape8);
	}
}
