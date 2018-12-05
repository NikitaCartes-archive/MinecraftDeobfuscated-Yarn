package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
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
	public static final IntegerProperty field_10709 = Properties.AGE_15;
	protected static final VoxelShape field_10711 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final VoxelShape field_10710 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	protected CactusBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10709, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
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
					int j = (Integer)blockState.get(field_10709);
					if (j == 15) {
						world.setBlockState(blockPos2, this.getDefaultState());
						BlockState blockState2 = blockState.with(field_10709, Integer.valueOf(0));
						world.setBlockState(blockPos, blockState2, 4);
						blockState2.neighborUpdate(world, blockPos2, this, blockPos);
					} else {
						world.setBlockState(blockPos, blockState.with(field_10709, Integer.valueOf(j + 1)), 4);
					}
				}
			}
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10711;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10710;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			BlockState blockState2 = viewableWorld.getBlockState(blockPos.method_10093(direction));
			Material material = blockState2.getMaterial();
			if (material.method_15799() || viewableWorld.getFluidState(blockPos.method_10093(direction)).matches(FluidTags.field_15518)) {
				return false;
			}
		}

		Block block = viewableWorld.getBlockState(blockPos.down()).getBlock();
		return (block == Blocks.field_10029 || block == Blocks.field_10102 || block == Blocks.field_10534)
			&& !viewableWorld.getBlockState(blockPos.up()).getMaterial().method_15797();
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.damage(DamageSource.CACTUS, 1.0F);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10709);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
