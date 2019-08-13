package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
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
	public static final IntProperty PICKLES = Properties.PICKLES;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape ONE_PICKLE_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
	protected static final VoxelShape TWO_PICKLES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
	protected static final VoxelShape THREE_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape FOUR_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

	protected SeaPickleBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(PICKLES, Integer.valueOf(1)).with(WATERLOGGED, Boolean.valueOf(true)));
	}

	@Override
	public int getLuminance(BlockState blockState) {
		return this.isDry(blockState) ? 0 : super.getLuminance(blockState) + 3 * (Integer)blockState.get(PICKLES);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
		if (blockState.getBlock() == this) {
			return blockState.with(PICKLES, Integer.valueOf(Math.min(4, (Integer)blockState.get(PICKLES) + 1)));
		} else {
			FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
			boolean bl = fluidState.matches(FluidTags.field_15517) && fluidState.getLevel() == 8;
			return super.getPlacementState(itemPlacementContext).with(WATERLOGGED, Boolean.valueOf(bl));
		}
	}

	private boolean isDry(BlockState blockState) {
		return !(Boolean)blockState.get(WATERLOGGED);
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !blockState.getCollisionShape(blockView, blockPos).getFace(Direction.field_11036).isEmpty();
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
			if ((Boolean)blockState.get(WATERLOGGED)) {
				iWorld.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getStack().getItem() == this.asItem() && blockState.get(PICKLES) < 4 ? true : super.canReplace(blockState, itemPlacementContext);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch (blockState.get(PICKLES)) {
			case 1:
			default:
				return ONE_PICKLE_SHAPE;
			case 2:
				return TWO_PICKLES_SHAPE;
			case 3:
				return THREE_PICKLES_SHAPE;
			case 4:
				return FOUR_PICKLES_SHAPE;
		}
	}

	@Override
	public FluidState getFluidState(BlockState blockState) {
		return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(PICKLES, WATERLOGGED);
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
		if (!this.isDry(blockState) && world.getBlockState(blockPos.down()).matches(BlockTags.field_15461)) {
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
								world.setBlockState(blockPos2, Blocks.field_10476.getDefaultState().with(PICKLES, Integer.valueOf(random.nextInt(4) + 1)), 3);
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

			world.setBlockState(blockPos, blockState.with(PICKLES, Integer.valueOf(4)), 2);
		}
	}
}
