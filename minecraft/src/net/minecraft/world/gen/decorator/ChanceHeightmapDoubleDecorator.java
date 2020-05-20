package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ChanceHeightmapDoubleDecorator extends Decorator<ChanceDecoratorConfig> {
	public ChanceHeightmapDoubleDecorator(Codec<ChanceDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, ChanceDecoratorConfig chanceDecoratorConfig, BlockPos blockPos
	) {
		if (random.nextFloat() < 1.0F / (float)chanceDecoratorConfig.chance) {
			int i = random.nextInt(16) + blockPos.getX();
			int j = random.nextInt(16) + blockPos.getZ();
			int k = worldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, i, j) * 2;
			return k <= 0 ? Stream.empty() : Stream.of(new BlockPos(i, random.nextInt(k), j));
		} else {
			return Stream.empty();
		}
	}
}
