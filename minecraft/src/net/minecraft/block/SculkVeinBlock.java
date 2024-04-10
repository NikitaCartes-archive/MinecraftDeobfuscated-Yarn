package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Collection;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SculkVeinBlock extends MultifaceGrowthBlock implements SculkSpreadable, Waterloggable {
	public static final MapCodec<SculkVeinBlock> CODEC = createCodec(SculkVeinBlock::new);
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private final LichenGrower allGrowTypeGrower = new LichenGrower(new SculkVeinBlock.SculkVeinGrowChecker(LichenGrower.GROW_TYPES));
	private final LichenGrower samePositionOnlyGrower = new LichenGrower(new SculkVeinBlock.SculkVeinGrowChecker(LichenGrower.GrowType.SAME_POSITION));

	@Override
	public MapCodec<SculkVeinBlock> getCodec() {
		return CODEC;
	}

	public SculkVeinBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	public LichenGrower getGrower() {
		return this.allGrowTypeGrower;
	}

	public LichenGrower getSamePositionOnlyGrower() {
		return this.samePositionOnlyGrower;
	}

	public static boolean place(WorldAccess world, BlockPos pos, BlockState state, Collection<Direction> directions) {
		boolean bl = false;
		BlockState blockState = Blocks.SCULK_VEIN.getDefaultState();

		for (Direction direction : directions) {
			BlockPos blockPos = pos.offset(direction);
			if (canGrowOn(world, direction, blockPos, world.getBlockState(blockPos))) {
				blockState = blockState.with(getProperty(direction), Boolean.valueOf(true));
				bl = true;
			}
		}

		if (!bl) {
			return false;
		} else {
			if (!state.getFluidState().isEmpty()) {
				blockState = blockState.with(WATERLOGGED, Boolean.valueOf(true));
			}

			world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
			return true;
		}
	}

	@Override
	public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, Random random) {
		if (state.isOf(this)) {
			for (Direction direction : DIRECTIONS) {
				BooleanProperty booleanProperty = getProperty(direction);
				if ((Boolean)state.get(booleanProperty) && world.getBlockState(pos.offset(direction)).isOf(Blocks.SCULK)) {
					state = state.with(booleanProperty, Boolean.valueOf(false));
				}
			}

			if (!hasAnyDirection(state)) {
				FluidState fluidState = world.getFluidState(pos);
				state = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
			}

			world.setBlockState(pos, state, Block.NOTIFY_ALL);
			SculkSpreadable.super.spreadAtSamePosition(world, state, pos, random);
		}
	}

	@Override
	public int spread(
		SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock
	) {
		if (shouldConvertToBlock && this.convertToBlock(spreadManager, world, cursor.getPos(), random)) {
			return cursor.getCharge() - 1;
		} else {
			return random.nextInt(spreadManager.getSpreadChance()) == 0 ? MathHelper.floor((float)cursor.getCharge() * 0.5F) : cursor.getCharge();
		}
	}

	private boolean convertToBlock(SculkSpreadManager spreadManager, WorldAccess world, BlockPos pos, Random random) {
		BlockState blockState = world.getBlockState(pos);
		TagKey<Block> tagKey = spreadManager.getReplaceableTag();

		for (Direction direction : Direction.shuffle(random)) {
			if (hasDirection(blockState, direction)) {
				BlockPos blockPos = pos.offset(direction);
				BlockState blockState2 = world.getBlockState(blockPos);
				if (blockState2.isIn(tagKey)) {
					BlockState blockState3 = Blocks.SCULK.getDefaultState();
					world.setBlockState(blockPos, blockState3, Block.NOTIFY_ALL);
					Block.pushEntitiesUpBeforeBlockChange(blockState2, blockState3, world, blockPos);
					world.playSound(null, blockPos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
					this.allGrowTypeGrower.grow(blockState3, world, blockPos, spreadManager.isWorldGen());
					Direction direction2 = direction.getOpposite();

					for (Direction direction3 : DIRECTIONS) {
						if (direction3 != direction2) {
							BlockPos blockPos2 = blockPos.offset(direction3);
							BlockState blockState4 = world.getBlockState(blockPos2);
							if (blockState4.isOf(this)) {
								this.spreadAtSamePosition(world, blockState4, blockPos2, random);
							}
						}
					}

					return true;
				}
			}
		}

		return false;
	}

	public static boolean veinCoversSculkReplaceable(WorldAccess world, BlockState state, BlockPos pos) {
		if (!state.isOf(Blocks.SCULK_VEIN)) {
			return false;
		} else {
			for (Direction direction : DIRECTIONS) {
				if (hasDirection(state, direction) && world.getBlockState(pos.offset(direction)).isIn(BlockTags.SCULK_REPLACEABLE)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	protected boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !context.getStack().isOf(Items.SCULK_VEIN) || super.canReplace(state, context);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	class SculkVeinGrowChecker extends LichenGrower.LichenGrowChecker {
		private final LichenGrower.GrowType[] growTypes;

		public SculkVeinGrowChecker(final LichenGrower.GrowType... growTypes) {
			super(SculkVeinBlock.this);
			this.growTypes = growTypes;
		}

		@Override
		public boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
			BlockState blockState = world.getBlockState(growPos.offset(direction));
			if (!blockState.isOf(Blocks.SCULK) && !blockState.isOf(Blocks.SCULK_CATALYST) && !blockState.isOf(Blocks.MOVING_PISTON)) {
				if (pos.getManhattanDistance(growPos) == 2) {
					BlockPos blockPos = pos.offset(direction.getOpposite());
					if (world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction)) {
						return false;
					}
				}

				FluidState fluidState = state.getFluidState();
				if (!fluidState.isEmpty() && !fluidState.isOf(Fluids.WATER)) {
					return false;
				} else {
					return state.isIn(BlockTags.FIRE) ? false : state.isReplaceable() || super.canGrow(world, pos, growPos, direction, state);
				}
			} else {
				return false;
			}
		}

		@Override
		public LichenGrower.GrowType[] getGrowTypes() {
			return this.growTypes;
		}

		@Override
		public boolean canGrow(BlockState state) {
			return !state.isOf(Blocks.SCULK_VEIN);
		}
	}
}
