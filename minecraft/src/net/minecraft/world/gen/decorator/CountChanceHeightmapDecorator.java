package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CountChanceHeightmapDecorator extends Decorator<CountChanceDecoratorConfig> {
	public CountChanceHeightmapDecorator(Function<Dynamic<?>, ? extends CountChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, CountChanceDecoratorConfig countChanceDecoratorConfig, BlockPos blockPos
	) {
		return IntStream.range(0, countChanceDecoratorConfig.count).filter(i -> random.nextFloat() < countChanceDecoratorConfig.chance).mapToObj(i -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = worldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k);
			return new BlockPos(j, l, k);
		});
	}
}
