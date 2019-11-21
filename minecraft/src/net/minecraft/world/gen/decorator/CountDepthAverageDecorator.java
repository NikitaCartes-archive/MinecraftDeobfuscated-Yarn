package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class CountDepthAverageDecorator extends SimpleDecorator<CountDepthDecoratorConfig> {
	public CountDepthAverageDecorator(Function<Dynamic<?>, ? extends CountDepthDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(Random random, CountDepthDecoratorConfig countDepthDecoratorConfig, BlockPos blockPos) {
		int i = countDepthDecoratorConfig.count;
		int j = countDepthDecoratorConfig.baseline;
		int k = countDepthDecoratorConfig.spread;
		return IntStream.range(0, i).mapToObj(kx -> {
			int l = random.nextInt(16) + blockPos.getX();
			int m = random.nextInt(16) + blockPos.getZ();
			int n = random.nextInt(k) + random.nextInt(k) - k + j;
			return new BlockPos(l, n, m);
		});
	}
}
