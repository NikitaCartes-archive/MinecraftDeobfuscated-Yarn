package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.config.decorator.CountDecoratorConfig;

public class CountHeight64Decorator extends Decorator<CountDecoratorConfig> {
	public CountHeight64Decorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15912(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorSettings> chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos
	) {
		return IntStream.range(0, countDecoratorConfig.count).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = 64;
			int l = random.nextInt(16);
			return blockPos.add(j, 64, l);
		});
	}
}
