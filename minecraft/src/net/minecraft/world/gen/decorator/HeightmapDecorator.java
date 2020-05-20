package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HeightmapDecorator extends Decorator<NopeDecoratorConfig> {
	public HeightmapDecorator(Codec<NopeDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		int i = random.nextInt(16) + blockPos.getX();
		int j = random.nextInt(16) + blockPos.getZ();
		int k = worldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, i, j);
		return Stream.of(new BlockPos(i, k, j));
	}
}
