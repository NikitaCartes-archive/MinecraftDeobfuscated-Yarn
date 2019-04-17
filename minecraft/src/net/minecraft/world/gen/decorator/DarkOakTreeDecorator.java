package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class DarkOakTreeDecorator extends Decorator<NopeDecoratorConfig> {
	public DarkOakTreeDecorator(Function<Dynamic<?>, ? extends NopeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_14524(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, NopeDecoratorConfig nopeDecoratorConfig, BlockPos blockPos
	) {
		return IntStream.range(0, 16).mapToObj(i -> {
			int j = i / 4;
			int k = i % 4;
			int l = j * 4 + 1 + random.nextInt(3);
			int m = k * 4 + 1 + random.nextInt(3);
			return iWorld.getTopPosition(Heightmap.Type.field_13197, blockPos.add(l, 0, m));
		});
	}
}
