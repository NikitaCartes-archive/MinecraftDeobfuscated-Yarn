package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CactusBlock extends Block {
	public static final IntProperty AGE = Properties.AGE_15;
	protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	protected CactusBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (world.isAir(blockPos2)) {
				int i = 1;

				while (world.getBlockState(blockPos.down(i)).getBlock() == this) {
					i++;
				}

				if (i < 3) {
					int j = (Integer)blockState.get(AGE);
					if (j == 15) {
						world.setBlockState(blockPos2, this.getDefaultState());
						BlockState blockState2 = blockState.with(AGE, Integer.valueOf(0));
						world.setBlockState(blockPos, blockState2, 4);
						blockState2.neighborUpdate(world, blockPos2, this, blockPos, false);
					} else {
						world.setBlockState(blockPos, blockState.with(AGE, Integer.valueOf(j + 1)), 4);
					}
				}
			}
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return COLLISION_SHAPE;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return OUTLINE_SHAPE;
	}

	@Override
	public boolean isOpaque(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		for (Direction direction : Direction.Type.field_11062) {
			BlockState blockState2 = viewableWorld.getBlockState(blockPos.offset(direction));
			Material material = blockState2.getMaterial();
			if (material.isSolid() || viewableWorld.getFluidState(blockPos.offset(direction)).matches(FluidTags.field_15518)) {
				return false;
			}
		}

		Block block = viewableWorld.getBlockState(blockPos.down()).getBlock();
		return (block == Blocks.field_10029 || block == Blocks.field_10102 || block == Blocks.field_10534)
			&& !viewableWorld.getBlockState(blockPos.up()).getMaterial().isLiquid();
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.damage(DamageSource.CACTUS, 1.0F);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
