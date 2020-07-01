package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.ServerWorldAccess;

/**
 * Tree decorators can add additional blocks to trees, such as vines or beehives.
 */
public abstract class TreeDecorator {
	public static final Codec<TreeDecorator> TYPE_CODEC = Registry.TREE_DECORATOR_TYPE.dispatch(TreeDecorator::getType, TreeDecoratorType::getCodec);

	protected abstract TreeDecoratorType<?> getType();

	public abstract void generate(
		ServerWorldAccess serverWorldAccess, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> set, BlockBox box
	);

	protected void placeVine(ModifiableWorld world, BlockPos pos, BooleanProperty directionProperty, Set<BlockPos> set, BlockBox box) {
		this.setBlockStateAndEncompassPosition(world, pos, Blocks.VINE.getDefaultState().with(directionProperty, Boolean.valueOf(true)), set, box);
	}

	protected void setBlockStateAndEncompassPosition(ModifiableWorld world, BlockPos pos, BlockState state, Set<BlockPos> set, BlockBox box) {
		world.setBlockState(pos, state, 19);
		set.add(pos);
		box.encompass(new BlockBox(pos, pos));
	}
}
