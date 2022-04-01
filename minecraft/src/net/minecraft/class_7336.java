package net.minecraft;

import java.util.Collection;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class class_7336 extends AbstractLichenBlock implements class_7331, Waterloggable {
	private static final BooleanProperty field_38595 = Properties.WATERLOGGED;
	private final class_7325 field_38596 = new class_7325(new class_7336.class_7337(class_7325.field_38564));
	private final class_7325 field_38597 = new class_7325(new class_7336.class_7337(class_7325.class_7330.SAME_POSITION));

	public class_7336(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(field_38595, Boolean.valueOf(false)));
	}

	@Override
	public class_7325 method_42882() {
		return this.field_38596;
	}

	public class_7325 method_42954() {
		return this.field_38597;
	}

	public static boolean method_42952(World world, BlockPos blockPos, BlockState blockState, Collection<Direction> collection) {
		boolean bl = false;
		BlockState blockState2 = Blocks.SCULK_VEIN.getDefaultState();

		for (Direction direction : collection) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (canGrowOn(world, direction, blockPos2, world.getBlockState(blockPos2))) {
				blockState2 = blockState2.with(getProperty(direction), Boolean.valueOf(true));
				bl = true;
			}
		}

		if (!bl) {
			return false;
		} else {
			if (!blockState.getFluidState().isEmpty()) {
				blockState2 = blockState2.with(field_38595, Boolean.valueOf(true));
			}

			world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
			return true;
		}
	}

	@Override
	public void method_42917(World world, BlockState blockState, BlockPos blockPos, Random random) {
		if (blockState.isOf(this)) {
			for (Direction direction : DIRECTIONS) {
				BooleanProperty booleanProperty = getProperty(direction);
				if ((Boolean)blockState.get(booleanProperty) && world.getBlockState(blockPos.offset(direction)).isOf(Blocks.SCULK)) {
					blockState = blockState.with(booleanProperty, Boolean.valueOf(false));
				}
			}

			if (!hasAnyDirection(blockState)) {
				FluidState fluidState = world.getFluidState(blockPos);
				blockState = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
			}

			world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
			class_7331.super.method_42917(world, blockState, blockPos, random);
		}
	}

	@Override
	public int method_42920(class_7334.class_7335 arg, World world, BlockPos blockPos, Random random) {
		if (this.method_42953(world, arg.method_42934(), random)) {
			return arg.method_42945() - 1;
		} else {
			return random.nextInt(10) == 0 ? MathHelper.floor((float)arg.method_42945() * 0.5F) : arg.method_42945();
		}
	}

	private boolean method_42953(World world, BlockPos blockPos, Random random) {
		BlockState blockState = world.getBlockState(blockPos);

		for (Direction direction : Direction.method_43009(random)) {
			if (hasDirection(blockState, direction)) {
				BlockPos blockPos2 = blockPos.offset(direction);
				if (world.getBlockState(blockPos2).isIn(BlockTags.SCULK_REPLACEABLE)) {
					BlockState blockState2 = Blocks.SCULK.getDefaultState();
					world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL);
					world.playSound(null, blockPos2, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.field_38596.method_42895(blockState2, world, blockPos2);
					Direction direction2 = direction.getOpposite();

					for (Direction direction3 : DIRECTIONS) {
						if (direction3 != direction2) {
							BlockPos blockPos3 = blockPos2.offset(direction3);
							BlockState blockState3 = world.getBlockState(blockPos3);
							if (blockState3.isOf(this)) {
								this.method_42917(world, blockState3, blockPos3, random);
							}
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	public static boolean method_42951(World world, BlockState blockState, BlockPos blockPos) {
		if (!blockState.isOf(Blocks.SCULK_VEIN)) {
			return false;
		} else {
			for (Direction direction : DIRECTIONS) {
				if (hasDirection(blockState, direction) && world.getBlockState(blockPos.offset(direction)).isIn(BlockTags.SCULK_REPLACEABLE)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(field_38595)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(field_38595);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.getStack().isOf(Items.field_38541) || super.canReplace(state, context);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(field_38595) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	class class_7337 extends class_7325.class_7326 {
		private final class_7325.class_7330[] field_38599;

		public class_7337(class_7325.class_7330... args) {
			super(class_7336.this);
			this.field_38599 = args;
		}

		@Override
		public boolean method_42907(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
			BlockState blockState2 = blockView.getBlockState(blockPos2.offset(direction));
			if (!blockState2.isOf(Blocks.SCULK) && !blockState2.isOf(Blocks.SCULK_CATALYST) && !blockState2.isOf(Blocks.MOVING_PISTON)) {
				if (blockPos.getManhattanDistance(blockPos2) == 2) {
					BlockPos blockPos3 = blockPos.offset(direction.getOpposite());
					if (blockView.getBlockState(blockPos3).isSideSolidFullSquare(blockView, blockPos3, direction)) {
						return false;
					}
				}

				FluidState fluidState = blockState.getFluidState();
				return !fluidState.isEmpty() && !fluidState.isOf(Fluids.WATER)
					? false
					: blockState.getMaterial().isReplaceable() || super.method_42907(blockView, blockPos, blockPos2, direction, blockState);
			} else {
				return false;
			}
		}

		@Override
		public class_7325.class_7330[] method_42909() {
			return this.field_38599;
		}

		@Override
		public boolean method_42911(BlockState blockState) {
			return !blockState.isOf(Blocks.SCULK_VEIN);
		}
	}
}
