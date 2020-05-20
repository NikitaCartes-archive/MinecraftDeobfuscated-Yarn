package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CountHeight64Decorator extends Decorator<CountDecoratorConfig> {
	public CountHeight64Decorator(Codec<CountDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, CountDecoratorConfig countDecoratorConfig, BlockPos blockPos
	) {
		return IntStream.range(0, countDecoratorConfig.count).mapToObj(i -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = 64;
			return new BlockPos(j, 64, k);
		});
	}
}
