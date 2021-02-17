package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class CountNoiseDecorator extends AbstractCountDecorator<CountNoiseDecoratorConfig> {
	public CountNoiseDecorator(Codec<CountNoiseDecoratorConfig> codec) {
		super(codec);
	}

	protected int getCount(Random random, CountNoiseDecoratorConfig countNoiseDecoratorConfig, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
		return d < countNoiseDecoratorConfig.noiseLevel ? countNoiseDecoratorConfig.belowNoise : countNoiseDecoratorConfig.aboveNoise;
	}
}
