package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HeightmapNoiseBiasedDecorator extends Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
	public HeightmapNoiseBiasedDecorator(Codec<TopSolidHeightmapNoiseBiasedDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess,
		ChunkGenerator chunkGenerator,
		Random random,
		TopSolidHeightmapNoiseBiasedDecoratorConfig topSolidHeightmapNoiseBiasedDecoratorConfig,
		BlockPos blockPos
	) {
		double d = Biome.FOLIAGE_NOISE
			.sample(
				(double)blockPos.getX() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor,
				(double)blockPos.getZ() / topSolidHeightmapNoiseBiasedDecoratorConfig.noiseFactor,
				false
			);
		int i = (int)Math.ceil((d + topSolidHeightmapNoiseBiasedDecoratorConfig.noiseOffset) * (double)topSolidHeightmapNoiseBiasedDecoratorConfig.noiseToCountRatio);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = worldAccess.getTopY(topSolidHeightmapNoiseBiasedDecoratorConfig.heightmap, j, k);
			return new BlockPos(j, l, k);
		});
	}
}
