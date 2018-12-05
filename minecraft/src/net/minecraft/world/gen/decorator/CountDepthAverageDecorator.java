package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.class_3667;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.config.decorator.CountDepthDecoratorConfig;

public class CountDepthAverageDecorator extends class_3667<CountDepthDecoratorConfig> {
	public CountDepthAverageDecorator(Function<Dynamic<?>, ? extends CountDepthDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15907(Random random, CountDepthDecoratorConfig countDepthDecoratorConfig, BlockPos blockPos) {
		int i = countDepthDecoratorConfig.count;
		int j = countDepthDecoratorConfig.baseline;
		int k = countDepthDecoratorConfig.spread;
		return IntStream.range(0, i).mapToObj(kx -> {
			int l = random.nextInt(16);
			int m = random.nextInt(k) + random.nextInt(k) - k + j;
			int n = random.nextInt(16);
			return blockPos.add(l, m, n);
		});
	}
}
