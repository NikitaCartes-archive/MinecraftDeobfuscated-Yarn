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

public class HeightmapRangeDecorator extends Decorator<HeightmapRangeDecoratorConfig> {
	public HeightmapRangeDecorator(Function<Dynamic<?>, ? extends HeightmapRangeDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15945(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		HeightmapRangeDecoratorConfig heightmapRangeDecoratorConfig,
		BlockPos blockPos
	) {
		int i = random.nextInt(heightmapRangeDecoratorConfig.max - heightmapRangeDecoratorConfig.min) + heightmapRangeDecoratorConfig.min;
		return IntStream.range(0, i).mapToObj(ix -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			int l = iWorld.getTop(Heightmap.Type.field_13195, blockPos.getX() + j, blockPos.getZ() + k);
			return new BlockPos(blockPos.getX() + j, l, blockPos.getZ() + k);
		});
	}
}
