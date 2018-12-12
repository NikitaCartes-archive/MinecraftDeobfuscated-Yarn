package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SeaPickleBlock extends PlantBlock implements Fertilizable, Waterloggable {
	public static final IntegerProperty field_11472 = Properties.PICKLES;
	public static final BooleanProperty field_11475 = Properties.WATERLOGGED;
	protected static final VoxelShape field_11473 = Block.createCubeShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
	protected static final VoxelShape field_11470 = Block.createCubeShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
	protected static final VoxelShape field_11471 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape field_11474 = Block.createCubeShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

	protected SeaPickleBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11472, Integer.valueOf(1)).with(field_11475, Boolean.valueOf(true)));
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return this.method_10506(blockState) ? 0 : super.getLuminance(blockState) + 3 * (Integer)blockState.get(field_11472);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos());
		if (blockState.getBlock() == this) {
			return blockState.with(field_11472, Integer.valueOf(Math.min(4, (Integer)blockState.get(field_11472) + 1)));
		} else {
			FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getPos());
			boolean bl = fluidState.matches(FluidTags.field_15517) && fluidState.method_15761() == 8;
			return super.getPlacementState(itemPlacementContext).with(field_11475, Boolean.valueOf(bl));
		}
	}

	private boolean method_10506(BlockState blockState) {
		return !(Boolean)blockState.get(field_11475);
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !blockState.getCollisionShape(blockView, blockPos).getFace(Direction.UP).isEmpty();
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return this.canPlantOnTop(viewableWorld.getBlockState(blockPos2), viewableWorld, blockPos2);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if ((Boolean)blockState.get(field_11475)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.method_15789(iWorld));
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem() && blockState.get(field_11472) < 4
			? true
			: super.method_9616(blockState, itemPlacementContext);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		switch (blockState.get(field_11472)) {
			case 1:
			default:
				return field_11473;
			case 2:
				return field_11470;
			case 3:
				return field_11471;
			case 4:
				return field_11474;
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(field_11475) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11472, field_11475);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		if (!this.method_10506(blockState) && world.getBlockState(blockPos.down()).matches(BlockTags.field_15461)) {
			int i = 5;
			int j = 1;
			int k = 2;
			int l = 0;
			int m = blockPos.getX() - 2;
			int n = 0;

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < j; p++) {
					int q = 2 + blockPos.getY() - 1;

					for (int r = q - 2; r < q; r++) {
						BlockPos blockPos2 = new BlockPos(m + o, r, blockPos.getZ() - n + p);
						if (blockPos2 != blockPos && random.nextInt(6) == 0 && world.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
							BlockState blockState2 = world.getBlockState(blockPos2.down());
							if (blockState2.matches(BlockTags.field_15461)) {
								world.setBlockState(blockPos2, Blocks.field_10476.getDefaultState().with(field_11472, Integer.valueOf(random.nextInt(4) + 1)), 3);
							}
						}
					}
				}

				if (l < 2) {
					j += 2;
					n++;
				} else {
					j -= 2;
					n--;
				}

				l++;
			}

			world.setBlockState(blockPos, blockState.with(field_11472, Integer.valueOf(4)), 2);
		}
	}
}
