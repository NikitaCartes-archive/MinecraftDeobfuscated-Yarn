package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_6982;
import net.minecraft.class_6989;
import net.minecraft.block.entity.SculkSpreadManager;
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

public class SculkVeinBlock extends AbstractLichenBlock implements class_6989, Waterloggable {
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public final class_6982 field_36871 = new class_6982(new SculkVeinBlock.class_6996(class_6982.field_36837));
	public final class_6982 field_36872 = new class_6982(new SculkVeinBlock.class_6996(new class_6982.class_6987[]{class_6982.class_6987.SAME_POSITION}));

	public boolean method_40819(World world, BlockPos blockPos, BlockState blockState, byte b) {
		boolean bl = false;
		BlockState blockState2 = this.getDefaultState();

		for (Direction direction : Direction.unpack(b)) {
			BlockPos blockPos2 = blockPos.offset(direction);
			if (canGrowOn(world, direction, blockPos2, world.getBlockState(blockPos2))) {
				blockState2 = blockState2.with(getProperty(direction), Boolean.valueOf(true));
				bl = true;
			}
		}

		if (bl) {
			if (!blockState.getFluidState().isEmpty()) {
				blockState2 = blockState2.with(WATERLOGGED, Boolean.valueOf(true));
			}

			world.setBlockState(blockPos, blockState2, Block.NOTIFY_ALL);
			return true;
		} else {
			return false;
		}
	}

	public SculkVeinBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public void method_40783(World world, BlockState blockState, BlockPos blockPos, Random random) {
		if (blockState.isOf(this)) {
			for (Direction direction : Direction.values()) {
				BooleanProperty booleanProperty = getProperty(direction);
				if ((Boolean)blockState.get(booleanProperty) && world.getBlockState(blockPos.offset(direction)).isOf(Blocks.SCULK)) {
					blockState = blockState.with(booleanProperty, Boolean.valueOf(false));
				}
			}

			if (hasAnyDirection(blockState)) {
				world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
			} else {
				FluidState fluidState = world.getFluidState(blockPos);
				blockState = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
				world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
			}

			class_6989.super.method_40783(world, blockState, blockPos, random);
		}
	}

	@Override
	public short method_40786(SculkSpreadManager.Cursor cursor, World world, BlockPos blockPos, Random random) {
		return this.method_40820(world, cursor.getPos(), random)
			? (short)(cursor.getCharge() - 1)
			: (short)(random.nextInt(10) == 0 ? MathHelper.floor((float)cursor.getCharge() * 0.5F) : cursor.getCharge());
	}

	private boolean method_40820(World world, BlockPos blockPos, Random random) {
		BlockState blockState = world.getBlockState(blockPos);

		for (Direction direction : Direction.shuffle(random)) {
			if (hasDirection(blockState, direction)) {
				BlockPos blockPos2 = blockPos.offset(direction);
				if (world.getBlockState(blockPos2).isIn(BlockTags.SCULK_REPLACEABLE)) {
					BlockState blockState2 = Blocks.SCULK.getDefaultState();
					world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL);
					world.playSound(null, blockPos2, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
					Direction direction2 = direction.getOpposite();

					for (Direction direction3 : Direction.values()) {
						if (direction3 != direction2) {
							BlockPos blockPos3 = blockPos2.offset(direction3);
							BlockState blockState3 = world.getBlockState(blockPos3);
							if (blockState3.isOf(this)) {
								this.method_40783(world, blockState3, blockPos3, random);
							}
						}
					}

					this.field_36871.method_40758(blockState2, world, blockPos2);
					return true;
				}
			}
		}

		return false;
	}

	public static boolean method_40818(World world, BlockState blockState, BlockPos blockPos) {
		if (!blockState.isOf(Blocks.SCULK_VEIN)) {
			return false;
		} else {
			for (Direction direction : Direction.values()) {
				if (hasDirection(blockState, direction) && world.getBlockState(blockPos.offset(direction)).isIn(BlockTags.SCULK_REPLACEABLE)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public class_6982 method_40751() {
		return this.field_36871;
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

	class class_6996 extends class_6982.class_6983 {
		private final class_6982.class_6987[] field_36875;

		public class_6996(class_6982.class_6987[] args) {
			super(SculkVeinBlock.this);
			this.field_36875 = args;
		}

		@Override
		public boolean method_40771(BlockView blockView, BlockPos blockPos, BlockPos blockPos2, Direction direction, BlockState blockState) {
			BlockState blockState2 = blockView.getBlockState(blockPos2.offset(direction));
			if (!blockState2.isOf(Blocks.SCULK) && !blockState2.isOf(Blocks.SCULK_CATALYST) && !blockState2.isOf(Blocks.MOVING_PISTON)) {
				if (blockPos.getManhattanDistance(blockPos2) == 2) {
					BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction.getOpposite()));
					if (blockState3.isSideSolidFullSquare(blockView, blockPos.offset(direction.getOpposite()), direction)) {
						return false;
					}
				}

				return blockState.getFluidState().isEmpty() || !blockState.isOf(Blocks.LAVA) && blockState.getFluidState().isStill()
					? blockState.getMaterial().isReplaceable() || super.method_40771(blockView, blockPos, blockPos2, direction, blockState)
					: false;
			} else {
				return false;
			}
		}

		@Override
		public class_6982.class_6987[] method_40773() {
			return this.field_36875;
		}

		@Override
		public boolean method_40775(BlockState blockState) {
			return !blockState.isOf(Blocks.SCULK_VEIN);
		}
	}
}
