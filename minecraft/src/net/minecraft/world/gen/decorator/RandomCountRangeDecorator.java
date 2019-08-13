package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RandomCountRangeDecorator extends SimpleDecorator<RangeDecoratorConfig> {
	public RandomCountRangeDecorator(Function<Dynamic<?>, ? extends RangeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15954(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
		int i = random.nextInt(Math.max(rangeDecoratorConfig.count, 1));
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset;
			int l = random.nextInt(16);
			return blockPos.add(j, k, l);
		});
	}
}
