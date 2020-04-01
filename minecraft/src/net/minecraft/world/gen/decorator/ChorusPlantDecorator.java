package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class ChorusPlantDecorator extends Decorator<NopeDecoratorConfig> {
	public ChorusPlantDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function, Function<Random, ? extends NopeDecoratorConfig> function2) {
		super(function, function2);
	}

	public Stream<BlockPos> getPositions(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		int i = random.nextInt(5);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16) + blockPos.getX();
			int k = random.nextInt(16) + blockPos.getZ();
			int l = iWorld.getTopY(Heightmap.Type.MOTION_BLOCKING, j, k);
			if (l > 0) {
				int m = l - 1;
				return new BlockPos(j, m, k);
			} else {
				return null;
			}
		}).filter(Objects::nonNull);
	}
}
