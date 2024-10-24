package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
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
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CactusBlock extends Block {
	public static final MapCodec<CactusBlock> CODEC = createCodec(CactusBlock::new);
	public static final IntProperty AGE = Properties.AGE_15;
	public static final int MAX_AGE = 15;
	protected static final int field_31045 = 1;
	protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	@Override
	public MapCodec<CactusBlock> getCodec() {
		return CODEC;
	}

	protected CactusBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockPos blockPos = pos.up();
		if (world.isAir(blockPos)) {
			int i = 1;

			while (world.getBlockState(pos.down(i)).isOf(this)) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)state.get(AGE);
				if (j == 15) {
					world.setBlockState(blockPos, this.getDefaultState());
					BlockState blockState = state.with(AGE, Integer.valueOf(0));
					world.setBlockState(pos, blockState, Block.NO_REDRAW);
					world.updateNeighbor(blockState, blockPos, this, null, false);
				} else {
					world.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), Block.NO_REDRAW);
				}
			}
		}
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_SHAPE;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		if (!state.canPlaceAt(world, pos)) {
			tickView.scheduleBlockTick(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockState blockState = world.getBlockState(pos.offset(direction));
			if (blockState.isSolid() || world.getFluidState(pos.offset(direction)).isIn(FluidTags.LAVA)) {
				return false;
			}
		}

		BlockState blockState2 = world.getBlockState(pos.down());
		return (blockState2.isOf(Blocks.CACTUS) || blockState2.isIn(BlockTags.SAND)) && !world.getBlockState(pos.up()).isLiquid();
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.serverDamage(world.getDamageSources().cactus(), 1.0F);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
