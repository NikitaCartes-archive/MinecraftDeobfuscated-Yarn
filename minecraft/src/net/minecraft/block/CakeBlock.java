package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CakeBlock extends Block {
	public static final IntProperty BITES = Properties.BITES;
	protected static final VoxelShape[] BITES_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
	};

	protected CakeBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(BITES, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return BITES_TO_SHAPE[blockState.get(BITES)];
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient) {
			return this.tryEat(world, blockPos, blockState, playerEntity);
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			return this.tryEat(world, blockPos, blockState, playerEntity) || itemStack.isEmpty();
		}
	}

	private boolean tryEat(IWorld iWorld, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!playerEntity.canConsume(false)) {
			return false;
		} else {
			playerEntity.incrementStat(Stats.field_15369);
			playerEntity.getHungerManager().add(2, 0.1F);
			int i = (Integer)blockState.get(BITES);
			if (i < 6) {
				iWorld.setBlockState(blockPos, blockState.with(BITES, Integer.valueOf(i + 1)), 3);
			} else {
				iWorld.clearBlockState(blockPos, false);
			}

			return true;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.down()).getMaterial().isSolid();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(BITES);
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		return (7 - (Integer)blockState.get(BITES)) * 2;
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
