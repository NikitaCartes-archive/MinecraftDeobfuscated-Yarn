package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SnowBlock extends Block {
	public static final IntProperty LAYERS = Properties.LAYERS;
	protected static final VoxelShape[] LAYERS_TO_SHAPE = new VoxelShape[]{
		VoxelShapes.empty(),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected SnowBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(LAYERS, Integer.valueOf(1)));
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Integer)blockState.get(LAYERS) < 5;
			case field_48:
				return false;
			case field_51:
				return false;
			default:
				return false;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return LAYERS_TO_SHAPE[blockState.get(LAYERS)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return LAYERS_TO_SHAPE[blockState.get(LAYERS) - 1];
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
		Block block = blockState2.getBlock();
		return block != Blocks.field_10295 && block != Blocks.field_10225 && block != Blocks.field_10499
			? Block.isFaceFullSquare(blockState2.getCollisionShape(viewableWorld, blockPos.down()), Direction.field_11036)
				|| block == this && (Integer)blockState2.get(LAYERS) == 8
			: false;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		return !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.getLightLevel(LightType.field_9282, blockPos) > 11) {
			dropStacks(blockState, world, blockPos);
			world.clearBlockState(blockPos, false);
		}
	}

	@Override
	public boolean canReplace(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		int i = (Integer)blockState.get(LAYERS);
		if (itemPlacementContext.getStack().getItem() != this.asItem() || i >= 8) {
			return i == 1;
		} else {
			return itemPlacementContext.canReplaceExisting() ? itemPlacementContext.getSide() == Direction.field_11036 : true;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos());
		if (blockState.getBlock() == this) {
			int i = (Integer)blockState.get(LAYERS);
			return blockState.with(LAYERS, Integer.valueOf(Math.min(8, i + 1)));
		} else {
			return super.getPlacementState(itemPlacementContext);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(LAYERS);
	}
}
