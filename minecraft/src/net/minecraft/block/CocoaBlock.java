package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CocoaBlock extends HorizontalFacingBlock implements Fertilizable {
	public static final IntProperty field_10779 = Properties.field_12556;
	protected static final VoxelShape[] field_10778 = new VoxelShape[]{
		Block.method_9541(11.0, 7.0, 6.0, 15.0, 12.0, 10.0), Block.method_9541(9.0, 5.0, 5.0, 15.0, 12.0, 11.0), Block.method_9541(7.0, 3.0, 4.0, 15.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] field_10776 = new VoxelShape[]{
		Block.method_9541(1.0, 7.0, 6.0, 5.0, 12.0, 10.0), Block.method_9541(1.0, 5.0, 5.0, 7.0, 12.0, 11.0), Block.method_9541(1.0, 3.0, 4.0, 9.0, 12.0, 12.0)
	};
	protected static final VoxelShape[] field_10777 = new VoxelShape[]{
		Block.method_9541(6.0, 7.0, 1.0, 10.0, 12.0, 5.0), Block.method_9541(5.0, 5.0, 1.0, 11.0, 12.0, 7.0), Block.method_9541(4.0, 3.0, 1.0, 12.0, 12.0, 9.0)
	};
	protected static final VoxelShape[] field_10780 = new VoxelShape[]{
		Block.method_9541(6.0, 7.0, 11.0, 10.0, 12.0, 15.0), Block.method_9541(5.0, 5.0, 9.0, 11.0, 12.0, 15.0), Block.method_9541(4.0, 3.0, 7.0, 12.0, 12.0, 15.0)
	};

	public CocoaBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11177, Direction.field_11043).method_11657(field_10779, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.random.nextInt(5) == 0) {
			int i = (Integer)blockState.method_11654(field_10779);
			if (i < 2) {
				world.method_8652(blockPos, blockState.method_11657(field_10779, Integer.valueOf(i + 1)), 2);
			}
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.method_8320(blockPos.offset(blockState.method_11654(field_11177))).getBlock();
		return block.matches(BlockTags.field_15474);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		int i = (Integer)blockState.method_11654(field_10779);
		switch ((Direction)blockState.method_11654(field_11177)) {
			case field_11035:
				return field_10780[i];
			case field_11043:
			default:
				return field_10777[i];
			case field_11039:
				return field_10776[i];
			case field_11034:
				return field_10778[i];
		}
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_9564();
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();

		for (Direction direction : itemPlacementContext.getPlacementDirections()) {
			if (direction.getAxis().isHorizontal()) {
				blockState = blockState.method_11657(field_11177, direction);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == blockState.method_11654(field_11177) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return (Integer)blockState.method_11654(field_10779) < 2;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		world.method_8652(blockPos, blockState.method_11657(field_10779, Integer.valueOf((Integer)blockState.method_11654(field_10779) + 1)), 2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11177, field_10779);
	}
}
