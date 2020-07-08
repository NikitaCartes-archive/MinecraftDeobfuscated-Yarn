package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class CountExtraDecorator extends SimpleDecorator<CountExtraChanceDecoratorConfig> {
	public CountExtraDecorator(Codec<CountExtraChanceDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, CountExtraChanceDecoratorConfig countExtraChanceDecoratorConfig, BlockPos blockPos) {
		int i = countExtraChanceDecoratorConfig.count
			+ (random.nextFloat() < countExtraChanceDecoratorConfig.extraChance ? countExtraChanceDecoratorConfig.extraCount : 0);
		return IntStream.range(0, i).mapToObj(ix -> blockPos);
	}
}
