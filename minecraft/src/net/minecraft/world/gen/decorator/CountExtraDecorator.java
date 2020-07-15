package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class CountExtraDecorator extends SimpleDecorator<CountExtraDecoratorConfig> {
	public CountExtraDecorator(Codec<CountExtraDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, CountExtraDecoratorConfig countExtraDecoratorConfig, BlockPos blockPos) {
		int i = countExtraDecoratorConfig.count + (random.nextFloat() < countExtraDecoratorConfig.extraChance ? countExtraDecoratorConfig.extraCount : 0);
		return IntStream.range(0, i).mapToObj(ix -> blockPos);
	}
}
