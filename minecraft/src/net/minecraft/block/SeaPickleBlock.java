package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SeaPickleBlock extends PlantBlock implements Fertilizable, Waterloggable {
	public static final IntProperty PICKLES = Properties.PICKLES;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape ONE_PICKLE_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
	protected static final VoxelShape TWO_PICKLES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
	protected static final VoxelShape THREE_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape FOUR_PICKLES_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

	protected SeaPickleBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(PICKLES, Integer.valueOf(1)).with(WATERLOGGED, Boolean.valueOf(true)));
	}

	@Override
	public int getLuminance(BlockState state) {
		return this.isDry(state) ? 0 : super.getLuminance(state) + 3 * (Integer)state.get(PICKLES);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (blockState.getBlock() == this) {
			return blockState.with(PICKLES, Integer.valueOf(Math.min(4, (Integer)blockState.get(PICKLES) + 1)));
		} else {
			FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
			boolean bl = fluidState.matches(FluidTags.WATER) && fluidState.getLevel() == 8;
			return super.getPlacementState(ctx).with(WATERLOGGED, Boolean.valueOf(bl));
		}
	}

	private boolean isDry(BlockState state) {
		return !(Boolean)state.get(WATERLOGGED);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView view, BlockPos pos) {
		return !floor.getCollisionShape(view, pos).getFace(Direction.UP).isEmpty();
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		return this.canPlantOnTop(world.getBlockState(blockPos), world, blockPos);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			if ((Boolean)state.get(WATERLOGGED)) {
				world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}

			return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		return ctx.getStack().getItem() == this.asItem() && state.get(PICKLES) < 4 ? true : super.canReplace(state, ctx);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		switch (state.get(PICKLES)) {
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
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(PICKLES, WATERLOGGED);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		if (!this.isDry(state) && world.getBlockState(pos.down()).matches(BlockTags.CORAL_BLOCKS)) {
			int i = 5;
			int j = 1;
			int k = 2;
			int l = 0;
			int m = pos.getX() - 2;
			int n = 0;

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < j; p++) {
					int q = 2 + pos.getY() - 1;

					for (int r = q - 2; r < q; r++) {
						BlockPos blockPos = new BlockPos(m + o, r, pos.getZ() - n + p);
						if (blockPos != pos && random.nextInt(6) == 0 && world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
							BlockState blockState = world.getBlockState(blockPos.down());
							if (blockState.matches(BlockTags.CORAL_BLOCKS)) {
								world.setBlockState(blockPos, Blocks.SEA_PICKLE.getDefaultState().with(PICKLES, Integer.valueOf(random.nextInt(4) + 1)), 3);
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

			world.setBlockState(pos, state.with(PICKLES, Integer.valueOf(4)), 2);
		}
	}
}
