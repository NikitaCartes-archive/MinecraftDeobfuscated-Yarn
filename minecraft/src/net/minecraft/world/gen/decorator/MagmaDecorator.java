package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class MagmaDecorator extends Decorator<CountDecoratorConfig> {
	public MagmaDecorator(Function<Dynamic<?>, ? extends CountDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos
	) {
		int i = iWorld.getSeaLevel() / 2 + 1;
		return IntStream.range(0, countDecoratorConfig.count).mapToObj(j -> {
			int k = random.nextInt(16) + blockPos.getX();
			int l = random.nextInt(16) + blockPos.getZ();
			int m = i - 5 + random.nextInt(10);
			return new BlockPos(k, m, l);
		});
	}
}
