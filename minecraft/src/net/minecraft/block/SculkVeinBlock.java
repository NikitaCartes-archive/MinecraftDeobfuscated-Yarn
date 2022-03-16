package net.minecraft.block;

import java.util.Collection;
import java.util.Random;
import net.minecraft.class_7118;
import net.minecraft.class_7124;
import net.minecraft.class_7128;
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
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SculkVeinBlock extends AbstractLichenBlock implements class_7124, Waterloggable {
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private final class_7118 field_37632 = new class_7118(new SculkVeinBlock.class_7131(class_7118.field_37595));
	private final class_7118 field_37633 = new class_7118(new SculkVeinBlock.class_7131(class_7118.class_7123.SAME_POSITION));

	public SculkVeinBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public class_7118 method_41432() {
		return this.field_37632;
	}

	public class_7118 method_41516() {
		return this.field_37633;
	}

	public static boolean method_41514(WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Collection<Direction> collection) {
		boolean bl = false;
		BlockState blockState2 = Blocks.SCULK_VEIN.getDefaultState();

		for (Direction direction : collection) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (canGrowOn(worldAccess, direction, blockPos2, worldAccess.getBlockState(blockPos2))) {
				blockState2 = blockState2.with(getProperty(direction), Boolean.valueOf(true));
				bl = true;
			}
		}

		if (!bl) {
			return false;
		} else {
			if (!blockState.getFluidState().isEmpty()) {
				blockState2 = blockState2.with(WATERLOGGED, Boolean.valueOf(true));
			}

			worldAccess.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
			return true;
		}
	}

	@Override
	public void method_41468(WorldAccess worldAccess, BlockState blockState, BlockPos blockPos, Random random) {
		if (blockState.isOf(this)) {
			for (Direction direction : DIRECTIONS) {
				BooleanProperty booleanProperty = getProperty(direction);
				if ((Boolean)blockState.get(booleanProperty) && worldAccess.getBlockState(blockPos.offset(direction)).isOf(Blocks.SCULK)) {
					blockState = blockState.with(booleanProperty, Boolean.valueOf(false));
				}
			}

			if (!hasAnyDirection(blockState)) {
				FluidState fluidState = worldAccess.getFluidState(blockPos);
				blockState = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
			}

			worldAccess.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
			class_7124.super.method_41468(worldAccess, blockState, blockPos, random);
		}
	}

	@Override
	public int method_41471(class_7128.class_7129 arg, WorldAccess worldAccess, BlockPos blockPos, Random random, class_7128 arg2, boolean bl) {
		if (bl && this.method_41515(arg2, worldAccess, arg.method_41495(), random)) {
			return arg.method_41508() - 1;
		} else {
			return random.nextInt(arg2.method_41490()) == 0 ? MathHelper.floor((float)arg.method_41508() * 0.5F) : arg.method_41508();
		}
	}

	private boolean method_41515(class_7128 arg, WorldAccess worldAccess, BlockPos blockPos, Random random) {
		BlockState blockState = worldAccess.getBlockState(blockPos);
		TagKey<Block> tagKey = arg.method_41487();

		for (Direction direction : Direction.shuffle(random)) {
			if (hasDirection(blockState, direction)) {
				BlockPos blockPos2 = blockPos.offset(direction);
				if (worldAccess.getBlockState(blockPos2).isIn(tagKey)) {
					BlockState blockState2 = Blocks.SCULK.getDefaultState();
					worldAccess.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL);
					worldAccess.playSound(null, blockPos2, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.field_37632.method_41452(blockState2, worldAccess, blockPos2, arg.method_41492());
					Direction direction2 = direction.getOpposite();

					for (Direction direction3 : DIRECTIONS) {
						if (direction3 != direction2) {
							BlockPos blockPos3 = blockPos2.offset(direction3);
							BlockState blockState3 = worldAccess.getBlockState(blockPos3);
							if (blockState3.isOf(this)) {
								this.method_41468(worldAccess, blockState3, blockPos3, random);
							}
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	public static boolean method_41513(WorldAccess worldAccess, BlockState blockState, BlockPos blockPos) {
		if (!blockState.isOf(Blocks.SCULK_VEIN)) {
			return false;
		} else {
			for (Direction direction : DIRECTIONS) {
				if (hasDirection(blockState, direction) && worldAccess.getBlockState(blockPos.offset(direction)).isIn(BlockTags.SCULK_REPLACEABLE)) {
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
		if ((Boolean)state.get(WATERLOGGED)) {
			world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.getStack().isOf(Items.SCULK_VEIN) || super.canReplace(state, context);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	class class_7131 extends class_7118.class_7119 {
		private final class_7118.class_7123[] field_37635;

		public class_7131(class_7118.class_7123... args) {
			super(SculkVeinBlock.this);
			this.field_37635 = args;
		}

		@Override
		public boolean method_41458(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
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
					: blockState.getMaterial().isReplaceable() || super.method_41458(blockView, blockPos, blockPos2, direction, blockState);
			} else {
				return false;
			}
		}

		@Override
		public class_7118.class_7123[] method_41460() {
			return this.field_37635;
		}

		@Override
		public boolean method_41462(BlockState blockState) {
			return !blockState.isOf(Blocks.SCULK_VEIN);
		}
	}
}
