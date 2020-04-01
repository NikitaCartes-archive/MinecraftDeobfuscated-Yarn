package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NoiseHeightmap32Decorator extends Decorator<NoiseHeightmapDecoratorConfig> {
	public NoiseHeightmap32Decorator(
		Function<Dynamic<?>, ? extends NoiseHeightmapDecoratorConfig> function, Function<Random, ? extends NoiseHeightmapDecoratorConfig> function2
	) {
		super(function, function2);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		NoiseHeightmapDecoratorConfig noiseHeightmapDecoratorConfig,
		BlockPos blockPos
	) {
		double d = Biome.FOLIAGE_NOISE.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
		int i = d < noiseHeightmapDecoratorConfig.noiseLevel ? noiseHeightmapDecoratorConfig.belowNoise : noiseHeightmapDecoratorConfig.aboveNoise;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k) + 32;
			return l <= 0 ? null : new BlockPos(j, random.nextInt(l), k);
		}).filter(Objects::nonNull);
	}
}
