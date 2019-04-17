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
	public ChorusPlantDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_14373(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		int i = random.nextInt(5);
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = iWorld.getTopPosition(Heightmap.Type.field_13197, blockPos.add(j, 0, k)).getY();
			if (l > 0) {
				int m = l - 1;
				return new BlockPos(blockPos.getX() + j, m, blockPos.getZ() + k);
			} else {
				return null;
			}
		}).filter(Objects::nonNull);
	}
}
