package net.minecraft.world.gen.decorator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableWorld;

/**
 * Tree decorators can add additional blocks to trees, such as vines or beehives.
 */
public abstract class TreeDecorator implements DynamicSerializable {
	protected final TreeDecoratorType<?> type;

	protected TreeDecorator(TreeDecoratorType<?> treeDecoratorType) {
		this.type = treeDecoratorType;
	}

	public abstract void generate(IWorld world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box);

	protected void placeVine(ModifiableWorld world, BlockPos pos, BooleanProperty directionProperty, Set<BlockPos> set, BlockBox box) {
		this.setBlockStateAndEncompassPosition(world, pos, Blocks.VINE.getDefaultState().with(directionProperty, Boolean.valueOf(true)), set, box);
	}

	protected void setBlockStateAndEncompassPosition(ModifiableWorld world, BlockPos pos, BlockState state, Set<BlockPos> set, BlockBox box) {
		world.setBlockState(pos, state, 19);
		set.add(pos);
		box.encompass(new BlockBox(pos, pos));
	}
}
