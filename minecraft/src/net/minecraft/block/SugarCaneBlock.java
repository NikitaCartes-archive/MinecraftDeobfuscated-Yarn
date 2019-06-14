package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
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

public class SugarCaneBlock extends Block {
	public static final IntProperty field_11610 = Properties.field_12498;
	protected static final VoxelShape field_11611 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	protected SugarCaneBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11610, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11611;
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		} else if (world.isAir(blockPos.up())) {
			int i = 1;

			while (world.method_8320(blockPos.down(i)).getBlock() == this) {
				i++;
			}

			if (i < 3) {
				int j = (Integer)blockState.method_11654(field_11610);
				if (j == 15) {
					world.method_8501(blockPos.up(), this.method_9564());
					world.method_8652(blockPos, blockState.method_11657(field_11610, Integer.valueOf(0)), 4);
				} else {
					world.method_8652(blockPos, blockState.method_11657(field_11610, Integer.valueOf(j + 1)), 4);
				}
			}
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.method_8397().schedule(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Block block = viewableWorld.method_8320(blockPos.down()).getBlock();
		if (block == this) {
			return true;
		} else {
			if (block == Blocks.field_10219
				|| block == Blocks.field_10566
				|| block == Blocks.field_10253
				|| block == Blocks.field_10520
				|| block == Blocks.field_10102
				|| block == Blocks.field_10534) {
				BlockPos blockPos2 = blockPos.down();

				for (Direction direction : Direction.Type.field_11062) {
					BlockState blockState2 = viewableWorld.method_8320(blockPos2.offset(direction));
					FluidState fluidState = viewableWorld.method_8316(blockPos2.offset(direction));
					if (fluidState.matches(FluidTags.field_15517) || blockState2.getBlock() == Blocks.field_10110) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11610);
	}
}
