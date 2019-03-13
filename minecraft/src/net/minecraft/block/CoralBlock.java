package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CoralBlock extends CoralParentBlock {
	private final Block deadCoralBlock;
	protected static final VoxelShape field_10834 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

	protected CoralBlock(Block block, Block.Settings settings) {
		super(settings);
		this.deadCoralBlock = block;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2) {
		this.method_9430(blockState, world, blockPos);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!method_9431(blockState, world, blockPos)) {
			world.method_8652(blockPos, this.deadCoralBlock.method_9564().method_11657(field_9940, Boolean.valueOf(false)), 2);
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.DOWN && !blockState.method_11591(iWorld, blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			this.method_9430(blockState, iWorld, blockPos);
			if ((Boolean)blockState.method_11654(field_9940)) {
				iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_10834;
	}
}
