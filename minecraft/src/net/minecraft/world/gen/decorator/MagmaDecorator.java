package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MagmaDecorator extends Decorator<CountDecoratorConfig> {
	public MagmaDecorator(Codec<CountDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos
	) {
		int i = worldAccess.getSeaLevel() / 2 + 1;
		return IntStream.range(0, countDecoratorConfig.count).mapToObj(j -> {
			int k = random.nextInt(16) + blockPos.getX();
			int l = random.nextInt(16) + blockPos.getZ();
			int m = i - 5 + random.nextInt(10);
			return new BlockPos(k, m, l);
		});
	}
}
