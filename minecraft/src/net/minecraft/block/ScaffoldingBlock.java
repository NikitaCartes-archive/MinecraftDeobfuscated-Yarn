package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
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
	private static final VoxelShape field_16494;
	private static final VoxelShape field_16497;
	public static final IntegerProperty field_16495 = Properties.DISTANCE_0_5;
	public static final BooleanProperty field_16496 = Properties.WATERLOGGED;
	public static final BooleanProperty field_16547 = Properties.BOTTOM;

	protected ScaffoldingBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateFactory
				.getDefaultState()
				.with(field_16495, Integer.valueOf(7))
				.with(field_16496, Boolean.valueOf(false))
				.with(field_16547, Boolean.valueOf(false))
		);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_16495, field_16496, field_16547);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.get(field_16547) ? field_16497 : field_16494;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.method_8038().getAxis() == Direction.Axis.Y && itemPlacementContext.getItemStack().getItem() == this.getItem();
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = this.method_16372(viewableWorld, blockPos);
		if (direction == null) {
			return false;
		} else {
			BlockState blockState2 = viewableWorld.getBlockState(blockPos.method_10093(direction));
			return blockState2.getBlock() != this || (Integer)blockState2.get(field_16495) + 1 <= 7;
		}
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getPos();
		World world = itemPlacementContext.getWorld();
		int i = this.method_16371(world, blockPos);
		return this.getDefaultState()
			.with(field_16496, Boolean.valueOf(world.getFluidState(blockPos).getFluid() == Fluids.WATER))
			.with(field_16495, Integer.valueOf(i))
			.with(field_16547, Boolean.valueOf(this.method_16373(world, blockPos, i)));
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		if (!world.isRemote) {
			world.getBlockTickScheduler().schedule(blockPos, this, 1);
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.get(field_16496)) {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
		}

		if (!iWorld.isRemote()) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return blockState;
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		int i = this.method_16371(world, blockPos);
		BlockState blockState2 = blockState.with(field_16495, Integer.valueOf(i)).with(field_16547, Boolean.valueOf(this.method_16373(world, blockPos, i)));
		if ((Integer)blockState2.get(field_16495) == 7) {
			if ((Integer)blockState.get(field_16495) == 7) {
				FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
					world, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, blockState2.with(field_16496, Boolean.valueOf(false))
				);
				world.spawnEntity(fallingBlockEntity);
			} else {
				world.breakBlock(blockPos, true);
			}
		} else if (blockState != blockState2) {
			world.setBlockState(blockPos, blockState2, 3);
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return verticalEntityPosition.isAboveBlock(VoxelShapes.fullCube(), blockPos)
				&& (!verticalEntityPosition.isSneaking() || blockState.get(field_16495) != 0 && blockState.get(field_16547))
			? VoxelShapes.fullCube()
			: VoxelShapes.empty();
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_16496) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	private boolean method_16373(BlockView blockView, BlockPos blockPos, int i) {
		return i > 0 && blockView.getBlockState(blockPos.down()).getBlock() != this;
	}

	@Nullable
	private Direction method_16372(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos.down());
		if (blockState.getBlock() == this) {
			return Direction.DOWN;
		} else if (Block.method_9501(blockState.method_11628(blockView, blockPos), Direction.UP)) {
			return Direction.DOWN;
		} else {
			Direction direction = null;
			int i = 7;

			for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
				BlockState blockState2 = blockView.getBlockState(blockPos.method_10093(direction2));
				if (blockState2.getBlock() == this) {
					int j = (Integer)blockState2.get(field_16495);
					if (j < i) {
						i = j;
						direction = direction2;
					}
				}
			}

			return direction;
		}
	}

	private int method_16371(BlockView blockView, BlockPos blockPos) {
		Direction direction = this.method_16372(blockView, blockPos);
		if (direction == null) {
			return 7;
		} else {
			BlockState blockState = blockView.getBlockState(blockPos.method_10093(direction));
			return blockState.getBlock() == this ? Math.min(7, (Integer)blockState.get(field_16495) + (direction.getAxis() == Direction.Axis.Y ? 0 : 1)) : 0;
		}
	}

	static {
		VoxelShape voxelShape = Block.createCubeShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
		VoxelShape voxelShape2 = Block.createCubeShape(0.0, 0.0, 0.0, 2.0, 16.0, 2.0);
		VoxelShape voxelShape3 = Block.createCubeShape(14.0, 0.0, 0.0, 16.0, 16.0, 2.0);
		VoxelShape voxelShape4 = Block.createCubeShape(0.0, 0.0, 14.0, 2.0, 16.0, 16.0);
		VoxelShape voxelShape5 = Block.createCubeShape(14.0, 0.0, 14.0, 16.0, 16.0, 16.0);
		field_16494 = VoxelShapes.method_1084(
			voxelShape, VoxelShapes.method_1084(voxelShape2, VoxelShapes.method_1084(voxelShape3, VoxelShapes.method_1084(voxelShape4, voxelShape5)))
		);
		VoxelShape voxelShape6 = Block.createCubeShape(0.0, 0.0, 0.0, 2.0, 2.0, 16.0);
		VoxelShape voxelShape7 = Block.createCubeShape(14.0, 0.0, 0.0, 16.0, 2.0, 16.0);
		VoxelShape voxelShape8 = Block.createCubeShape(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
		VoxelShape voxelShape9 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
		field_16497 = VoxelShapes.method_1084(
			field_16494, VoxelShapes.method_1084(voxelShape7, VoxelShapes.method_1084(voxelShape6, VoxelShapes.method_1084(voxelShape9, voxelShape8)))
		);
	}
}
