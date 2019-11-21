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

	public Stream<BlockPos> getPositions(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
		int i = random.nextInt(Math.max(rangeDecoratorConfig.count, 1));
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset;
			return new BlockPos(j, l, k);
		});
	}
}
