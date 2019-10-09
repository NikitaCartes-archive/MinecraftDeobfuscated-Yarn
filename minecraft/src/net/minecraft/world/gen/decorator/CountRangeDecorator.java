package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class CountRangeDecorator extends SimpleDecorator<RangeDecoratorConfig> {
	public CountRangeDecorator(Function<Dynamic<?>, ? extends RangeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15948(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
		return IntStream.range(0, rangeDecoratorConfig.count).mapToObj(i -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset;
			return new BlockPos(j, l, k);
		});
	}
}
