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

public class DarkOakTreeDecorator extends Decorator<NopeDecoratorConfig> {
	public DarkOakTreeDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		return IntStream.range(0, 16).mapToObj(i -> {
			int j = i / 4;
			int k = i % 4;
			int l = j * 4 + 1 + random.nextInt(3) + blockPos.getX();
			int m = k * 4 + 1 + random.nextInt(3) + blockPos.getZ();
			int n = worldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, l, m);
			return new BlockPos(l, n, m);
		});
	}
}
