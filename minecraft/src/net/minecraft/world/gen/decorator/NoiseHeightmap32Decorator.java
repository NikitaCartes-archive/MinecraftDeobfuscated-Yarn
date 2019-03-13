package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NoiseHeightmap32Decorator extends Decorator<NoiseHeightmapDecoratorConfig> {
	public NoiseHeightmap32Decorator(Function<Dynamic<?>, ? extends NoiseHeightmapDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15936(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		NoiseHeightmapDecoratorConfig noiseHeightmapDecoratorConfig,
		BlockPos blockPos
	) {
		double d = Biome.field_9324.sample((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0);
		int i = d < noiseHeightmapDecoratorConfig.noiseLevel ? noiseHeightmapDecoratorConfig.belowNoise : noiseHeightmapDecoratorConfig.aboveNoise;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = iWorld.method_8598(Heightmap.Type.MOTION_BLOCKING, blockPos.add(j, 0, k)).getY() + 32;
			if (l <= 0) {
				return null;
			} else {
				int m = random.nextInt(l);
				return blockPos.add(j, m, k);
			}
		}).filter(Objects::nonNull);
	}
}
