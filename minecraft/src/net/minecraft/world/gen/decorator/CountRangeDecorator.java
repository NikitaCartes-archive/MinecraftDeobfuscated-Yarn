package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.class_3667;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.config.decorator.RangeDecoratorConfig;

public class CountRangeDecorator extends class_3667<RangeDecoratorConfig> {
	public CountRangeDecorator(Function<Dynamic<?>, ? extends RangeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15948(Random random, RangeDecoratorConfig rangeDecoratorConfig, BlockPos blockPos) {
		return IntStream.range(0, rangeDecoratorConfig.count).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = random.nextInt(rangeDecoratorConfig.maximum - rangeDecoratorConfig.topOffset) + rangeDecoratorConfig.bottomOffset;
			int l = random.nextInt(16);
			return blockPos.add(j, k, l);
		});
	}
}
