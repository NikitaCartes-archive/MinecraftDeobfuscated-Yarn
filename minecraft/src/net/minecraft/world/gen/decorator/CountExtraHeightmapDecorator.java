package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.decorator.CountExtraChanceDecoratorConfig;

public class CountExtraHeightmapDecorator extends Decorator<CountExtraChanceDecoratorConfig> {
	public CountExtraHeightmapDecorator(Function<Dynamic<?>, ? extends CountExtraChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15919(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator,
		Random random,
		CountExtraChanceDecoratorConfig countExtraChanceDecoratorConfig,
		BlockPos blockPos
	) {
		int i = countExtraChanceDecoratorConfig.count;
		if (random.nextFloat() < countExtraChanceDecoratorConfig.extraChance) {
			i += countExtraChanceDecoratorConfig.extraCount;
		}

		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			return iWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos.add(j, 0, k));
		});
	}
}
