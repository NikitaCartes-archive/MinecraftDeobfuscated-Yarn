package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
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
	public static final IntegerProperty field_11518 = Properties.LAYERS;
	protected static final VoxelShape[] field_11517 = new VoxelShape[]{
		VoxelShapes.empty(),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected SnowBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11518, Integer.valueOf(1)));
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Integer)blockState.get(field_11518) < 5;
			case field_48:
				return false;
			case field_51:
				return false;
			default:
				return false;
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11517[blockState.get(field_11518)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11517[blockState.get(field_11518) - 1];
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.getBlockState(blockPos.down());
		Block block = blockState2.getBlock();
		return block != Blocks.field_10295 && block != Blocks.field_10225 && block != Blocks.field_10499
			? Block.isFaceFullCube(blockState2.getCollisionShape(viewableWorld, blockPos.down()), Direction.UP)
				|| blockState2.matches(BlockTags.field_15503)
				|| block == this && (Integer)blockState2.get(field_11518) == 8
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
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.getLightLevel(LightType.BLOCK_LIGHT, blockPos) > 11) {
			dropStacks(blockState, world, blockPos);
			world.clearBlockState(blockPos);
		}
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		int i = (Integer)blockState.get(field_11518);
		if (itemPlacementContext.getItemStack().getItem() != this.getItem() || i >= 8) {
			return i == 1;
		} else {
			return itemPlacementContext.method_7717() ? itemPlacementContext.getFacing() == Direction.UP : true;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getPos());
		if (blockState.getBlock() == this) {
			int i = (Integer)blockState.get(field_11518);
			return blockState.with(field_11518, Integer.valueOf(Math.min(8, i + 1)));
		} else {
			return super.getPlacementState(itemPlacementContext);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11518);
	}
}
