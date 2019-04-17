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

public class CountChanceHeightmapDecorator extends Decorator<CountChanceDecoratorConfig> {
	public CountChanceHeightmapDecorator(Function<Dynamic<?>, ? extends CountChanceDecoratorConfig> function) {
		super(function);
	}

	public Stream<BlockPos> method_15902(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		CountChanceDecoratorConfig countChanceDecoratorConfig,
		BlockPos blockPos
	) {
		return IntStream.range(0, countChanceDecoratorConfig.count).filter(i -> random.nextFloat() < countChanceDecoratorConfig.chance).mapToObj(i -> {
			int j = random.nextInt(16);
			int k = random.nextInt(16);
			return iWorld.getTopPosition(Heightmap.Type.field_13197, blockPos.add(j, 0, k));
		});
	}
}
