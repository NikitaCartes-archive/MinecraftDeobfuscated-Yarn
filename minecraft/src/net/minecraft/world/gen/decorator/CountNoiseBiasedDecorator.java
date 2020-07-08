package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class CountNoiseBiasedDecorator extends SimpleDecorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
	public CountNoiseBiasedDecorator(Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(Random random, TopSolidHeightmapNoiseBiasedDecoratorConfig topSolidHeightmapNoiseBiasedDecoratorConfig, BlockPos blockPos) {
		double d = Biome.FOLIAGE_NOISE
			.sample(
				(double)blockPos.getX() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor,
				(double)blockPos.getZ() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor,
				false
			);
		int i = (int)Math.ceil((d + topSolidHeightmapNoiseBiasedDecoratorConfig.noiseOffset) * (double)topSolidHeightmapNoiseBiasedDecoratorConfig.noiseToCountRatio);
		return IntStream.range(0, i).mapToObj(ix -> blockPos);
	}
}
