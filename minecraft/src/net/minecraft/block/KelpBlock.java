package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class KelpBlock extends Block implements FluidFillable {
	public static final IntegerProperty field_11194 = Properties.AGE_25;
	protected static final VoxelShape field_11195 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	protected KelpBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11194, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11195;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
		return fluidState.matches(FluidTags.field_15517) && fluidState.method_15761() == 8 ? this.method_10292(itemPlacementContext.getWorld()) : null;
	}

	public BlockState method_10292(IWorld iWorld) {
		return this.getDefaultState().with(field_11194, Integer.valueOf(iWorld.getRandom().nextInt(25)));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return Fluids.WATER.getState(false);
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		} else {
			BlockPos blockPos2 = blockPos.up();
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.getBlock() == Blocks.field_10382 && (Integer)blockState.get(field_11194) < 25 && random.nextDouble() < 0.14) {
				world.setBlockState(blockPos2, blockState.method_11572(field_11194));
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		return block == Blocks.field_10092
			? false
			: block == this || block == Blocks.field_10463 || Block.isFaceFullCube(blockState2.getCollisionShape(viewableWorld, blockPos2), Direction.UP);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			if (direction == Direction.DOWN) {
				return Blocks.field_10124.getDefaultState();
			}

			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		if (direction == Direction.UP && blockState2.getBlock() == this) {
			return Blocks.field_10463.getDefaultState();
		} else {
			iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11194);
	}

	@Override
	public boolean canFillWithFluid(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return false;
	}

	@Override
	public boolean tryFillWithFluid(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
