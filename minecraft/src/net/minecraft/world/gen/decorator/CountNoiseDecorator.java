package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class CountNoiseDecorator extends Decorator<CountNoiseDecoratorConfig> {
	public CountNoiseDecorator(Codec<CountNoiseDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(DecoratorContext decoratorContext, Random random, CountNoiseDecoratorConfig countNoiseDecoratorConfig, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
		int i = d < countNoiseDecoratorConfig.noiseLevel ? countNoiseDecoratorConfig.belowNoise : countNoiseDecoratorConfig.aboveNoise;
		return IntStream.range(0, i).mapToObj(ix -> blockPos);
	}
}
