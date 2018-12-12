package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CakeBlock extends Block {
	public static final IntegerProperty field_10739 = Properties.BITES;
	protected static final VoxelShape[] field_10738 = new VoxelShape[]{
		Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCubeShape(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCubeShape(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCubeShape(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCubeShape(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCubeShape(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCubeShape(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
	};

	protected CakeBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10739, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10738[blockState.get(field_10739)];
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		if (!world.isClient) {
			return this.method_9719(world, blockPos, blockState, playerEntity);
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			return this.method_9719(world, blockPos, blockState, playerEntity) || itemStack.isEmpty();
		}
	}

	private boolean method_9719(IWorld iWorld, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!playerEntity.canConsume(false)) {
			return false;
		} else {
			playerEntity.increaseStat(Stats.field_15369);
			playerEntity.getHungerManager().add(2, 0.1F);
			int i = (Integer)blockState.get(field_10739);
			if (i < 6) {
				iWorld.setBlockState(blockPos, blockState.with(field_10739, Integer.valueOf(i + 1)), 3);
			} else {
				iWorld.clearBlockState(blockPos);
			}

			return true;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.DOWN && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.down()).getMaterial().method_15799();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10739);
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return (7 - (Integer)blockState.get(field_10739)) * 2;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
