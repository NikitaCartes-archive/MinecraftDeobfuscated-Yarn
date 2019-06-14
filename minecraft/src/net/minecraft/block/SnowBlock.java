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
	public static final IntProperty field_11518 = Properties.field_12536;
	protected static final VoxelShape[] field_11517 = new VoxelShape[]{
		VoxelShapes.method_1073(),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected SnowBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11518, Integer.valueOf(1)));
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		switch (blockPlacementEnvironment) {
			case field_50:
				return (Integer)blockState.method_11654(field_11518) < 5;
			case field_48:
				return false;
			case field_51:
				return false;
			default:
				return false;
		}
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11517[blockState.method_11654(field_11518)];
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11517[blockState.method_11654(field_11518) - 1];
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.method_8320(blockPos.down());
		Block block = blockState2.getBlock();
		return block != Blocks.field_10295 && block != Blocks.field_10225 && block != Blocks.field_10499
			? Block.method_9501(blockState2.method_11628(viewableWorld, blockPos.down()), Direction.field_11036)
				|| block == this && (Integer)blockState2.method_11654(field_11518) == 8
			: false;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.method_8314(LightType.field_9282, blockPos) > 11) {
			method_9497(blockState, world, blockPos);
			world.clearBlockState(blockPos, false);
		}
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		int i = (Integer)blockState.method_11654(field_11518);
		if (itemPlacementContext.getStack().getItem() != this.asItem() || i >= 8) {
			return i == 1;
		} else {
			return itemPlacementContext.canReplaceExisting() ? itemPlacementContext.getSide() == Direction.field_11036 : true;
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.getBlockPos());
		if (blockState.getBlock() == this) {
			int i = (Integer)blockState.method_11654(field_11518);
			return blockState.method_11657(field_11518, Integer.valueOf(Math.min(8, i + 1)));
		} else {
			return super.method_9605(itemPlacementContext);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11518);
	}
}
