package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CactusBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	protected CactusBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
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
					world.setBlockState(pos, blockState, 4);
					blockState.neighborUpdate(world, blockPos, this, pos, false);
				} else {
					world.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 4);
				}
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return COLLISION_SHAPE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockState blockState = world.getBlockState(pos.offset(direction));
			Material material = blockState.getMaterial();
			if (material.isSolid() || world.getFluidState(pos.offset(direction)).isIn(FluidTags.LAVA)) {
				return false;
			}
		}

		BlockState blockState2 = world.getBlockState(pos.down());
		return (blockState2.isOf(Blocks.CACTUS) || blockState2.isOf(Blocks.SAND) || blockState2.isOf(Blocks.RED_SAND))
			&& !world.getBlockState(pos.up()).getMaterial().isLiquid();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.damage(DamageSource.CACTUS, 1.0F);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
