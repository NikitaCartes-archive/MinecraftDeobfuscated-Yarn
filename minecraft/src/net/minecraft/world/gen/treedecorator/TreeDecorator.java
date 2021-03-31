package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TestableWorld;

/**
 * Tree decorators can add additional blocks to trees, such as vines or beehives.
 */
public abstract class TreeDecorator {
	public static final Codec<TreeDecorator> TYPE_CODEC = Registry.TREE_DECORATOR_TYPE.dispatch(TreeDecorator::getType, TreeDecoratorType::getCodec);

	protected abstract TreeDecoratorType<?> getType();

	public abstract void generate(
		TestableWorld testableWorld, BiConsumer<BlockPos, BlockState> biConsumer, Random random, List<BlockPos> leavesPositions, List<BlockPos> list
	);

	protected static void placeVine(BiConsumer<BlockPos, BlockState> biConsumer, BlockPos blockPos, BooleanProperty booleanProperty) {
		biConsumer.accept(blockPos, Blocks.VINE.getDefaultState().with(booleanProperty, Boolean.valueOf(true)));
	}
}
