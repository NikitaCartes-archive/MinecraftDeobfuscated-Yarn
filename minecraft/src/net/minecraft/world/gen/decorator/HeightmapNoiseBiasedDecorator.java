package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class HeightmapNoiseBiasedDecorator extends Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> {
	public HeightmapNoiseBiasedDecorator(Function<Dynamic<?>, ? extends TopSolidHeightmapNoiseBiasedDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
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
			int l = iWorld.getTopY(topSolidHeightmapNoiseBiasedDecoratorConfig.heightmap, j, k);
			return new BlockPos(j, l, k);
		});
	}
}
