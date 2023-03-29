package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PitcherCropBlock extends TallPlantBlock implements Fertilizable {
	public static final IntProperty AGE = Properties.AGE_4;
	public static final int field_43240 = 4;
	private static final int field_43241 = 3;
	private static final int field_43391 = 1;
	private static final VoxelShape UPPER_OUTLINE_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 15.0, 13.0);
	private static final VoxelShape LOWER_OUTLINE_SHAPE = Block.createCuboidShape(3.0, -1.0, 3.0, 13.0, 16.0, 13.0);
	private static final VoxelShape AGE_0_COLLISION_SHAPE = Block.createCuboidShape(5.0, -1.0, 5.0, 11.0, 3.0, 11.0);
	private static final VoxelShape LOWER_COLLISION_SHAPE = Block.createCuboidShape(3.0, -1.0, 3.0, 13.0, 5.0, 13.0);

	public PitcherCropBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	private boolean isFullyGrown(BlockState state) {
		return (Integer)state.get(AGE) >= 4;
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(HALF) == DoubleBlockHalf.LOWER && !this.isFullyGrown(state);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState();
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : state;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if ((Integer)state.get(AGE) == 0) {
			return AGE_0_COLLISION_SHAPE;
		} else {
			return state.get(HALF) == DoubleBlockHalf.LOWER ? LOWER_COLLISION_SHAPE : super.getCollisionShape(state, world, pos, context);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		if (state.get(HALF) == DoubleBlockHalf.LOWER && (Integer)state.get(AGE) >= 3) {
			BlockState blockState = world.getBlockState(pos.up());
			return blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.UPPER && this.canPlantOnTop(world.getBlockState(pos.down()), world, pos);
		} else {
			return (world.getBaseLightLevel(pos, 0) >= 8 || world.isSkyVisible(pos)) && super.canPlaceAt(state, world, pos);
		}
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.FARMLAND);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
		super.appendProperties(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(HALF) == DoubleBlockHalf.UPPER ? UPPER_OUTLINE_SHAPE : LOWER_OUTLINE_SHAPE;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return false;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		float f = CropBlock.getAvailableMoisture(this, world, pos);
		boolean bl = random.nextInt((int)(25.0F / f) + 1) == 0;
		if (bl) {
			this.tryGrow(world, state, pos, 1);
		}
	}

	private void tryGrow(ServerWorld world, BlockState state, BlockPos pos, int amount) {
		int i = Math.min((Integer)state.get(AGE) + amount, 4);
		if (i < 3 || canGrowAt(world, pos.up())) {
			world.setBlockState(pos, state.with(AGE, Integer.valueOf(i)), Block.NOTIFY_LISTENERS);
			if (i >= 3) {
				DoubleBlockHalf doubleBlockHalf = state.get(HALF);
				if (doubleBlockHalf == DoubleBlockHalf.LOWER) {
					BlockPos blockPos = pos.up();
					world.setBlockState(
						blockPos, withWaterloggedState(world, pos, this.getDefaultState().with(AGE, Integer.valueOf(i)).with(HALF, DoubleBlockHalf.UPPER)), Block.NOTIFY_ALL
					);
				} else if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
					BlockPos blockPos = pos.down();
					world.setBlockState(
						blockPos, withWaterloggedState(world, pos, this.getDefaultState().with(AGE, Integer.valueOf(i)).with(HALF, DoubleBlockHalf.LOWER)), Block.NOTIFY_ALL
					);
				}
			}
		}
	}

	private static boolean canGrowAt(ServerWorld world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.isAir() || blockState.isOf(Blocks.PITCHER_CROP);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
		return !this.isFullyGrown(state);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		this.tryGrow(world, state, pos, 1);
	}
}
