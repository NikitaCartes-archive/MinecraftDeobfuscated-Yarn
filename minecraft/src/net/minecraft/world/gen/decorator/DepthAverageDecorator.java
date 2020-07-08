package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class DepthAverageDecorator extends SimpleDecorator<CountDepthDecoratorConfig> {
	public DepthAverageDecorator(Codec<CountDepthDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, CountDepthDecoratorConfig countDepthDecoratorConfig, BlockPos blockPos) {
		int i = countDepthDecoratorConfig.count;
		int j = countDepthDecoratorConfig.spread;
		int k = blockPos.getX();
		int l = blockPos.getZ();
		int m = random.nextInt(j) + random.nextInt(j) - j + i;
		return Stream.of(new BlockPos(k, m, l));
	}
}
